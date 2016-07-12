package com.dvdexchange;


import com.dvdexchange.utils.WebAppPropertiesListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@ImportResource({"classpath:spring/dispatcher-servlet.xml", "classpath:spring/spring-database.xml", "classpath:spring/spring-security.xml"})
@Configuration
@SpringBootApplication
public class DvdExchangeSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DvdExchangeSpringBootApplication.class, args);
    }

    @Bean
    public WebAppPropertiesListener webAppPropertiesListener() {
        return new WebAppPropertiesListener();
    }

}
