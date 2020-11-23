package pl.krug.yagna.transcoding.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

@ConfigurationProperties(prefix = "golem")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class ScriptConfiguration {

    private final String scriptLocation;
    private final String yagnaKey;
    private final String inputFileLocation;
    private final Duration dataRetention;
    private final DataSize maxUpload;

}
