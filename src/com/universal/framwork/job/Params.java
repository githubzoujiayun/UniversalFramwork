package com.universal.framwork.job;

/**
 * BaseJob builder object to have a more readable design.
 * Methods can be chained to have more readable code.
 */
public class Params {
    private String groupId = null;
    private int priority = JobPriority.NORMAL.ordinal();
    private long delayMs;
    
    public enum JobPriority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }
    
    public Params() {
    }

    /**
     *
     * @param priority higher = better
     */
    public Params(JobPriority priority) {
    	if(priority!=null){
    		this.priority = priority.ordinal();
    	}
    }


    /**
     * Sets the group id. Jobs in the same group are guaranteed to execute sequentially.
     * @param groupId which group this job belongs (can be null of course)
     * @return this
     */
    public Params groupBy(String groupId) {
        this.groupId = groupId;
        return this;
    }

    /**
     * Marks the job as persistent. Make sure your job is serializable.
     * @return this
     */
//    public Params persist() {
//        this.persistent = true;
//        return this;
//    }

    /**
     * Delays the job in given ms.
     * @param delayMs .
     * @return this
     */
    public Params delayInMs(long delayMs) {
        this.delayMs = delayMs;
        return this;
    }
    
    /**
     * 
     * @param priority higher = better
     * @return
     */
    public Params setPriority(JobPriority priority) {
    	if(priority!=null){
    		this.priority = priority.ordinal();
    	}
    	return this;
    }


    /**
     * convenience method to set group id.
     * @param groupId
     * @return this
     */
    public Params setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

//    /**
//     * convenience method to set whether {@link JobManager} should persist this job or not.
//     * @param persistent true|false
//     * @return this
//     */
//    public Params setPersistent(boolean persistent) {
//        this.persistent = persistent;
//        return this;
//    }

    /**
     * convenience method to set delay
     * @param delayMs in ms
     * @return this
     */
    public Params setDelayMs(long delayMs) {
        this.delayMs = delayMs;
        return this;
    }


    public String getGroupId() {
        return groupId;
    }


    public int getPriority() {
        return priority;
    }

    public long getDelayMs() {
        return delayMs;
    }
}
