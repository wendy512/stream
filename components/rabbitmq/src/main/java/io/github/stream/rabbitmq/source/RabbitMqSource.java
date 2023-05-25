package io.github.stream.rabbitmq.source;

import org.apache.commons.lang3.StringUtils;

import io.github.stream.core.properties.AbstractProperties;
import io.github.stream.core.source.AbstractSource;
import com.rabbitmq.client.ConnectionFactory;

/**
 * rabbitMq 输入（消费）
 * @author wendy512@yeah.net
 * @date 2023-05-24 13:56:41
 * @since 1.0.0
 */
public class RabbitMqSource extends AbstractSource {

    @Override
    public void configure(AbstractProperties properties) {
        /*ConnectionFactory connectionFactory = createConnectionFactory(properties);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();*/
        //channel.que
    }

    private ConnectionFactory createConnectionFactory(AbstractProperties properties) {
        String host = properties.getString("host");
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("RabbitMQ host cannot be empty");
        }

        Integer port = properties.getInteger("port");
        if (null == port) {
            throw new IllegalArgumentException("RabbitMQ port cannot be empty");
        }

        String username = properties.getString("username");
        String password = properties.getString("password");
        String virtualHost = properties.getString("virtualHost", "/");
        int connectionTimeout = properties.getInt("connectionTimeout", 60000);

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setConnectionTimeout(connectionTimeout);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }
}
