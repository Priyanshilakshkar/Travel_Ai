package com.Train.TrainService.Configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;

public class RedisConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonCLient(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6380")
                .setDatabase(0);

        return Redisson.create(config);
    }
}
