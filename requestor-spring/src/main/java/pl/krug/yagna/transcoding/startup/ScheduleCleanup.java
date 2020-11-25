package pl.krug.yagna.transcoding.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.krug.yagna.transcoding.configuration.ScriptConfiguration;
import pl.krug.yagna.transcoding.job.TranscodingJob;
import pl.krug.yagna.transcoding.job.TranscodingJobRepository;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleCleanup {

    private final TranscodingJobRepository repository;
    private final ScriptConfiguration scriptConfiguration;

    @EventListener
    public void handleContextStart(ApplicationReadyEvent are) {
        log.info("Registering data cleanup job");
        Flux.interval(Duration.ofMinutes(1))
                .doOnNext(time -> clean())
                .subscribeOn(Schedulers.parallel())
                .subscribe((number) -> {}, error -> log.error("Error occurred during data cleaning.", error));
    }

    private void clean() {
        log.info("Commencing cleanup");
        long hourInThePast = Instant.now().minus(scriptConfiguration.getDataRetention()).getEpochSecond();
        List<TranscodingJob> oldJobs = repository.getCompletedJobsOlderThan(hourInThePast);
        oldJobs.forEach(this::cleanJob);
        log.info("Commencing completed");
    }

    private void cleanJob(TranscodingJob job) {
        log.info("Started removal of job data: {}", job.getId());
        cleanOutput(job);
        cleanDatabase(job);
        log.info("Cleaned job: {}", job.getId());
    }

    private void cleanOutput(TranscodingJob job) {
        try {
            Path outputPath = Paths.get(calculateOutputFolder(job.getId()));
            FileUtils.deleteDirectory(outputPath.toFile());
        } catch (IOException e) {
            log.error("Could not clean the output for job: " + job.getId(), e);
        }
    }

    private void cleanDatabase(TranscodingJob job) {
        repository.remove(job);
    }

    private String calculateOutputFolder(String jobId) {
        return scriptConfiguration.getScriptLocation() + "/output/" + jobId;
    }

}
