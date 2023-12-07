package com.kfm.system;

import com.kfm.system.util.RSAUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SystemApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(RSAUtil.genKeyPair());
    }

}
