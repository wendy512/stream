/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.github.stream.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.github.stream.core.Configurable;
import io.github.stream.core.properties.BaseProperties;

/**
 * connection状态管理
 * @author wendy512@yeah.net
 * @date 2023-05-25 14:22:33
 * @since 1.0.0
 */
public class RabbitMqStateConfigure implements Configurable {

    private ConnectionFactory connectionFactory;

    @Override
    public void configure(BaseProperties properties) {
        this.connectionFactory = createConnectionFactory(properties);
    }

    public Connection newConnection() throws IOException, TimeoutException {
        return connectionFactory.newConnection();
    }

    private ConnectionFactory createConnectionFactory(BaseProperties properties) {
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

    public void stop() {
    }
}
