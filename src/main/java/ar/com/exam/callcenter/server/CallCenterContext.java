package ar.com.exam.callcenter.server;

import ar.com.exam.callcenter.dispatch.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CallCenterContext {

    ExecutorService executorService;
    // inject via application.properties
    @Value("${max.calls}")
    private Integer maxCalls;

    public CallCenterContext() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService getExecutorService(){
        return executorService;
    }

    @Bean
    public Integer getMaxCalls(){
        return this.maxCalls;
    }

    @Bean
    public Dispatcher getDispatcher(){
        Dispatcher dispatcher = new Dispatcher(this.maxCalls);
        executorService.execute(dispatcher);
        return dispatcher;
    }
}
