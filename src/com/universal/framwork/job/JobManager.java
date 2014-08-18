package com.universal.framwork.job;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

import com.universal.framwork.job.queue.CachedJobQueue;
import com.universal.framwork.job.queue.JobConsumerExecutor;
import com.universal.framwork.job.queue.JobPriorityQueue;
import com.universal.framwork.util.LogUtil;


public class JobManager  {
    public static final String TAG="JobManager";
    public static final long NS_PER_MS = 1000000;
    public static final long NOT_RUNNING_SESSION_ID = Long.MIN_VALUE;
    public static final long NOT_DELAYED_JOB_DELAY = Long.MIN_VALUE;
    private final long sessionId;
    private boolean running;

    private final Context appContext;
    private final JobQueue mJobQueue;
    private final CopyOnWriteGroupSet runningJobGroups;
    private final JobConsumerExecutor jobConsumerExecutor;
    private final Object addJobLock = new Object();
    private final ConcurrentHashMap<Long, CountDownLatch> onAddLocks;
    private final ScheduledExecutorService timedExecutor;
    private final Object getJobLock = new Object();
    private final JobDelivery jobDelivery;
    private static JobManager jobManager;
    
    
    public static JobManager build(Context context,JobConfig configuration) {
    	jobManager = new JobManager(context, configuration);
    	return jobManager;
    }
    
    /**
     * get jobManager
     * @return
     */
    public static JobManager get(){
    	return jobManager;
    }

    /**
     * Default constructor that will create a JobManager with 1 {@link SqliteJobQueue} and 1 {@link JobPriorityQueue}
     * @param context job manager will use applicationContext.
     */
    private JobManager(Context context) {
        this(context, "default");
    }


    /**
     * Default constructor that will create a JobManager with a default {@link JobConfig}
     * @param context application context
     * @param id an id that is unique to this JobManager
     */
    private JobManager(Context context, String id) {
        this(context, new JobConfig.Builder(context).id(id).build());
    }

    /**
     *
     * @param context used to acquire ApplicationContext
     * @param config
     */
    private JobManager(Context context, JobConfig config) {
        appContext = context.getApplicationContext();
        running = true;
        runningJobGroups = new CopyOnWriteGroupSet();
        sessionId = System.nanoTime();
        this.mJobQueue = config.getQueueFactory().createJobQueue(context, sessionId, config.getId());
        onAddLocks = new ConcurrentHashMap<Long, CountDownLatch>();

        jobDelivery = new JobDelivery();
        //is important to initialize consumers last so that they can start running
        jobConsumerExecutor = new JobConsumerExecutor(config,consumerContract);
        timedExecutor = Executors.newSingleThreadScheduledExecutor();
        start();
    }


    /**
     * Stops consuming jobs. Currently running jobs will be finished but no new jobs will be run.
     */
    public void stop() {
        running = false;
    }

    /**
     * restarts the JobManager. Will create a new consumer if necessary.
     */
    public void start() {
        if(running) {
            return;
        }
        running = true;
        notifyJobConsumer();
    }

    /**
     * returns the # of jobs that are waiting to be executed.
     * This might be a good place to decide whether you should wake your app up on boot etc. to complete pending jobs.
     * @return # of total jobs.
     */
    public int count() {
        int cnt = 0;
        synchronized (mJobQueue) {
            cnt += mJobQueue.count();
        }
        return cnt;
    }

    private int countReadyJobs() {
        //TODO we can cache this
        int total = 0;
        synchronized (mJobQueue) {
            total += mJobQueue.countReadyJobs(runningJobGroups.getSafe());
        }
        return total;
    }

    /**
     * Adds a new Job to the list and returns an ID for it.
     * @param job to add
     * @return id for the job.
     */
    public long addJob(Job job) {
        //noinspection deprecation
        return addJob(job.getPriority(), job.getDelayInMs(), job);
    }

    /**
     * Non-blocking convenience method to add a job in background thread.
     * @see #addJob(Job)
     * @param job job to add
     *
     */
    public void addJobInBackground(Job job) {
        //noinspection deprecation
        addJobInBackground(job.getPriority(), job.getDelayInMs(), job);
    }

    public void addJobInBackground(Job job, /*nullable*/ AsyncAddCallback callback) {
        addJobInBackground(job.getPriority(), job.getDelayInMs(), job, callback);
    }

    //need to sync on related job queue before calling this
    private void addOnAddedLock(ConcurrentHashMap<Long, CountDownLatch> lockMap, long id) {
        lockMap.put(id, new CountDownLatch(1));
    }

    //need to sync on related job queue before calling this
    private void waitForOnAddedLock(ConcurrentHashMap<Long, CountDownLatch> lockMap, long id) {
        CountDownLatch latch = lockMap.get(id);
        if(latch == null) {
            return;
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            LogUtil.e(TAG, "could not wait for onAdded lock");
        }
    }

