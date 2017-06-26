package rabbitmq.factory;

import rabbitmq.enums.CacheType;
import rabbitmq.publisher.impl.ChannelCachedMessagePublisher;
import rabbitmq.publisher.impl.ConnectionCachedMessagePublisher;
import rabbitmq.publisher.IMessagePublisher;

/**
 * @author Arpit
 */
public class MessagePublisherFactory {

    public static IMessagePublisher getPublisher(CacheType cacheType) {
        switch (cacheType) {
            case CHANNEL_CACHE:
                return new ChannelCachedMessagePublisher();
            case CONNECTION_CACHE:
                return new ConnectionCachedMessagePublisher();
            default:
                throw new RuntimeException("Invalid cache type");
        }
    }
}
