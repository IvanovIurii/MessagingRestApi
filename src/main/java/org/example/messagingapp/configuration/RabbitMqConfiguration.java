package org.example.messagingapp.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.example.messagingapp.configuration.RabbitMqProperties.DEAD_LETTER_QUEUE_NAME;
import static org.example.messagingapp.configuration.RabbitMqProperties.EXCHANGE_NAME;
import static org.example.messagingapp.configuration.RabbitMqProperties.QUEUE_NAME;

@Configuration
public class RabbitMqConfiguration {
    @Bean
    public Exchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_NAME)
                .build();
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queue.getName());
    }

    @Bean
    public Queue queueDlq() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_NAME).build();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
