package com.universal.framwork.job;

/**
 * Identifies the current status of a job if it is in the queue
 */
public enum JobStatus {
    WAITING_NOT_READY,
    WAITING_READY,
    RUNNING,
    UNKNOWN
}
