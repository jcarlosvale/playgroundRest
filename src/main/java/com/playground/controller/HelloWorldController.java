package com.playground.controller;

import com.playground.dto.HelloWorldBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@Log4j2
public class HelloWorldController {

    @GetMapping(path = "/hello-world", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello World 1", HttpStatus.OK);
    }

    @GetMapping(path = "/hello-world-bean")
    public HelloWorldBean helloWorldBean() {
        return new HelloWorldBean("Hello World Bean");
    }

    @GetMapping(path = "/hello-world/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> helloWorld(@PathVariable String name) {
        return new ResponseEntity<>("Hello World 2 " + name, HttpStatus.OK);
    }

    @GetMapping(path = "/exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> helloWorldException() {
        throw new RuntimeException("Hello Word Example");
    }

    @PostMapping(path = "/hello-world", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> helloWorld(@RequestBody HelloWorldBean helloWorldBean) {
        log.info("POST " + helloWorldBean);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/hello-world/{name}")
    public ResponseEntity<String> helloWorldDelete(@PathVariable String name) {
        log.info("DELETE " + name);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/hello-world")
    public ResponseEntity<Void> helloWorldPut(@RequestBody HelloWorldBean helloWorldBean) {
        log.info("PUT " + helloWorldBean);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
