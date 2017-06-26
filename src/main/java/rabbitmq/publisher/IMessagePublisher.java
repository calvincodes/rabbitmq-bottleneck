package rabbitmq.publisher;

import rabbitmq.enums.CacheType;

/**
 * @author Arpit
 *         Created on 24/06/17.
 */
public interface IMessagePublisher {

    CacheType getCacheType();

    void publishMessage(String exchange, String routingKey, String message);
}
