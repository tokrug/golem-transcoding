package pl.krug.yagna.transcoding.commandline;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor(staticName = "instance")
class BytesToStringLines implements Function<byte[], Publisher<String>> {

    private String current = "";

    public Publisher<String> apply(byte[] bytes) {
        List<String> lines = new ArrayList<>();
        String newString = new String(bytes);
        current += newString;
        while (current.contains("\n")) {
            int lineBreakIndex = current.indexOf("\n");
            String completedLine = current.substring(0, lineBreakIndex);
            lines.add(completedLine);
            current = current.substring(lineBreakIndex + 1);
        }
        return Flux.fromIterable(lines);
    }
}
