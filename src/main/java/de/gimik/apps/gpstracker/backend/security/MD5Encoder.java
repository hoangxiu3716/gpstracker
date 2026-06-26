package de.gimik.apps.gpstracker.backend.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dang on 29.08.2014.
 */
public class MD5Encoder implements PasswordEncoder {


    public String encode(CharSequence rawPassword) {
        try {
            return MD5Util.getMD5Hash(rawPassword.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            return encodedPassword.equals(MD5Util.getMD5Hash(rawPassword.toString()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
