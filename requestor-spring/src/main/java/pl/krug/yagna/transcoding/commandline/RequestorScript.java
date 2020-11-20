package pl.krug.yagna.transcoding.commandline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.krug.yagna.transcoding.configuration.ScriptConfiguration;
import pl.krug.yagna.transcoding.job.TranscodingJob;
import pl.krug.yagna.transcoding.job.event.TranscodingEvent;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class RequestorScript {

    private static final String APPKEY_VARIABLE = "YAGNA_APPKEY";

    private final ScriptConfiguration configuration;
    private final ObjectMapper mapper;
    private final CommandExecutor executor;

    public Flux<TranscodingEvent> startTranscoding(TranscodingJob job) {
        return executor.executeCommand(Arrays.asList("./run.sh", "-t", job.getId(), "-i", job.getInputPath()), prepareEnvironment(), configuration.getScriptLocation())
                .filter(this::isEventJson)
                .map(this::mapJsonToEvent);
    }

    private Map<String, String> prepareEnvironment() {
        Map<String, String> env = new HashMap<>();
        env.put(APPKEY_VARIABLE, configuration.getYagnaKey());
        return env;
    }

    private boolean isEventJson(String line) {
        return line.startsWith("{");
    }

    private TranscodingEvent mapJsonToEvent(String line) {
        try {
            return mapper.readValue(line, TranscodingEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
