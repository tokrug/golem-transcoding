package pl.krug.yagna.transcoding.job;

import lombok.RequiredArgsConstructor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.stereotype.Component;
import pl.krug.yagna.transcoding.job.event.TranscodingEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TranscodingJobRepository {

    private final ObjectRepository<TranscodingEvent> db;

    public Optional<TranscodingJob> findById(String id) {
        List<TranscodingEvent> events = db.find(ObjectFilters.eq("transcoding_id", id)).toList();
        if (events.isEmpty()) {
            return Optional.empty();
        }
        TranscodingJob entity = TranscodingJob.base();
        events.forEach(entity::handleEvent);
        return Optional.of(entity);
    }

    public TranscodingJob getById(String id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Cannot find transaction job by id: " + id));
    }

    public List<TranscodingJob> getCompletedJobsOlderThan(long epochSecond) {
        List<TranscodingEvent> endEvents = db.find(ObjectFilters.and(ObjectFilters.lt("timestamp", epochSecond), ObjectFilters.in("type", "error", "finished"))).toList();
        return endEvents.stream()
                .map(TranscodingEvent::getJobId)
                .distinct()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public void add(TranscodingEvent event) {
        db.update(event, true);
    }

    public void remove(TranscodingJob job) {
        db.remove(ObjectFilters.eq("transcoding_id", job.getId()));
    }
}
