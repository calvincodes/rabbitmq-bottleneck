package rabbitmq.publisher.impl;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import rabbitmq.publisher.IMessagePublisher;
import rabbitmq.enums.CacheType;

/**
 * @author Arpit
 */
public class ConnectionCachedMessagePublisher implements IMessagePublisher {

    private static volatile boolean INITIALIZED = false;

    private static CachingConnectionFactory CACHING_CONNECTION_FACTORY = new CachingConnectionFactory("localhost");
    private static RabbitTemplate RABBIT_TEMPLATE;

    private void init() {
        if (!INITIALIZED) {
            synchronized (ConnectionCachedMessagePublisher.class) {
                CACHING_CONNECTION_FACTORY.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
                CACHING_CONNECTION_FACTORY.setConnectionCacheSize(20);
                CACHING_CONNECTION_FACTORY.setVirtualHost("testVHost");
                CACHING_CONNECTION_FACTORY.setConnectionNameStrategy(CACHING_CONNECTION_FACTORY -> "arpit-cached-connection");
                CACHING_CONNECTION_FACTORY.setPublisherConfirms(false);
                RABBIT_TEMPLATE = new RabbitTemplate(CACHING_CONNECTION_FACTORY);
            }
            INITIALIZED = true;
        }
    }

    public CacheType getCacheType() {
        return CacheType.CONNECTION_CACHE;
    }

    public void publishMessage(String exchange, String routingKey, String message) {
        init();

        RABBIT_TEMPLATE.convertAndSend(exchange, routingKey, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
                return new org.springframework.amqp.core.Message(message.getBody(),properties);
            }
        });
    }
}
