package com.ezar.clickandeat.security;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;

@Component(value="passwordEncoder")
public class PasswordEncoder {
    
    private static final String algorithm = "SHA-256";
    
    private static final Logger LOGGER = Logger.getLogger(PasswordEncoder.class);

    private final MessageDigest messageDigest;

    private final BASE64Encoder base64Encoder = new BASE64Encoder();

    public PasswordEncoder() throws Exception {
        this.messageDigest = MessageDigest.getInstance(algorithm);
    }
    
    
    /**
     * @param password
     * @param salt
     * @return
     */
    
    public String encodePassword(String password, String salt) {
        try {
            String saltedPass = mergePasswordAndSalt(password, salt);
            byte[] digest = messageDigest.digest(saltedPass.getBytes("UTF-8"));
            return base64Encoder.encode(digest);
        }
        catch( Exception ex ) {
            throw new IllegalArgumentException("Could not encode password",ex);
        }
    }


    /**
     * @param encodedPassword
     * @param password
     * @param salt
     * @return
     */

    public boolean isPasswordValid(String encodedPassword, String password, String salt ) {
        String pass1 = "" + encodedPassword;
        String pass2 = encodePassword(password,salt);
        return pass1.equals(pass2);
    }


    /**
     * @param password
     * @param salt
     * @return
     */

    private String mergePasswordAndSalt(String password, String salt) {
        return password + "{" + salt + "}";
    }


}
