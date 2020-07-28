package com.panbo.commons.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author PanBo 2020/7/28 15:05
 */
@Slf4j
public class JWTUtil {
    private static final String PUBLIC_KEY = "";
    private static RSAPublicKey publicKey;
    static {
        try {
            publicKey = (RSAPublicKey)getPublicKey(PUBLIC_KEY);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("公钥生成失败");
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }
    private static PublicKey getPublicKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String pem = key.replaceAll("\\-*BEGIN PUBLIC KEY\\-*","")
                        .replaceAll("\\-*END PUBLIC KEY\\-*","")
                        .trim();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] keyBytes = decoder.decode(pem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    public static boolean verifyToken(String token){
        try {
            if(publicKey == null){
                publicKey = (RSAPublicKey)getPublicKey(PUBLIC_KEY);
            }
            JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null)).build();
            verifier.verify(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            //token错误，验证不通过
            log.error(e.getMessage());
            return false;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("公钥生成失败");
            log.error(ExceptionUtil.stacktraceToString(e));
            return false;
        }
        return true;
    }
}
