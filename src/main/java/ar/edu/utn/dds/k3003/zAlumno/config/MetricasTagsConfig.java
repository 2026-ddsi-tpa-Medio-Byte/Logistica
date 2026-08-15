package ar.edu.utn.dds.k3003.zAlumno.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricasTagsConfig {

    @Value("${DD_INSTANCIA:desconocida}")
    private String instancia;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> tagInstancia() {
        return registry -> registry.config().commonTags("instancia", instancia);
    }
}