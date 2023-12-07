package com.kfm.system.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * MD5 工具类
 */
public class MD5Util {

    /**
     * MD5 加密
     *
     * @param source 原文
     * @return 密文
     */
    public static String md5(String source) {
        try {
            // 创建MD5消息摘要对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将输入字符串转换为字节数组
            byte[] inputBytes = source.getBytes();

            // 计算MD5摘要
            byte[] md5Bytes = md.digest(inputBytes);

            // 将字节数组转换为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : md5Bytes) {
                sb.append(String.format("%02x", b));
            }

            String md5Hash = sb.toString();

            System.out.println("MD5 Hash: " + md5Hash);
            return md5Hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(String source, String salt) {
        try {
            // 生成随机盐值
            SecureRandom random = new SecureRandom();
            byte[] saltByte = salt.getBytes();

            // 创建MD5消息摘要对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将密码和盐值连接
            byte[] passwordWithSalt = concatenateByteArrays(source.getBytes(), saltByte);

            // 计算MD5摘要
            byte[] md5Bytes = md.digest(passwordWithSalt);

            // 将字节数组转换为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : md5Bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return source;
    }

    private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }


    public static String encodePassword(String password, String salt) {
        return md5(md5(password, salt));
    }

    public static void main(String[] args) {
        String md5 = md5(md5("a123456", "admin123"));
        System.out.println(md5);
    }
}
