package com.universal.framwork.job;

import android.content.Context;
import android.net.ConnectivityManager;

import com.universal.framwork.job.queue.JobPriorityQueue;

/**
 * 用来配置JobManager
 * com.universal.framwork.job.config.JobConfig
 * @author yuanzeyao <br/>
 * create at 2014年7月22日 下午1:10:30
 */
public class JobConfig {
    public static final String DEFAULT_ID = "default_job_manager";
    public static final int DEFAULT_THREAD_KEEP_ALIVE_SECONDS = 15;
    public static final int DEFAULT_LOAD_FACTOR_PER_CONSUMER = 3;
    public static final int MAX_CONSUMER_COUNT = 5;
    public static final int MIN_CONSUMER_COUNT = 0;

    private String id = DEFAULT_ID;
    private int maxConsumerCount = MAX_CONSUMER_COUNT;
    private int minConsumerCount = MIN_CONSUMER_COUNT;
    private int consumerKeepAlive = DEFAULT_THREAD_KEEP_ALIVE_SECONDS;
    private int loadFactor = DEFAULT_LOAD_FACTOR_PER_CONSUMER;
    private QueueFactory queueFactory;

    private JobConfig(){
        //use builder instead
    }

    public String getId() {
        return id;
    }

    public QueueFactory getQueueFactory() {
        return queueFactory;
    }


    public int getConsumerKeepAlive() {
        return consumerKeepAlive;
    }

    public int getMaxConsumerCount() {
        return maxConsumerCount;
    }

    public int getMinConsumerCount() {
        return minConsumerCount;
    }


    public int getLoadFactor() {
        return loadFactor;
    }

    public static final class Builder {
        private JobConfig configuration;
        private Context appContext;
        public Builder(Context context) {
            this.configuration = new JobConfig();
            appContext = context.getApplicationContext();
        }

        /**
         * provide and ID for this job manager to be used while creating persistent queue. it is useful if you are going to
         * create multiple instances of it.
         * default id is {@value #DEFAULT_ID}
         * @param id if you have multiple instances of job manager, you should provide an id to distinguish their persistent files.
         */
        public Builder id(String id) {
            configuration.id = id;
            return this;
        }

        /**
         * When JobManager runs out of `ready` jobs, it will keep consumers alive for this duration. it defaults to {@value #DEFAULT_THREAD_KEEP_ALIVE_SECONDS}
         * @param keepAlive in seconds
         */
        public Builder consumerKeepAlive(int keepAlive) {
            configuration.consumerKeepAlive = keepAlive;
            return this;
        }

        /**
         * JobManager needs one persistent and one non-persistent {@link JobQueue} to function.
         * By default, it will use {@link SqliteJobQueue} and {@link JobPriorityQueue}
         * You can provide your own implementation if they don't fit your needs. Make sure it passes all tests in
         * {@link JobQueueTestBase} to ensure it will work fine.
         * @param queueFactory your custom queue factory.
         */
        public Builder queueFactory(QueueFactory queueFactory) {
            if(configuration.queueFactory != null) {
                throw new RuntimeException("already set a queue factory. This might happen if you've provided a custom " +
                        "job serializer");
            }
            configuration.queueFactory = queueFactory;
            return this;
        }


        /**
         * # of max consumers to run concurrently. defaults to {@value #MAX_CONSUMER_COUNT}
         * @param count
         */
        public Builder maxConsumerCount(int count) {
            configuration.maxConsumerCount = count;
            return this;
        }

        /**
         * you can specify to keep minConsumers alive even if there are no ready jobs. defaults to {@value #MIN_CONSUMER_COUNT}
         * @param count
         */
        public Builder minConsumerCount(int count) {
            configuration.minConsumerCount = count;
            return this;
        }

        /**
         * calculated by # of jobs (running+waiting) per thread
         * for instance, at a given time, if you have two consumers and 10 jobs in waiting queue (or running right now), load is
         * (10/2) =5
         * defaults to {@value #DEFAULT_LOAD_FACTOR_PER_CONSUMER}
         * @param loadFactor
         */
        public Builder loadFactor(int loadFactor) {
            configuration.loadFactor = loadFactor;
            return this;
        }

        public JobConfig build() {
            if(configuration.queueFactory == null) {
                configuration.queueFactory = new JobManager.DefaultQueueFactory();
            }
            return configuration;
        }
    }
}
