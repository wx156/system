package com.kfm.system.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SendSmsTest {

    @Test
    void sendSms() {
        try {
            SendSms.sendSms("15569869411", "1234");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}