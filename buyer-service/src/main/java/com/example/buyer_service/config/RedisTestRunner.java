package com.example.buyer_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTestRunner implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Guardar un valor de prueba
        redisTemplate.opsForValue().set("testKey", "¡Hola Redis!");

        // Leer el valor
        String value = (String) redisTemplate.opsForValue().get("testKey");
        System.out.println("Valor en Redis: " + value);
    }
}
