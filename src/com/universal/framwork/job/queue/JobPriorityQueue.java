package com.universal.framwork.job.queue;


import java.util.Collection;
import java.util.Comparator;

import com.universal.framwork.job.JobHolder;
import com.universal.framwork.job.JobManager;
import com.universal.framwork.job.JobQueue;
import com.universal.framwork.util.LogUtil;

public class JobPriorityQueue implements JobQueue {
    private long jobIdGenerator = Integer.MIN_VALUE;
    //TODO implement a more efficient priority queue where we can mark jobs as removed but don't remove for real
    //private NetworkAwarePriorityQueue jobs;
    private TimeAwarePriorityQueue jobs;
    private final String id;
    private final long sessionId;

    public JobPriorityQueue(long sessionId, String id) {
        this.id = id;
        this.sessionId = sessionId;
        jobs = new TimeAwarePriorityQueue(5, jobComparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized long insert(JobHolder jobHolder) {
        jobIdGenerator++;
        jobHolder.setId(jobIdGenerator);
        jobHolder.getBaseJob().setId(jobIdGenerator);
        jobs.offer(jobHolder);
        return jobHolder.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long insertOrReplace(JobHolder jobHolder) {
        remove(jobHolder);
        jobHolder.setRunningSessionId(JobManager.NOT_RUNNING_SESSION_ID);
        jobs.offer(jobHolder);
        return jobHolder.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(JobHolder jobHolder) {
        jobs.remove(jobHolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        return jobs.size();
    }

    @Override
    public int countReadyJobs(Collection<String> excludeGroups) {
        long now=System.nanoTime();
        return jobs.countReadyJobs(now, excludeGroups).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobHolder nextJobAndIncRunCount(Collection<String> excludeGroups) {
        JobHolder jobHolder = jobs.peek(excludeGroups);

        if (jobHolder != null) {
            //check if job can run
            if(jobHolder.getDelayUntilNs() > System.nanoTime()) {
                LogUtil.e("yzy", "job is get success but is has delay");
                jobHolder = null;
            } else {
                jobHolder.setRunningSessionId(sessionId);
                jobHolder.setRunCount(jobHolder.getRunCount() + 1);
                jobs.remove(jobHolder);
            }
        }
        return jobHolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getNextJobDelayUntilNs() {
        JobHolder next = jobs.peek(null);
        return next == null ? null : next.getDelayUntilNs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        jobs.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobHolder findJobById(long id) {
        return jobs.findById(id);
    }

    public final Comparator<JobHolder> jobComparator = new Comparator<JobHolder>() {
        @Override
        public int compare(JobHolder holder1, JobHolder holder2) {
            //we should not check delay here. TimeAwarePriorityQueue does it for us.
            //high priority first
            int cmp = compareInt(holder1.getPriority(), holder2.getPriority());
            if(cmp != 0) {
                return cmp;
            }

            //if run counts are also equal, older job first
            cmp = -compareLong(holder1.getCreatedNs(), holder2.getCreatedNs());
            if(cmp != 0) {
                return cmp;
            }

            //if jobs were created at the same time, smaller id first
            return -compareLong(holder1.getId(), holder2.getId());
        }
    };

    private static int compareInt(int i1, int i2) {
        if (i1 > i2) {
            return -1;
        }
        if (i2 > i1) {
            return 1;
        }
        return 0;
    }

    private static int compareLong(long l1, long l2) {
        if (l1 > l2) {
            return -1;
        }
        if (l2 > l1) {
            return 1;
        }
        return 0;
    }


}
