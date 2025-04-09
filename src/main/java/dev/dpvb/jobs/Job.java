package dev.dpvb.jobs;

public abstract class Job implements Runnable {

    /**
     * Get the initial delay in seconds before running this job.
     * @return The initial delay in milliseconds.
     */
    protected abstract long getInitialDelay();

    /**
     * Get the delay in seconds between executions.
     * @return The delay in milliseconds.
     */
    protected abstract long getDelay();



}
