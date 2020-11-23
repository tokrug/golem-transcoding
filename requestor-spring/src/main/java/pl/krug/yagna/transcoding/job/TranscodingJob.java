package pl.krug.yagna.transcoding.job;

import lombok.Getter;
import org.dizitart.no2.objects.Id;
import pl.krug.yagna.transcoding.job.event.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TranscodingJob {

    @Id
    private String id;
    private String filename;
    private String inputPath;
    private boolean completed;
    private Long completedTimestamp;
    private String error;
    private final List<String> completedFormats = new ArrayList<>();

    public static TranscodingJob base() {
        return new TranscodingJob();
    }

    public void handleEvent(TranscodingEvent event) {
        if (event instanceof JobCreatedEvent) {
            JobCreatedEvent created = (JobCreatedEvent) event;
            id = created.getJobId();
            filename = created.getFilename();
            inputPath = created.getInputPath();
        } else if (event instanceof SubtaskFinishedEvent) {
            SubtaskFinishedEvent subtaskFinishedEvent = (SubtaskFinishedEvent) event;
            completedFormats.add(subtaskFinishedEvent.getExtension());
        } else if (event instanceof TranscodingFinishedEvent) {
            completed = true;
            completedTimestamp = Instant.now().getEpochSecond();
        } else if (event instanceof ErrorEvent) {
            ErrorEvent error = (ErrorEvent) event;
            completed = true;
            this.error = error.getMessage();
            completedTimestamp = Instant.now().getEpochSecond();
        }
    }



}
