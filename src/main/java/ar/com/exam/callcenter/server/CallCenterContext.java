package ar.com.exam.callcenter.server;

import ar.com.exam.callcenter.dispatch.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CallCenterContext {
    // inject via application.properties
    @Value("${max.calls}")
    private Integer maxCalls;

    @Bean
    public Integer getMaxCalls(){
        return this.maxCalls;
    }

    @Bean
    public Dispatcher getDispatcher(){
        return new Dispatcher(this.maxCalls);
    }
}
