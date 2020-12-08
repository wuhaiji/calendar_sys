package com.yuntun.calendar_sys.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuntun.calendar_sys.constant.JwtConstant;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.SysUserCode;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.yuntun.calendar_sys.constant.JwtConstant.AES_SECRET;
import static com.yuntun.calendar_sys.constant.JwtConstant.BASE64SECRET;


/**
 * JWT工具类
 *
 * @author whj
 */
@Slf4j
@SuppressWarnings("restriction")
public class JwtHelper {

    public static final ConcurrentHashMap<String, String> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    public static final String USER_AGENT = "userAgent";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";
    public static final String EXPIRE_TIME = "expireTime";
    public static final String SHA_256 = "SHA-256";
    public static final long TEN_YEARS_MILL = 600_000_000_000L;
    // public static final long TEN_YEARS_MILL = 20_000L;

    /**
     * 生成JWT字符串 格式：A.B.C A-header头信息 B-payload 有效负荷 C-signature 签名信息
     * 是将header和payload进行加密生成的
     *
     * @param userId     用户编号
     * @param userName   用户名
     * @param identities 客户端信息（变长参数），目前包含浏览器信息，用于客户端拦截器校验，防止跨域非法访问
     * @return
     */
    public static String generateJWT(String userId, String userName, String... identities) {

        // 签名算法，选择SHA-256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 获取当前系统时间
        long nowTimeMillis = System.currentTimeMillis();
        // 添加Token过期时间
        Date expDate = new Date(nowTimeMillis + JwtConstant.EXPIRE_Mill);
        Date now = new Date(nowTimeMillis);
        //生成密匙
        // 将BASE64SECRET常量字符串使用base64解码成字节数组
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(BASE64SECRET);
        // 使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        // 添加构成JWT的参数
        Map<String, Object> headMap = new HashMap<>(2);
        // Header { "alg": "HS256", "typ": "JWT" }
        headMap.put("alg", SignatureAlgorithm.HS256.getValue());
        headMap.put("typ", "JWT");
        JwtBuilder builder = Jwts.builder().setHeader(headMap)
                // Payload { "userId": "1234567890", "userName": "vic", }
                // 加密后的客户编号
                .claim(USER_ID, AESUtil.encryptToStr(userId, AES_SECRET))
                // 客户名称
                .claim(USER_NAME, userName)
                // 客户端浏览器信息
                .claim(USER_AGENT, identities[0])
                // Signature
                .signWith(signatureAlgorithm, signingKey)
                //设置过期时间
                .setExpiration(expDate)
                .setNotBefore(now);
        return builder.compact();
    }

    /**
     * 生成JWT字符串 格式：A.B.C A-header头信息 B-payload 有效负荷 C-signature 签名信息
     * 是将header和payload进行加密生成的
     * (过期时间十年，长期有效)
     *
     * @param userId     用户编号
     * @param userName   用户名
     * @param identities 客户端信息（变长参数），目前包含浏览器信息，用于客户端拦截器校验，防止跨域非法访问
     * @return
     */
    public static String generateJWT2(String userId, String userName, String... identities) {

        // 签名算法，选择SHA-256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 获取当前系统时间
        long nowTimeMillis = System.currentTimeMillis();
        // 添加Token过期时间
        Date expDate = new Date(nowTimeMillis + TEN_YEARS_MILL);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("生成token的过期时间：{}", simpleDateFormat.format(expDate));
        //生成密匙
        // 将BASE64SECRET常量字符串使用base64解码成字节数组
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(BASE64SECRET);
        // 使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        // 添加构成JWT的参数
        Map<String, Object> headMap = new HashMap<>(2);
        // Header { "alg": "HS256", "typ": "JWT" }
        headMap.put("alg", SignatureAlgorithm.HS256.getValue());
        headMap.put("typ", "JWT");
        JwtBuilder builder = Jwts.builder().setHeader(headMap)
                // Payload { "userId": "1234567890", "userName": "vic", }
                // 加密后的客户编号
                .claim(USER_ID, AESUtil.encryptToStr(userId, AES_SECRET))
                // 客户名称
                .claim(USER_NAME, userName)
                // 客户端浏览器信息
                .claim(USER_AGENT, identities[0])
                // Signature
                .signWith(signatureAlgorithm, signingKey)
                //设置过期时间
                .setExpiration(expDate)
                .setNotBefore(new Date(nowTimeMillis));
        return builder.compact();
    }


