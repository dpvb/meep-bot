package dev.dpvb.jobs;

import net.dv8tion.jda.api.JDA;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobManager {

    private static JobManager INSTANCE;
    private Set<Job> jobs = new HashSet<>();

    private JobManager(JDA jda) {
        jobs.add(new WordleWinnerJob(jda));
        scheduleJobs();
    }

    public static void init(JDA jda) {
        if (INSTANCE == null) {
            INSTANCE = new JobManager(jda);
        }
    }

    private void scheduleJobs() {
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        jobs.forEach(job -> {
            long initialDelay = job.getInitialDelay();
            long delay = job.getDelay();
            exec.scheduleWithFixedDelay(job, initialDelay, delay, TimeUnit.MILLISECONDS);
        });
    }

}
