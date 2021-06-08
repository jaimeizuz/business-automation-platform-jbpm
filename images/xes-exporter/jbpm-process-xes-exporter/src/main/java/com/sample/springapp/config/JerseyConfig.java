package com.sample.springapp.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sample.rest.XesExporterService;

@Configuration
@ComponentScan("com.sample.service")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        
        register(XesExporterService.class);
    }
}
