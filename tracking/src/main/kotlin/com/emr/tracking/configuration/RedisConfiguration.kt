package com.emr.tracking.configuration

import com.emr.tracking.model.RedisGatewayDirectory
import com.emr.tracking.model.RedisGatewayNode
import com.emr.tracking.model.RedisStreamReading
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfiguration(
    private var appProperties: AppProperties
) {
    @Bean
    fun letucceConnectionFactory(): LettuceConnectionFactory {
        val configuration = RedisStandaloneConfiguration(appProperties.redisHost, appProperties.redisPort.toInt())
        return LettuceConnectionFactory(configuration)
    }

    @Bean
    fun redisStreamReadingTemplate(): RedisTemplate<String, RedisStreamReading> {
        val redisTemplate = RedisTemplate<String, RedisStreamReading>()
        redisTemplate.setConnectionFactory(letucceConnectionFactory())
        return redisTemplate
    }

    @Bean
    fun redisGatewayTemplate(): RedisTemplate<String, RedisGatewayNode> {
        val redisTemplate = RedisTemplate<String, RedisGatewayNode>()
        redisTemplate.setConnectionFactory(letucceConnectionFactory())
        return redisTemplate
    }

    @Bean
    fun redisGatewayDirectoryTemplate(): RedisTemplate<String, RedisGatewayDirectory> {
        val redisTemplate = RedisTemplate<String, RedisGatewayDirectory>()
        redisTemplate.setConnectionFactory(letucceConnectionFactory())
        return redisTemplate
    }
}