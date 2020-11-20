package pl.krug.yagna.transcoding.job.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class JobCreatedEvent extends TranscodingEvent {

    private String inputPath;
    private String filename;

    JobCreatedEvent(String jobId, String inputPath, String filename) {
        super(jobId);
        this.inputPath = inputPath;
        this.filename = filename;
    }

    public static JobCreatedEvent of(String name, String inputPath) {
        return new JobCreatedEvent(UUID.randomUUID().toString(), inputPath, name);
    }
}
