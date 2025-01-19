package dev.dpvb.jobs;

public abstract class Job implements Runnable {

    /**
     * Get the initial delay in seconds before running this job.
     * @return The initial delay in seconds.
     */
    protected abstract long getInitialDelay();

    /**
     * Get the delay in seconds between executions.
     * @return The initial delay in seconds.
     */
    protected abstract long getDelay();



}
