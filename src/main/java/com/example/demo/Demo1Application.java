package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Demo1Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
        System.out.println("Run on: http://localhost:8080/swagger-ui/index.html");
    }

    @RestController
    static class HelloController {
        @GetMapping("/hello")
        public String hello() {
            return "Hello, Spring Boot!";
        }

        @GetMapping("/greet")
        public String greet(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "world") String name) {
            return "Hello, " + name + "!";
        }

        @GetMapping("/add")
        public int add(int a,int b){
            return a+b;
        }
        
        @PostMapping("/create")
        public String create(@RequestBody String data) {
            // Handle the creation logic here
            return "Data created: " + data;

        }

        
        
        
    }
}
