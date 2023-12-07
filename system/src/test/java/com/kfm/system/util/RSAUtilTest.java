package com.kfm.system.util;

import com.sun.tools.javac.Main;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RSAUtilTest {

    public static void main(String[] args) {
        Map<Integer, String> integerStringMap = RSAUtil.genKeyPair();
        System.out.println(integerStringMap);
    }
}