package com.kfm.system.util;

import java.util.HashMap;
import java.util.Map;

public class RSAPairKey {
    // 使用静态Map存储公钥和私钥
    private static Map<Integer, String> keymap = new HashMap<Integer, String>();
    static {
        keymap = RSAUtil.genKeyPair(); // 生成公钥和私钥
    }

    /**
     * 获取公钥
     * @return
     */
    public static String getPublicKey(){
        return keymap.get(0);
    }

    /**
     * 获取私钥
     * @return
     */
    public static String getPrivateKey(){
        return keymap.get(1);
    }
}
