package pl.krug.yagna.transcoding.commandline;

import com.zaxxer.nuprocess.NuProcessBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CommandExecutor {

    public Flux<String> executeCommand(List<String> args, Map<String, String> environment, String workingDirectory) {
        log.info("Executing command: {}", args);
        return Flux.<byte[]>create(sink -> executeCommand(sink, args, environment, workingDirectory))
                .flatMapSequential(BytesToStringLines.instance());
    }

    private void executeCommand(FluxSink<byte[]> sink, List<String> args, Map<String, String> environment, String workingDirectory) {
        NuProcessBuilder pb = new NuProcessBuilder(args);
        pb.environment().putAll(environment);
        pb.setCwd(Paths.get(workingDirectory));
        ProcessHandler handler = new ProcessHandler(sink);
        pb.setProcessListener(handler);
        pb.start();
    }

}
