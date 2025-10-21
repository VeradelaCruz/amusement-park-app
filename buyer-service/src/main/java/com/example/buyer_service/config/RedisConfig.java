package com.example.buyer_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
//Cachear un objeto implica que Spring va a convertirlo en bytes para
// poder guardarlo en Redis (o en cualquier otro almacenamiento de caché).
//
//Serialización es justamente ese proceso de convertir un objeto
// Java en un formato que se pueda guardar y luego recuperar.
//
//Por defecto, Spring Redis usa JdkSerializationRedisSerializer,
// que solo puede serializar objetos que implementen la interfaz Serializable.
//
//Si tu objeto no implementa Serializable, Spring no puede “convertirlo a bytes” y
// da ese error que viste.
//
//En otras palabras: Redis no guarda el objeto tal cual,
// sino su representación en bytes, y para eso necesita que el objeto sea “serializable”.
public class RedisConfig {

    /**
     * Bean que configura el administrador de caché de Redis.
     * Este se encarga de gestionar el almacenamiento y recuperación de los datos cacheados
     * cuando se usan las anotaciones @Cacheable, @CachePut o @CacheEvict.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Define la configuración base del caché
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // Especifica que los valores de la caché se serializan en formato JSON
                // para poder guardar objetos complejos de Java en Redis
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );

        // Crea el administrador de caché usando la conexión y configuración definidas arriba
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config) // Aplica la configuración por defecto a todos los cachés
                .build();              // Construye el objeto final RedisCacheManager
    }
}
