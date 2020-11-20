package pl.krug.yagna.transcoding.job.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.dizitart.no2.objects.Id;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JobCreatedEvent.class, name = "created"),
        @JsonSubTypes.Type(value = SubtaskFinishedEvent.class, name = "subtask-finished"),
        @JsonSubTypes.Type(value = TranscodingFinishedEvent.class, name = "finished"),
        @JsonSubTypes.Type(value = ErrorEvent.class, name = "error")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public abstract class TranscodingEvent {

    @Id
    private String id;
    @JsonProperty("transcoding_id")
    private String jobId;
    private long timestamp = Instant.now().getEpochSecond();

    public TranscodingEvent(String jobId) {
        this.id = UUID.randomUUID().toString();
        this.jobId = jobId;
    }
}
