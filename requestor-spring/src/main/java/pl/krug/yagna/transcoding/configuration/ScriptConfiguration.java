package pl.krug.yagna.transcoding.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "golem")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class ScriptConfiguration {

    private final String scriptLocation;
    private final String yagnaKey;
    private final String inputFileLocation;

}
