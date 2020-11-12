package com.yuntun.calendar_sys.util;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: RSA加解密工具类
 * @author: whj 2020/7/7
 */
public class RSAUtils {
    public static final String PUBLIC_KEY_STR = "publicKey";
    public static final String PRIVATE_KEY_STR = "privateKey";
    private static final int DEFAULT_RSA_KEY_SIZE = 1024;
    public static final String publickey =

            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMLcuUF1IvZatffboyxSiaafx71UNNGU11eGAHu9CPTVQPtxbl2N/DvkKn3meTaXJF847rLvNiYtSlaBAKA9gAkCAwEAAQ==";

    @Data
    @Accessors(chain = true)
    public static class KeyPairBase64 {
        private String privateKey;
        private String publicKey;

    }

    public static void main(String[] args) throws Exception {
        // Map<String, String> map = genKeyPair();
        // System.out.println("公钥：" + map.get(PUBLIC_KEY_STR));
        // System.out.println("私钥：" + map.get(PRIVATE_KEY_STR));

        String encrypt = encrypt("123456",
                publickey
        );
        System.out.println("加密后的字符串：" + encrypt);
        //
        // String decrypt = decrypt(encrypt,   OpenApi.PRIVATE_KEY);
        // System.out.println("解密密后的字符串："+decrypt);
        //
        // System.out.println("是否相等："+OpenApi.SECRET.equals(decrypt));
    }

    public static Map<String, String> genKeyPair() {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        Map<String, String> keyMap = new HashMap<>();
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            // 初始化密钥对生成器，密钥大小为96-1024位
            keyPairGen.initialize(DEFAULT_RSA_KEY_SIZE, new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 得到公钥
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            // 得到私钥字符串
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            // 将公钥和私钥保存到
            keyMap.put(PUBLIC_KEY_STR, publicKeyString);
            // 0表示公钥
            keyMap.put(PRIVATE_KEY_STR, privateKeyString);
            // 1表示私钥
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyMap;
    }

    public static KeyPairBase64 genKeyPairBase64() {
        KeyPairBase64 keyPairBase64 = new KeyPairBase64();
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            // 初始化密钥对生成器，密钥大小为96-1024位
            keyPairGen.initialize(DEFAULT_RSA_KEY_SIZE, new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 得到公钥
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            // 得到私钥字符串
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            // 将公钥和私钥保存到
            keyPairBase64.setPrivateKey(privateKeyString);
            keyPairBase64.setPublicKey(publicKeyString);
            // 1表示私钥
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPairBase64;
    }

    /**
     * 加密
     *
     * @param str
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        // base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey.getBytes());
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 解密
     *
     * @param str
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        // 64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        // base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

}
