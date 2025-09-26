package com.example.api_gateway_park;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayParkApplication.class, args);
	}

}
