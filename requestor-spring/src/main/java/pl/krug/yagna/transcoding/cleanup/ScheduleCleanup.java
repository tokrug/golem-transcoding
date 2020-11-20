package pl.krug.yagna.transcoding.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.krug.yagna.transcoding.configuration.ScriptConfiguration;
import pl.krug.yagna.transcoding.job.TranscodingJob;
import pl.krug.yagna.transcoding.job.TranscodingJobRepository;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
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
    public void handleContextStart(ContextStartedEvent cse) {
        Flux.interval(Duration.ofHours(1))
                .doOnNext(time -> clean())
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }

    private void clean() {
        long hourInThePast = Instant.now().minus(Duration.ofHours(1)).getEpochSecond();
        List<TranscodingJob> oldJobs = repository.getCompletedJobsOlderThan(hourInThePast);
        oldJobs.forEach(this::cleanJob);
    }

    private void cleanJob(TranscodingJob job) {
        cleanOutput(job);
        cleanInput(job);
        cleanDatabase(job);
    }

    private void cleanOutput(TranscodingJob job) {
        try {
            Path outputPath = Paths.get(calculateOutputFolder(job.getId()));
            FileUtils.deleteDirectory(outputPath.toFile());
        } catch (IOException e) {
            log.error("Could not clean the output for job: " + job.getId(), e);
        }
    }

    private void cleanInput(TranscodingJob job) {
        try {
            Files.deleteIfExists(Paths.get(calculateInputFilePath(job.getInputPath())));
        } catch (IOException e) {
            log.error("Could not clean the input for job: " + job.getId(), e);
        }
    }

    private void cleanDatabase(TranscodingJob job) {
        repository.remove(job);
    }

    private String calculateOutputFolder(String jobId) {
        return scriptConfiguration.getScriptLocation() + "/output/" + jobId;
    }

    private String calculateInputFilePath(String inputId) {
        return scriptConfiguration.getInputFileLocation() + "/" + inputId;
    }

}
