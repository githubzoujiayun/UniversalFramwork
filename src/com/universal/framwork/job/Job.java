package com.universal.framwork.job;

import com.universal.framwork.job.Params.JobPriority;



/**
 * Base class for all of your jobs. If you were using {@link BaseJob}, please
 * move to this instance since BaseJob will be removed from the public api.
 */
//@SuppressWarnings("deprecation")
abstract public class Job<T> extends BaseJob<T>{
	private transient int priority;
	private transient long delayInMs;
	private Listener<T> listener;

	public interface Listener<T>{
		void onSuccess(T result);
		void onCancel();
		void onStart();
		void onFailure(Exception e);
	}

	protected Job() {
		super(null);
		this.priority = JobPriority.NORMAL.ordinal();
		this.delayInMs = 0;
	}

	protected Job(Listener<T> listener) {
		super(null);
		this.priority = JobPriority.NORMAL.ordinal();
		this.delayInMs = 0;
		this.listener = listener;
	}

	protected Job(Params params) {
		super(params.getGroupId());
		this.priority = params.getPriority();
		this.delayInMs = params.getDelayMs();
	}

	protected Job(Params params, Listener<T> listener) {
		this(params);
		this.listener = listener;
	}

	/**
	 * used by {@link JobManager} to assign proper priority at the time job is
	 * added. This field is not preserved!
	 * 
	 * @return priority (higher = better)
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * used by {@link JobManager} to assign proper delay at the time job is
	 * added. This field is not preserved!
	 * 
	 * @return delay in ms
	 */
	public final long getDelayInMs() {
		return delayInMs;
	}

	public void cancel() {
		cancelJob();
	}

	@Override
	public void onAdded() {
	}

	@Override
	public void onSuccess(T result) {
		if (listener != null) {
			listener.onSuccess(result);
		}
	}
	
	@Override
	protected void onStart()
	{
	  if(listener!=null)
	  {
	    listener.onStart();
	  }
	}
	
	@Override
	protected void onFailure(Exception e)
	{
	  if(listener!=null)
	  {
	    listener.onFailure(e);
	  }
	}
	
	
	
	protected boolean shouldReRunOnThrowable(Throwable throwable){
		return false;
	}

	@Override
	protected void onCancel() {
		if (listener != null) {
			listener.onCancel();
		}
	}
}
