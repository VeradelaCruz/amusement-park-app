package com.example.games_service.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration // 1️⃣ Esto le dice a Spring que esta clase es de configuración, como un "manual de instrucciones" para beans.
@EnableCaching // 2️⃣ Activa el soporte de cache en Spring, para poder usar @Cacheable, @CachePut, etc.
public class RedisConfig {

    @Bean // 3️⃣ Le decimos a Spring que este método devuelve un bean que podemos inyectar en otras partes
    public RedisCacheConfiguration cacheConfiguration() {
        // 4️⃣ Creamos un ObjectMapper de Jackson (el que convierte objetos Java <-> JSON)
        ObjectMapper mapper = new ObjectMapper();

        // 5️⃣ Registramos el módulo de Java 8 Time (para LocalDate, LocalTime, etc.)
        // sin esto Redis no sabe cómo convertir LocalTime y daba el error que tenías
        mapper.registerModule(new JavaTimeModule());

        // 6️⃣ Opcional pero recomendable: que las fechas no se escriban como números (timestamps)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 7️⃣ Creamos un serializer que use este ObjectMapper
        // Este serializer es el que realmente va a serializar los objetos que van al cache
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        // 8️⃣ Configuramos RedisCache para que use este serializer en los valores
        // Así Spring sabe cómo guardar cualquier objeto (incluso con LocalTime) en Redis
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean // 9️⃣ Creamos el CacheManager de Redis, que es el “jefe” de todos los caches de Redis
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration()) // 10️⃣ Le pasamos nuestra configuración personalizada
                .build();
    }
}

