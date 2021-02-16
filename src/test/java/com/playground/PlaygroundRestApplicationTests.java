package com.playground;

import com.playground.service.TodoHardCodedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PlaygroundRestApplicationTests {

    @Autowired
    TodoHardCodedService service;

    @Test
	void contextLoads() {
        System.out.println(service.getSQL());
	}

}
