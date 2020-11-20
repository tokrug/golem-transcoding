package pl.krug.yagna.transcoding.job;

import lombok.*;
import pl.krug.yagna.transcoding.job.event.TranscodingEvent;

import java.util.List;

@Getter
@Builder
public class TranscodingJobDto {

    private final String id;
    private final String filename;
    private final String error;
    private final boolean completed;
    private final List<String> completedFormats;

    public static TranscodingJobDto of(TranscodingJob job) {
        return TranscodingJobDto.builder()
                .id(job.getId())
                .completed(job.isCompleted())
                .completedFormats(job.getCompletedFormats())
                .filename(job.getFilename())
                .error(job.getError())
                .build();
    }

}
