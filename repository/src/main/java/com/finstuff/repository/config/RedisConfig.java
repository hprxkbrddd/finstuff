package com.finstuff.repository.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<AccountEnlargedDTO> accountSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, AccountEnlargedDTO.class);

        Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();

        cacheConfigurationMap.put("accounts_of_user", RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(
                                new Jackson2JsonRedisSerializer<>(objectMapper,
                                        objectMapper.getTypeFactory().constructCollectionType(List.class, AccountDTO.class))
                        )
                )
        );

        cacheConfigurationMap.put("account", RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(
                                new Jackson2JsonRedisSerializer<>(objectMapper, AccountEnlargedDTO.class)
                        )
                )
        );

        cacheConfigurationMap.put("transactions_of_account", RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
        );
        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .transactionAware()
                .build();
    }
}