    //need to sync on related job queue before calling this
    private void clearOnAddedLock(ConcurrentHashMap<Long, CountDownLatch> lockMap, long id) {
        CountDownLatch latch = lockMap.get(id);
        if(latch != null) {
            latch.countDown();
        }
        lockMap.remove(id);
    }

    /**
     * checks next available job and returns when it will be available (if it will, otherwise returns {@link Long#MAX_VALUE})
     * also creates a timer to notify listeners at that time
     * @return time wait until next job (in milliseconds)
     */
    private long ensureConsumerWhenNeeded() {
        //this method is called when there are jobs but job consumer was not given any
        //this may happen in a race condition or when the latest job is a delayed job
        Long nextRunNs;
        synchronized (mJobQueue) {
            nextRunNs = mJobQueue.getNextJobDelayUntilNs();
        }
        if(nextRunNs != null) {
            //to avoid overflow, we need to check equality first
            if(nextRunNs <= System.nanoTime()) {
                notifyJobConsumer();
                return 0L;
            }
            long diff = (long)Math.ceil((double)(nextRunNs - System.nanoTime()) / NS_PER_MS);
            ensureConsumerOnTime(diff);
            return diff;
        }
        return Long.MAX_VALUE;
    }

    private void notifyJobConsumer() {
        synchronized (addJobLock) {
            addJobLock.notifyAll();
        }
        jobConsumerExecutor.considerAddingConsumer();
    }

    private final Runnable notifyRunnable = new Runnable() {
        @Override
        public void run() {
            notifyJobConsumer();
        }
    };

    private void ensureConsumerOnTime(long waitMs) {
        timedExecutor.schedule(notifyRunnable, waitMs, TimeUnit.MILLISECONDS);
    }

    private JobHolder getNextJob() {
        JobHolder jobHolder;
        synchronized (getJobLock) {
            final Collection<String> runningJobIds = runningJobGroups.getSafe();
            synchronized (mJobQueue) {
                jobHolder = mJobQueue.nextJobAndIncRunCount(runningJobIds);
            }
            if(jobHolder == null) {
                return null;
            }
            if(jobHolder.getGroupId() != null) {
                runningJobGroups.add(jobHolder.getGroupId());
            }
        }
        // wait the Job's onAdd method invoke
        waitForOnAddedLock(onAddLocks, jobHolder.getId());
        return jobHolder;
    }

    private void reAddJob(JobHolder jobHolder) {
        LogUtil.d(TAG,String.format("re-adding job %s", jobHolder.getId()) );
            synchronized (mJobQueue) {
                mJobQueue.insertOrReplace(jobHolder);
            }
        if(jobHolder.getGroupId() != null) {
            runningJobGroups.remove(jobHolder.getGroupId());
        }
    }

    /**
     * @param id the ID, returned by the addJob method
     * @param isPersistent Jobs are added to different queues depending on if they are persistent or not. This is necessary
     *                     because each queue has independent id sets.
     * @return
     */
    public JobStatus getJobStatus(long id) {
        if(jobConsumerExecutor.isRunning(id)) {
            return JobStatus.RUNNING;
        }
        JobHolder holder;
        synchronized (mJobQueue) {
                holder = mJobQueue.findJobById(id);
            }
        if(holder == null) {
            return JobStatus.UNKNOWN;
        }
       
        if(holder.getDelayUntilNs() > System.nanoTime()) {
            return JobStatus.WAITING_NOT_READY;
        }

        return JobStatus.WAITING_READY;
    }

    private void removeJob(JobHolder jobHolder) {
       synchronized (mJobQueue) {
                mJobQueue.remove(jobHolder);
            }
        if(jobHolder.getGroupId() != null) {
            runningJobGroups.remove(jobHolder.getGroupId());
        }
    }
    
    public void cancelJob(Job job) 
    {
    	JobHolder jobHolder = mJobQueue.findJobById(job.getId());
    	if(jobHolder == null){
    		if(job != null){
    			job.cancelJob();
    		}
    		return;
    	}
      synchronized (mJobQueue) {
              mJobQueue.remove(jobHolder);
          }
      if(jobHolder.getGroupId() != null) {
          runningJobGroups.remove(jobHolder.getGroupId());
      }
  }
    
