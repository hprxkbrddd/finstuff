package com.finstuff.repository.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finstuff.repository.dto.AccountDTO;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.AccountTransactionsDTO;
import com.finstuff.repository.dto.TransactionEnlargedDTO;
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

        // ACCOUNTS OF USER CACHE
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

        // ACCOUNT CACHE
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

        // TRANSACTIONS OF ACCOUNT CACHE
        cacheConfigurationMap.put("account_transactions", RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, AccountTransactionsDTO.class))
                )
        );

        // TRANSACTION CACHE
        cacheConfigurationMap.put("transaction", RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, TransactionEnlargedDTO.class))
                )
        );

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .transactionAware()
                .build();

    }
}
