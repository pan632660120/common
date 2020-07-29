package com.panbo.commons.util;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author PanBo 2020/7/22 15:55
 */
@Slf4j
public class HttpUtil {
    private static HttpClient httpClient;
    private static final Pattern KEY_PATTERN = Pattern.compile(
            "-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern CERT_PATTERN = Pattern.compile(
            "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+",
            Pattern.CASE_INSENSITIVE);


    private static void initHttpsClient(String certificateChainContents, String privateKeyContent) throws InvalidKeySpecException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = loadKeyStore(certificateChainContents, privateKeyContent);
        SSLContext sslContext = new SSLContextBuilder().loadKeyMaterial(keyStore, "".toCharArray()).build();
        httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();
    }
    private static RSAPrivateCrtKeySpec readPrivateKey(String content) throws IOException, KeyStoreException {
        Matcher matcher = KEY_PATTERN.matcher(content);
        if(matcher.find()){
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] encodeKey = decoder.decode(matcher.group(1));
            return PrivateKeyReader.getRSAKeySpec(encodeKey);
        }else {
            log.info("PrivateKey:content->" + content);
            throw new KeyStoreException("found no private key");
        }
    }
    private static KeyStore loadKeyStore(String certificateChainContents, String privateKeyContent) throws IOException, KeyStoreException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
        RSAPrivateCrtKeySpec encodeKeySpec = readPrivateKey(privateKeyContent);
        PrivateKey key;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            key = keyFactory.generatePrivate(encodeKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            key = keyFactory.generatePrivate(encodeKeySpec);
        }
        List<X509Certificate> certificateChain = readCertificateChain(certificateChainContents);
        if(certificateChain.isEmpty()){
            throw new CertificateException("Certificate file does not contain any certificates");
        }
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setKeyEntry("key",key, "".toCharArray(), certificateChain.stream().toArray(Certificate[]::new));
        return keyStore;
    }
    private static List<X509Certificate> readCertificateChain(String contents) throws CertificateException {
        Matcher matcher = CERT_PATTERN.matcher(contents);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        List<X509Certificate> certificates = new ArrayList<>();
        int start = 0;
        Base64.Decoder decoder = Base64.getDecoder();
        while(matcher.find(start)){
            byte[] buffer = decoder.decode(matcher.group(1));
            certificates.add((X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(buffer)));
            start = matcher.end();
        }
        return certificates;
    }


}
