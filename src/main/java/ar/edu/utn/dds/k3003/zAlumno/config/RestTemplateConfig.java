package ar.edu.utn.dds.k3003.zAlumno.config;

import ar.edu.utn.dds.k3003.zAlumno.logging.TraceIdInterceptor;
import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder, TraceIdInterceptor traceIdInterceptor) {
    RestTemplate restTemplate = builder.build();
    // Agrega el interceptor que propaga X-Trace-Id en cada llamada saliente,
    // sin pisar otros interceptores que ya pudiera tener.
    List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
    interceptors.add(traceIdInterceptor);
    restTemplate.setInterceptors(interceptors);
    return restTemplate;
  }
}