    /**
     * 生成JWT字符串 格式：A.B.C A-header头信息 B-payload 有效负荷 C-signature 签名信息
     * 是将header和payload进行加密生成的
     *
     * @param userId     用户编号
     * @param identities 客户端信息（变长参数），目前包含浏览器信息，用于客户端拦截器校验，防止跨域非法访问
     * @return
     */
    public static String generateJWTCustomize(String userId, Long expireTime, String... identities) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(USER_ID, userId);
        jsonObject.put(USER_AGENT, identities[0]);
        jsonObject.put(EXPIRE_TIME + System.currentTimeMillis(), expireTime);

        RSAUtils.KeyPairBase64 keyPair = RSAUtils.genKeyPairBase64();
        String privateKey = keyPair.getPrivateKey();
        String publicKey = keyPair.getPublicKey();
        String publicKeyMd5 = SecureUtil.md5(publicKey);

        CONCURRENT_HASH_MAP.put("JWT_RSA_KEY_PAIR:" + publicKeyMd5, privateKey);
        try {
            String encrypt = RSAUtils.encrypt(jsonObject.toJSONString(), publicKey);

            encrypt = publicKeyMd5 + "." + encrypt;
            log.info("自定义jwt结果：{}", encrypt);
            return encrypt;
        } catch (Exception e) {
            log.error("RSA加密失败:", e);
            return null;
        }
    }

    /**
     * 验证自定义jwt有效性
     *
     * @param userId     用户编号
     * @param identities 客户端信息（变长参数），目前包含浏览器信息，用于客户端拦截器校验，防止跨域非法访问
     * @return
     */
    public static JSONObject validateJWTCustomize(String userId, Long expireTime, String... identities) {

        return null;

    }


    /**
     * 生成随机字符串
     *
     * @param length 要生成的字符串长度
     * @return
     */
    public static String getRandomString(int length) {
        //1.  定义一个字符串（A-Z，a-z，0-9,1-9对应键盘符号）即62个数字字母；
        String str = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!@#$%^&*()";
        //2.  由Random生成随机数
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        //3.  长度为几就循环几次
        for (int i = 0; i < length; ++i) {
            //从62个的数字或字母中选择
            int number = random.nextInt(str.length());
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        // //生成AES_SECRET
        // String randomString = getRandomString(16);
        // System.out.println(randomString);
        // //生成BASE64SECRET
        // String randomString2 = getRandomString(24);
        // String string = Base64.getEncoder().encodeToString(randomString2.getBytes());
        // System.out.println(string);

        // String chrome = generateJWTCustomize("1", 30_000L, "chrome");
        // System.out.println(chrome);

    }

    /**
     * 解析JWT 返回Claims对象,从redis中获取secret进行解密，如果获取为空，说明jwt已经失效
     *
     * @param jsonWebToken token
     * @return
     */
    public static Claims parseJWT(String jsonWebToken) {
        try {
            // 解析jwt
            return Jwts.parser().setSigningKey(Base64.getDecoder().decode(BASE64SECRET)).parseClaimsJws(jsonWebToken).getBody();
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException:", e);
            log.error("[JWTHelper]-JWT解析异常：token已经超时");
            throw new ServiceException(SysUserCode.LOGIN_FAILED_TIME_OUT);
        } catch (Exception e) {
            log.error("Exception:", e);
            log.error("[JWTHelper]-JWT解析异常：token非法token");
            throw new ServiceException(SysUserCode.LOGIN_FAILED_ILLEGAL_TOKEN);
        }
    }


    /**
     * 校验JWT是否有效 返回json字符串的demo:
     * {"freshToken":"A.B.C","userName":"vic","userId":"123", "userAgent":"xxxx"}
     * freshToken-刷新后的jwt userName-客户名称 userId-客户编号 userAgent-客户端浏览器信息
     *
     * @param jsonWebToken
     * @return
     */
    public static JSONObject validateLogin(String jsonWebToken) {

        if (EptUtil.isEmpty(jsonWebToken)) {
            log.error("[JWTHelper]-json web token 为空");
            throw new ServiceException(SysUserCode.LOGIN_FAILED_TOKEN_IS_EMPTY);
        }
        JSONObject retMap = new JSONObject();
        Claims claims = parseJWT(jsonWebToken);
        if (claims == null) {
            return retMap;
        }
        // 解密用户id编号
        retMap.put(USER_ID, AESUtil.decryptToStr((String) claims.get(USER_ID), AES_SECRET));
        // 客户名称
        retMap.put(USER_NAME, claims.get(USER_NAME));
        // 客户端浏览器信息
        retMap.put(USER_AGENT, claims.get(USER_AGENT));
        // 刷新JWT
        // retMap.put("freshToken", generateJWT(decryptUserId, (String) claims.get("userName"),
        //         (String) claims.get("userAgent"), (String) claims.get("domainName")));
        return retMap;
    }
}
