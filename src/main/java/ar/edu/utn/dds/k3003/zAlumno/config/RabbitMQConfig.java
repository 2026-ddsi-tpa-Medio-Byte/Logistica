package ar.edu.utn.dds.k3003.zAlumno.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_DONACIONES = "cola-donaciones";

    // Dead letter: adonde van los mensajes que fallaron todos los reintentos
    public static final String DLX_DONACIONES = "donaciones.dlx";
    public static final String COLA_DONACIONES_DLQ = "cola-donaciones.dlq";

    @Bean
    public Queue colaDonaciones() {
        return QueueBuilder.durable(COLA_DONACIONES)
                .withArgument("x-dead-letter-exchange", DLX_DONACIONES)
                .withArgument("x-dead-letter-routing-key", COLA_DONACIONES_DLQ)
                .build();
    }

    @Bean
    public DirectExchange donacionesDlx() {
        return new DirectExchange(DLX_DONACIONES, true, false);
    }

    @Bean
    public Queue colaDonacionesDlq() {
        return QueueBuilder.durable(COLA_DONACIONES_DLQ).build();
    }

    @Bean
    public Binding bindingDlq() {
        return BindingBuilder.bind(colaDonacionesDlq()).to(donacionesDlx()).with(COLA_DONACIONES_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}