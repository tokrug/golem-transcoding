package pl.krug.yagna.transcoding.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import pl.krug.yagna.transcoding.configuration.ScriptConfiguration;

@Configuration
@RequiredArgsConstructor
public class WebFluxConfiguration implements WebFluxConfigurer {

    private final ScriptConfiguration scriptConfiguration;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        SynchronossPartHttpMessageReader partReader = new SynchronossPartHttpMessageReader();
        partReader.setMaxParts(1);
        partReader.setMaxDiskUsagePerPart(scriptConfiguration.getMaxUpload().toBytes());
        partReader.setEnableLoggingRequestDetails(true);

        MultipartHttpMessageReader multipartReader = new MultipartHttpMessageReader(partReader);
        multipartReader.setEnableLoggingRequestDetails(true);

        configurer.defaultCodecs().multipartReader(multipartReader);
    }
}
