package com.yourrents.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.yourrents.services.common.util.jooq.JooqUtils;

@Configuration
@ComponentScan(basePackages = { "com.yourrents.services.geodata.controller",
                "com.yourrents.services.geodata.repository", "com.yourrents.services.geodata.mapper" })
public class YourRentsServiceGeodataConfiguration {

        @Bean
        public JooqUtils jooqUtils() {
                return new JooqUtils();
        }

}