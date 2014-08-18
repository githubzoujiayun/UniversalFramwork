package com.universal.framwork.job;

import android.content.Context;

/**
 * Interface to supply custom {@link JobQueue}s for JobManager
 */
public interface QueueFactory {
    public JobQueue createJobQueue(Context context, Long sessionId, String id);
}