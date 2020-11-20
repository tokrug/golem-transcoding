package pl.krug.yagna.transcoding.job.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString(callSuper = true)
public class SubtaskFinishedEvent extends TranscodingEvent {

    private String extension;

    SubtaskFinishedEvent(String jobId, String extension) {
        super(jobId);
        this.extension = extension;
    }

    public static SubtaskFinishedEvent of(String jobId, String extension) {
        return new SubtaskFinishedEvent(jobId, extension);
    }

}
