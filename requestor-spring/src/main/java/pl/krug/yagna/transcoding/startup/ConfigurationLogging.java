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
public class ConfigurationLogging {

    private final ScriptConfiguration scriptConfiguration;

    @EventListener
    public void handleContextStart(ApplicationReadyEvent are) {
        log.info("Application started with the following configuration");
        log.info("{}", scriptConfiguration);
    }

}
