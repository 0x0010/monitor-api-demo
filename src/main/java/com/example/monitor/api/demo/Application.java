package com.example.monitor.api.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sam
 * @since 3.0.0
 */

@SpringBootApplication()
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @RestController
  class ApiIndexController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
      Monitor.idxTpsMark();
      try {
        Thread.sleep(5);
      } catch (InterruptedException ignore) {

      }
      return "Greetings from Spring Boot!";
    }
  }

  @Bean
  public ServletRegistrationBean monitorServlet() {
    return new ServletRegistrationBean(new MonitorServlet(), "/monitor/*");
  }
}
