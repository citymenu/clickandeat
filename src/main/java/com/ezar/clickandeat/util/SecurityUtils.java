package com.ezar.clickandeat.util;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;

@Component(value="securityUtils")
public class SecurityUtils {

    private Key key;
    
    private String salt;

    private final String ALGORITHM = "AES";

    /**
     * @param input
     * @return
     * @throws Exception
     */

    public String encrypt(String input) throws Exception {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal((salt + input).getBytes());
        String encrypted = new BASE64Encoder().encode(encValue).replace("+","###");
        return URLEncoder.encode(encrypted, "utf-8");
    }


    /**
     * @param encryptedValue
     * @return
     * @throws Exception
     */

    public String decrypt(String encryptedValue) throws Exception {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        String decoded = URLDecoder.decode(encryptedValue,"utf-8");
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(decoded.replace("###","+"));
        byte[] decValue = c.doFinal(decordedValue);
        String decrypted = new String(decValue);
        return decrypted.substring(salt.length());
    }


    @Required
    @Value(value="${security.key}")
    public void setKey(String key) throws Exception{
        byte[] bytes = new byte[key.length()];
        for( int i = 0; i < key.length(); i++ ) {
            bytes[i] = (byte)key.charAt(i);
        }
        this.key = new SecretKeySpec(bytes, ALGORITHM);
    }


    @Required
    @Value(value="${security.salt}")
    public void setSalt(String salt) throws Exception{
        this.salt = salt;
    }

    
    
    
}
