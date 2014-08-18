package com.universal.framwork.job;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.universal.framwork.util.LogUtil;



abstract public class BaseJob<T>{
    public static final String TAG="BaseJob";
  	public static final int DEFAULT_RETRY_LIMIT = 20;
    private String groupId;
    private transient int currentRunCount;
    private boolean canceled;
    private Long id;

    protected BaseJob(String groupId) {
        this.groupId = groupId;
    }

    /**
     * called when the job is added to disk and committed.
     * this means job will eventually run. this is a good time to update local database and dispatch events
     * Changes to this class will not be preserved if your job is persistent !!!
     * Also, if your app crashes right after adding the job, {@code onRun} might be called without an {@code onAdded} call
     */
    abstract public void onAdded();

    /**
     * The actual method that should to the work
     * It should finish w/o any exception. If it throws any exception, {@code shouldReRunOnThrowable} will be called to
     * decide either to dismiss the job or re-run it.
     * @throws Throwable
     */
    abstract public T onRun() ;
    
    /**
     * UI Thread get result from onRun method
     * @param result
     */
    abstract public void onSuccess(T result);

    /**
     * called when a job is cancelled.
     */
    abstract protected void onCancel();
    
    
    abstract protected void onStart();
    
    abstract protected void onFailure(Exception e);

    /**
     * if {@code onRun} method throws an exception, this method is called.
     * return true if you want to run your job again, return false if you want to dismiss it. If you return false,
     * onCancel will be called.
     */
    abstract protected boolean shouldReRunOnThrowable(Throwable throwable);
    
    /**
     * cancel the job.and onCancel
     */
    protected void cancelJob() {
    	canceled = true;
	}

    /**
     * Runs the job and catches any exception
     * @param currentRunCount
     * @return
     */
    public final boolean safeRun(int currentRunCount,JobDelivery delivery) {
        this.currentRunCount = currentRunCount;
            LogUtil.d("yzy",String.format("running job %s",  this.getClass().getSimpleName()));
        boolean reRun = false;
        boolean failed = false;
        Exception exception=null;
        T result = null;
        if(canceled){
        	delivery.delivery(JobDelivery.MSG_TASK_CANCLE,this, result);
        	return true;
        }
        try {
            delivery.delivery(JobDelivery.MSG_TASK_START,this, result);
            result = onRun();
        } catch (Exception e) {
            failed = true;
            exception=e;
            reRun = currentRunCount < getRetryLimit();
            if(reRun) {
                try {
                    reRun = shouldReRunOnThrowable(e);
                } catch (Throwable t2) {
                    LogUtil.e("yzy", "shouldReRunOnThrowable did th row an exception");
                }
            }
        } finally {
        	if(canceled){
            	delivery.delivery(JobDelivery.MSG_TASK_CANCLE,this, result);
            	return true;
            }
            if (reRun) {
                return false;
            } else if (failed) {
                try {
                	delivery.delivery(JobDelivery.MSG_TASK_FAILURE,this, exception);
                } catch (Throwable ignored) {
                }
                
            }else{
            	delivery.delivery(JobDelivery.MSG_TASK_SUCCESS,this, result);
            }
        }
        return true;
    }

    /**
     * before each run, JobManager sets this number. Might be useful for the {@link tv.pps.modules.job.BaseJob#onRun()}
     * method
     * @return
     */
    protected int getCurrentRunCount() {
        return currentRunCount;
    }


    /**
     * Some jobs may require being run synchronously. For instance, if it is a job like sending a comment, we should
     * never run them in parallel (unless they are being sent to different conversations).
     * By assigning same groupId to jobs, you can ensure that that type of jobs will be run in the order they were given
     * (if their priority is the same).
     * @return
     */
    public final String getRunGroupId() {
        return groupId;
    }

    /**
     * By default, jobs will be retried {@code DEFAULT_RETRY_LIMIT}  times.
     * If job fails this many times, onCancel will be called w/o calling {@code shouldReRunOnThrowable}
     * @return
     */
    protected int getRetryLimit() {
        return DEFAULT_RETRY_LIMIT;
    }

	public boolean isCanceled() {
		return canceled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
