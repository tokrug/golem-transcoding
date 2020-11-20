package pl.krug.yagna.transcoding.commandline;

import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SynchronousSink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

@Slf4j
@RequiredArgsConstructor
class ProcessHandler extends NuAbstractProcessHandler {

    private final FluxSink<byte[]> sink;
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    private NuProcess nuProcess;

    @Override
    public void onStart(NuProcess nuProcess) {
        this.nuProcess = nuProcess;
    }

    @Override
    public boolean onStdinReady(ByteBuffer buffer) {
        return false; // false means we have nothing else to write at this time
    }

    @Override
    public void onStdout(ByteBuffer buffer, boolean closed) {
        if (!closed) {
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            sink.next(bytes);
        }
    }

    @Override
    public void onStderr(ByteBuffer buffer, boolean closed) {
        if (!closed) {
            try {
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                errorStream.write(bytes);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    public void onExit(int statusCode) {
        if (statusCode == Integer.MIN_VALUE || statusCode == Integer.MAX_VALUE) {
            sink.error(new RuntimeException("Unexpected error: " + statusCode));
        } else if (statusCode != 0) {
            sink.error(new RuntimeException(new String(errorStream.toByteArray())));
        }
        sink.complete();
    }
}
