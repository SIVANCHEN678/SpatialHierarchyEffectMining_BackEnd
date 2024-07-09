package com.edu.cqupt.shemining.config;

import com.edu.cqupt.shemining.util.PythonRun;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import java.io.IOException;

@Configuration
public class ApplicationConfig {
    @Resource
    ApplicationContext context;

    @Primary
    @Scope(value = "prototype")
    @Bean(name = "pythonRun")
    @ConfigurationProperties("application.python")
    public PythonRun getPythonRunBean(){
        return new PythonRun();
    }
}
