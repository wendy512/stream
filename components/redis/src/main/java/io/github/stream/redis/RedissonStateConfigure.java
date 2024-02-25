package io.github.stream.redis;

import io.github.stream.core.Configurable;
import io.github.stream.core.properties.AbstractProperties;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.BaseConfig;
import org.redisson.config.Config;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 连接配置
 * @author taowenwu
 * @date 2023-10-17 16:21:11
 * @since 1.0.0
 */
public class RedissonStateConfigure implements Configurable {

    private RedissonClient client;

    @Override
    public void configure(AbstractProperties properties) {
        String mode = properties.getString("mode", Constants.MODE_SINGLE);
        String address = properties.getString("address");
        Assert.hasText(address, "address cannot be empty");
        String username = properties.getString("username");
        String password = properties.getString("password");
        int database = properties.getInt("database");

        Config config = new Config();
        BaseConfig baseConfig = null;
        List<String> addresses = Arrays.asList(address.split(","));
        switch (mode) {
            case Constants.MODE_SINGLE:
                baseConfig = config.useSingleServer().setAddress(address).setDatabase(database);
                break;
            case Constants.MODE_CLUSTER:
                config.useClusterServers().setNodeAddresses(addresses);
                break;
            case Constants.MODE_MASTER_SLAVE:
                config.useMasterSlaveServers()
                        .setDatabase(database)
                        .setMasterAddress(addresses.remove(0))
                        .setSlaveAddresses(new HashSet<>(addresses));
                break;
            case Constants.MODE_REPLICATED:
                config.useReplicatedServers().setDatabase(database).setNodeAddresses(addresses);
                break;
        }

        if (StringUtils.isNotBlank(username)) {
            baseConfig.setUsername(username);
        }
        if (StringUtils.isNotBlank(password)) {
            baseConfig.setPassword(password);
        }
        client = Redisson.create(config);
    }

    public RedissonClient getClient() {
        return this.client;
    }
}
