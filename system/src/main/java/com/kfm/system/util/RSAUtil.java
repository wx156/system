package com.kfm.system.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtil {
    public static final String RSA = "RSA";// 非对称加密密钥算法
    public static final String ECB_PADDING = "RSA/ECB/PKCS1Padding";// 加密填充方式
    private static final int KEY_SIZE = 2048;// 密钥位数
    private static final int RESERVE_BYTES = 11;
    private static final int DECRYPT_BLOCK = KEY_SIZE / 8;
    private static final int ENCRYPT_BLOCK = DECRYPT_BLOCK - RESERVE_BYTES;

    /**
     * 随机生成RSA密钥对
     *
     * @param keySize 密钥长度，范围：512-2048
     */
    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(keySize);
            return kpg.genKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥对字符串进行加密
     *
     * @param data 原文
     * @param key 公钥
     */
    public static byte[] encryptWithPublicKey(byte[] data, byte[] key) throws Exception {
        Cipher cp = Cipher.getInstance(ECB_PADDING);
        cp.init(Cipher.ENCRYPT_MODE, getPublicKey(key));
        return cp.doFinal(data);
    }

    /**
     * 用公钥对字符串进行加密
     *
     * @param plain 原文
     * @param publicKey 公钥
     */
    public static String encryptWithPublicKey(String plain, String publicKey) throws Exception {
        byte[] key = Base64.decodeBase64(publicKey);
        byte[] data = plain.getBytes("UTF-8");
        return Base64.encodeBase64String(encryptWithPublicKey(data, key));
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key 密钥
     */
    public static byte[] decryptWithPublicKey(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(key));
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key 密钥
     */
    public static byte[] encryptWithPrivateKey(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key 密钥
     */
    public static byte[] decryptWithPrivateKey(byte[] data, byte[] key) throws Exception {
        Cipher cp = Cipher.getInstance(ECB_PADDING);
        cp.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
        byte[] arr = cp.doFinal(data);
        return arr;
    }

    public static PublicKey getPublicKey(byte[] key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKey(byte[] key) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 分块加密
     *
     * @param plain 原文
     * @param publicKey 公钥
     */
    public static String encryptWithPublicKeyBlock(String plain, String publicKey) throws Exception {
        byte[] data = plain.getBytes("UTF-8");
        byte[] key = Base64.decodeBase64(publicKey);

        byte[] result = encryptWithPublicKeyBlock(data, key);
        return Base64.encodeBase64String(result);
    }

    /**
     * 分块解密
     *
     * @param plain 密文
     * @param privateKey 私钥
     */
    public static String decryptWithPrivateKeyBlock(String plain, String privateKey) {
        try {
            byte[] data = Base64.decodeBase64(plain.getBytes("UTF-8"));
            byte[] key = Base64.decodeBase64(privateKey);

            byte[] bytes = decryptWithPrivateKeyBlock(data, key);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取公钥，私钥对
     * @return get(0)公钥，get(1)私钥
     */
    public static Map<Integer, String> genKeyPair() {

        Map<Integer, String> keyMap = new HashMap<Integer, String>(); // 用于封装随机产生的公钥与私钥

        try {
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = generateKeyPair(KEY_SIZE);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 得到公钥

            // 得到公钥字符串
            String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
            // 得到私钥字符串
            String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));

            // 将公钥和私钥保存到Map
            keyMap.put(0, publicKeyString); // 0表示公钥
            keyMap.put(1, privateKeyString); // 1表示私钥
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return keyMap;
    }

    /**
     * 分块加密
     *
     * @param data
     * @param key
     */
    public static byte[] encryptWithPublicKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / ENCRYPT_BLOCK);

        if ((data.length % ENCRYPT_BLOCK) != 0) {
            blockCount += 1;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * ENCRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(key));

        for (int offset = 0; offset < data.length; offset += ENCRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > ENCRYPT_BLOCK) {
                inputLen = ENCRYPT_BLOCK;
            }
            byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(encryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块加密
     *
     * @param data
     * @param key
     */
    public static byte[] encryptWithPrivateKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / ENCRYPT_BLOCK);

        if ((data.length % ENCRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * ENCRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));

        for (int offset = 0; offset < data.length; offset += ENCRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > ENCRYPT_BLOCK) {
                inputLen = ENCRYPT_BLOCK;
            }
            byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(encryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块解密
     *
     * @param data
     * @param key
     */
    public static byte[] decryptWithPublicKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / DECRYPT_BLOCK);
        if ((data.length % DECRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * DECRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(key));
        for (int offset = 0; offset < data.length; offset += DECRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > DECRYPT_BLOCK) {
                inputLen = DECRYPT_BLOCK;
            }
            byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(decryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块解密
     *
     * @param data
     * @param key
     */
    public static byte[] decryptWithPrivateKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / DECRYPT_BLOCK);
        if ((data.length % DECRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * DECRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
        for (int offset = 0; offset < data.length; offset += DECRYPT_BLOCK) {
            int inputLen = (data.length - offset);

            if (inputLen > DECRYPT_BLOCK) {
                inputLen = DECRYPT_BLOCK;
            }

            byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(decryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }
}
