package pl.krug.yagna.transcoding.job.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
public class EventStream {

    private final Sinks.Many<TranscodingEvent> eventSink = Sinks.many().multicast().directBestEffort();

    public Flux<TranscodingEvent> getByJobId(String id) {
        return eventSink.asFlux().filter(event -> event.getJobId().equals(id));
    }

    public void emitEvent(TranscodingEvent event) {
        eventSink.tryEmitNext(event).orThrow();
    }

}
