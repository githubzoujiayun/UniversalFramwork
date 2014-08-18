package com.universal.framwork.job.queue;


import java.util.Comparator;

import com.universal.framwork.job.JobHolder;

/**
 * A job holder comparator that checks time before checking anything else
 */
public class ConsistentTimedComparator implements Comparator<JobHolder> {
    final Comparator<JobHolder> baseComparator;

    public ConsistentTimedComparator(Comparator<JobHolder> baseComparator) {
        this.baseComparator = baseComparator;
    }

    @Override
    public int compare(JobHolder jobHolder, JobHolder jobHolder2) {
        if(jobHolder.getDelayUntilNs() < jobHolder2.getDelayUntilNs()) {
            return -1;
        } else if(jobHolder.getDelayUntilNs() > jobHolder2.getDelayUntilNs()) {
            return 1;
        }
        return baseComparator.compare(jobHolder, jobHolder2);
    }
}
