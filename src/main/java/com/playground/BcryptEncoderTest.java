package com.playground;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptEncoderTest {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        for (int i = 1; i <= 10; i++) {
            String encodeString = encoder.encode("joao123");
            System.out.println(encodeString);
        }
    }
}
