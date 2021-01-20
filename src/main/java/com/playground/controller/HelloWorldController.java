package com.playground.controller;

import com.playground.dto.HelloWorldBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins="http://localhost:4200")
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

    @GetMapping(path = "/hello-world-body", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> helloWorldBody(@RequestBody String name) {
        return new ResponseEntity<>("Hello World 3 " + name, HttpStatus.OK);
    }

    @GetMapping(path = "/exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> helloWorldException() {
        throw new RuntimeException("Hello Word Example");
    }
}
