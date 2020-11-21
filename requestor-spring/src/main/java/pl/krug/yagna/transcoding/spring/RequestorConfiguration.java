package pl.krug.yagna.transcoding.spring;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krug.yagna.transcoding.Application;
import pl.krug.yagna.transcoding.job.event.TranscodingEvent;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = Application.class)
public class RequestorConfiguration {

    @Bean
    Nitrite provideDb() {
        return Nitrite.builder()
                .compressed()
                .openOrCreate();
    }

    @Bean
    ObjectRepository<TranscodingEvent> jobRepository(Nitrite db) {
        return db.getRepository(TranscodingEvent.class);
    }

}
