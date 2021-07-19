package com.playground.controller;

import com.playground.dto.HelloWorldBean;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="http://localhost:4200")
public class HelloWorldController {

    private final MeterRegistry meterRegistry;

    //Prometheus counter example
    private final Counter counter_OK;
    private final Counter counter_FAIL;

    //Prometheus gauge example
    private final Gauge gauge;

    //Prometheus histogram example
    private final DistributionSummary histogram;

    //Prometheus summary example
    private final DistributionSummary summary;

    public HelloWorldController(MeterRegistry meterRegistry) {
        counter_OK = Counter
                .builder("requests_count")
                .description("indicates the total requests")
                .tag("status_code", "200")
                .register(meterRegistry);

        counter_FAIL = Counter
                .builder("requests_count")
                .description("indicates the total error requests")
                .tag("status_code", "500")
                .register(meterRegistry);

        gauge = Gauge
                .builder("user_logged", () -> 500 + Math.round(50 * Math.random()))
                .description("indicates the gauge usage")
                .register(meterRegistry);

        histogram = DistributionSummary
                .builder("response_time")
                .description("indicates the histogram usage")
                .sla(200,300,400,500, 1000)
                .register(meterRegistry);

        summary = DistributionSummary
                .builder("summary_random")
                .description("summary distribution example")
                .publishPercentiles(0.5, 0.9, 0.95, 0.99)
                .register(meterRegistry);

        this.meterRegistry = meterRegistry;
    }


    @GetMapping(path = "/hello-world", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> helloWorld() {

        //Simulate error rate
        double errorRate = 0.05;
        double random_value = Math.random();
        if (random_value <= errorRate) {
            counter_FAIL.increment();
        } else {
            counter_OK.increment();
        }

        int max = 600;
        int min = 50;
        histogram.record((int) ((Math.random() * (max - min)) + min));
        summary.record(random_value);

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
