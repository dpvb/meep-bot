package dev.dpvb.jobs;

public abstract class Job implements Runnable {

    protected final boolean runAtInitialization;

    /**
     * Create a new job, by default only running at fixed delay.
     */
    protected Job() {
        this(false);
    }

    /**
     * Create a new job, running at a fixed delay and running at initialization if set to do so.
     * @param runAtInitialization Whether to run at start up of the discord bot.
     */
    protected Job(boolean runAtInitialization) {
        this.runAtInitialization = runAtInitialization;
    }

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
