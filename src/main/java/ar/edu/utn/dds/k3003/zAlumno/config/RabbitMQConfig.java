package ar.edu.utn.dds.k3003.zAlumno.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_DONACIONES = "cola-donaciones";

    @Bean
    public Queue colaDonaciones() {
        return new Queue(COLA_DONACIONES, true); // true = durable, sobrevive reinicios
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}