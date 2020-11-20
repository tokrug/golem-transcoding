package pl.krug.yagna.transcoding.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.krug.yagna.transcoding.commandline.RequestorScript;
import pl.krug.yagna.transcoding.controller.UploadedFile;
import pl.krug.yagna.transcoding.job.event.*;
import pl.krug.yagna.transcoding.job.TranscodingJob;
import pl.krug.yagna.transcoding.job.TranscodingJobDto;
import pl.krug.yagna.transcoding.job.TranscodingJobRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class TranscodingApi {

    private final TranscodingJobRepository repository;
    private final EventStream eventStream;
    private final RequestorScript requestorScript;

    public TranscodingJobDto start(UploadedFile uploadedFile) {
        JobCreatedEvent event = JobCreatedEvent.of(uploadedFile.getName(), uploadedFile.getPath().toString());
        repository.add(event);
        TranscodingJob job = repository.getById(event.getJobId());
        startTranscoding(job);
        return TranscodingJobDto.of(job);
    }

    public Flux<TranscodingJobDto> getUpdates(String id) {
        return eventStream.getByJobId(id)
                .map(event -> repository.getById(id))
                .startWith(Mono.defer(() -> Mono.just(repository.getById(id))))
                .map(TranscodingJobDto::of);
    }

    public TranscodingJobDto getState(String id) {
        TranscodingJob job = repository.getById(id);
        return TranscodingJobDto.of(job);
    }

    private void startTranscoding(TranscodingJob job) {
        requestorScript.startTranscoding(job)
                .doOnNext(event -> handleEvent(job, event))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe((item) -> {}, error -> log.error("Error occurred when executing the transcoding script", error));
    }


    private void handleEvent(TranscodingJob job, TranscodingEvent event) {
        log.info("Received event: {}", event);
        repository.add(event);
        if (event instanceof TranscodingFinishedEvent) {
            removeTemporaryFile(job.getInputPath());
        }
        eventStream.emitEvent(event);
    }

    private void removeTemporaryFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