    public synchronized void clear() {
        synchronized (mJobQueue) {
            mJobQueue.clear();
            onAddLocks.clear();
        }
        runningJobGroups.clear();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final JobConsumerExecutor.Contract consumerContract = new JobConsumerExecutor.Contract() {
    	
    	
        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public void insertOrReplace(JobHolder jobHolder) {
            reAddJob(jobHolder);
        }

        @Override
        public void removeJob(JobHolder jobHolder) {
            JobManager.this.removeJob(jobHolder);
        }

        @Override
        public JobHolder getNextJob(int wait, TimeUnit waitDuration) {
            //be optimistic
            JobHolder nextJob = JobManager.this.getNextJob();
            if(nextJob != null) {
                return nextJob;
            }
            long start = System.nanoTime();
            long remainingWait = waitDuration.toNanos(wait);
            long waitUntil = remainingWait + start;
            //for delayed jobs,
            long nextJobDelay = ensureConsumerWhenNeeded();
            while (nextJob == null && waitUntil > System.nanoTime()) {
                //keep running inside here to avoid busy loop
                nextJob = running ? JobManager.this.getNextJob() : null;
                if(nextJob == null) {
                   if(nextJobDelay==Long.MAX_VALUE)
                    nextJobDelay = ensureConsumerWhenNeeded();
                    long remaining = waitUntil - System.nanoTime();
                    if(remaining > 0) {
                        //if we can't detect network changes, we won't be notified.
                        //to avoid waiting up to give time, wait in chunks of 500 ms max
                        long maxWait = Math.min(nextJobDelay, TimeUnit.NANOSECONDS.toMillis(remaining));
                        if(maxWait < 1) {
                            continue;//wait(0) will cause infinite wait.
                        }
                            //TODO fix above case where we may wait unnecessarily long if a job is about to become available
                            synchronized (addJobLock) {
                                try {
                                    addJobLock.wait(maxWait);
                                } catch (InterruptedException e) {
                                    LogUtil.e(TAG, "exception while waiting for a new job.");
                                }
                            }
                    }
                }
            }
            return nextJob;
        }

        @Override
        public int countRemainingReadyJobs() {
            //if we can't detect network changes, assume we have network otherwise nothing will trigger a consumer
            //noinspection SimplifiableConditionalExpression
            return countReadyJobs();
        }

		@Override
		public JobDelivery getJobDelivery() {
			return jobDelivery;
		}
    };

    /**
     * <p>Adds a job with given priority and returns the JobId.</p>
     * @param priority Higher runs first
     * @param baseJob The actual job to run
     * @return job id
     */
    public long addJob(int priority, BaseJob baseJob) {
        return addJob(priority, 0, baseJob);
    }

    /**
     * <p>Adds a job with given priority and returns the JobId.</p>
     * @param priority Higher runs first
     * @param delay number of milliseconds that this job should be delayed
     * @param baseJob The actual job to run
     * @return a job id. is useless for now but we'll use this to cancel jobs in the future.
     */
    public long addJob(int priority, long delay, BaseJob baseJob) {
        JobHolder jobHolder = new JobHolder(priority, baseJob, delay > 0 ? System.nanoTime() + delay * NS_PER_MS : NOT_DELAYED_JOB_DELAY, NOT_RUNNING_SESSION_ID);
        long id;
        synchronized (mJobQueue) {
                id = mJobQueue.insert(jobHolder);
                addOnAddedLock(onAddLocks, id);
            }
            LogUtil.d("yzy",String.format("added job id: %d class: %s priority: %d delay: %d group : %s", id, baseJob.getClass().getSimpleName(), priority, delay, baseJob.getRunGroupId()
                ));
        jobHolder.getBaseJob().onAdded();
        synchronized (mJobQueue) {
                clearOnAddedLock(onAddLocks, id);
            }
        notifyJobConsumer();
        return id;
    }

    /**
     * <p>Non-blocking convenience method to add a job in background thread.</p>
     *
     * @see #addJob(int, BaseJob) addJob(priority, job).
     */
    public void addJobInBackground(final int priority, final BaseJob baseJob) {
        timedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                addJob(priority, baseJob);
            }
        });
    }

    /**
     * <p></p>Non-blocking convenience method to add a job in background thread.</p>
     * @see #addJob(int, long, BaseJob) addJob(priority, delay, job).
     */
    public void addJobInBackground(final int priority, final long delay, final BaseJob baseJob) {
        addJobInBackground(priority, delay, baseJob, null);
    }

    protected void addJobInBackground(final int priority, final long delay, final BaseJob baseJob,
        /*nullable*/final AsyncAddCallback callback) {
        final long callTime = System.nanoTime();
        timedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final long runDelay = (System.nanoTime() - callTime) / NS_PER_MS;
                    long id = addJob(priority, Math.max(0, delay - runDelay), baseJob);
                    if(callback != null) {
                        callback.onAdded(id);
                    }
                } catch (Throwable t) {
                    LogUtil.e(TAG, String.format("addJobInBackground received an exception. job class: %s",  baseJob.getClass().getSimpleName() ));
                }
            }
        });
    }


    /**
     * Default implementation of QueueFactory that creates one {@link SqliteJobQueue} and one {@link JobPriorityQueue}
     * both are wrapped inside a {@link CachedJobQueue} to improve performance
     */
    public static class DefaultQueueFactory implements QueueFactory {

        public DefaultQueueFactory() {
        }


        @Override
        public JobQueue createJobQueue(Context context, Long sessionId, String id) {
            return new CachedJobQueue(new JobPriorityQueue(sessionId, id));
        }
    }
}
