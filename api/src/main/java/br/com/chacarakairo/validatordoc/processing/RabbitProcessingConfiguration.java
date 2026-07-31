package br.com.chacarakairo.validatordoc.processing;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RabbitProcessingConfiguration {
    public static final String EXCHANGE = "validator-doc.processing";
    public static final String QUEUE = "validator-doc.processing.jobs";
    public static final String ROUTING_KEY = "document.processing.requested";

    @Bean DirectExchange processingExchange() { return new DirectExchange(EXCHANGE, true, false); }
    @Bean Queue processingQueue() { return new Queue(QUEUE, true); }
    @Bean Binding processingBinding(Queue processingQueue, DirectExchange processingExchange) {
        return BindingBuilder.bind(processingQueue).to(processingExchange).with(ROUTING_KEY);
    }
    @Bean Jackson2JsonMessageConverter jsonMessageConverter() { return new Jackson2JsonMessageConverter(); }
    @Bean SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
