package pl.krug.yagna.transcoding.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import pl.krug.yagna.transcoding.job.TranscodingApi;
import pl.krug.yagna.transcoding.configuration.ScriptConfiguration;
import pl.krug.yagna.transcoding.job.TranscodingJobDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TranscodingController {

    private final TranscodingApi transcodingApi;
    private final ScriptConfiguration configuration;

    @PostMapping(value = "/api/transcode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> uploadFile(@RequestPart("files") FilePart filePart, @RequestHeader("Content-Length") long contentLength) {
        try {
            Path inputFilePath = Files.createFile(Paths.get(configuration.getInputFileLocation() + "/" + UUID.randomUUID().toString()));
            if (!isSizeValid(contentLength)) {
                return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(UnprocessableEntityError.SIZE_TOO_BIG));
            }
            return filePart.transferTo(inputFilePath)
                    .then(Mono.defer(() -> {
                        UploadedFile uploadedFile = UploadedFile.of(inputFilePath, filePart.filename());
                        TranscodingJobDto start = transcodingApi.start(uploadedFile);
                        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(start));
                    }));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @GetMapping(value = "/api/transcode/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TranscodingJobDto> getTranscodeJobUpdates(@PathVariable String id) {
        return transcodingApi.getUpdates(id);
    }

    @GetMapping(value = "/api/transcode/{id}")
    public TranscodingJobDto getTranscodeJob(@PathVariable String id) {
        return transcodingApi.getState(id);
    }

    @GetMapping(value = "/output/{jobId}/output.{extension}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Resource getOutput(@PathVariable String jobId, @PathVariable String extension) {
        if (jobId.contains(".") || extension.contains(".")) {
            throw new BadRequestException();
        }
        String filename = configuration.getScriptLocation() + "/output/" + jobId + "/output." + extension;
        return new FileSystemResource(filename);
    }

    private boolean isSizeValid(long length) {
        return length <= 1024 * 1024 * 10;
    }


}
