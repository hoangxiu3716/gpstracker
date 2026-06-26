package de.gimik.apps.gpstracker.backend.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.util.Constants;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class SecurityUtility {
	private static String AES_KEY = "m+vT4BsWVkrJY/qCK4d+SnYHZ7QDmojN1QIZrxsO/K8=";
	private static String RSA_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCYv9W0gV31zWWeERWOPgOMo8f3Ap6JhFrMW3CurCsQyTDFzGqg9v3Fptk9TfRG6sfqOmR7KPwQF87VZXmiMHzyvFErZr5dme1qCtVi107tKGyP5txQzXKtyCP8kjwbO2yVw6Wg0QfwNbOFKGjIoMtrATz6yJ9haAM7zY0sGw+/rIvs5+DQZCkMQ/jKFzOnD2tzUoUhbXFgap6rAlwc+5T8NOCn5LwolM9K97qn8Zh6A/GdOmCkd0kXkUp155hb/lQucDjOk2HY9C77NV0lsl2MwkWUexifWXzuu9jYHzCgp5bSV6tTPqAARSZFkXZxa9wIzH9loEjOunVfbilruDUdAgMBAAECggEAO9PfWzzUVYMmivu8CqOFdl9r4fa3q7sYaTRjdTbgA0oZ/U8scHFtQi4wpk9THze0J6zJUGfkGlWI4JJHStxdBCH4VNyAMV8p2z/1Biq4sjEEHHTa1Bc0FJbwaWvJeQ0UYbBovg1BY7dU4hHKwNIh+n5YqQ1NR7Sal6DR63WJ/JN4lCAW84jySioozwwCa1Alh9osZDUAmZFkJT1xppjm4tnz4fGbctYxxU6h7zMd0DwzIHm3cxnHCVD6UA7tNhIoikYZYXgup9qfBH31Segu3SAyDIoOpNrfqi7e+VO8HUT+RvEHClGukrtCP5Kaz5ITyyFsixzkRFE3HFaScKKogQKBgQDNIp3IX95kG+cAt/W8UIcawD2S8hZdCYv/uZBrETr40wybypDcOP+5hnh4TMnf/GBjmtidK/eXSRaC8J2PxEQfYYcA7h0QlSPNIWLQj5cXBdaENw2znMDnjm06MLvNYTQp+2FLYrj2ECxvbOfs5e+mHcC1X56y0xEJGt2oUSsUPQKBgQC+n+q94JyHmmZ+XeX4y72sSF9VRieb/O6e4IPLaC2AbZCFSrzjRb5jO9dmM33WotGMEVpk6EuS2+nLOvUHrNjEjHVVhW6RLBrpoj4U3tDbveRkt6IUquWwwwaShkpUr+MCSZw/dCFTSYwQRFp8jFL1Sk6fTaD49EbiXS5pMjJSYQKBgQCJAmHX/xAOVQSI2lVqnW5fU57MkpyVL1cuScZHfI6iunNlanouXHOQelvkHYWH8IyAel/LOh54EPzUH9lcuH23Z/A/ksoe1T3AOFhDbGRBoEiSriKcoEnHXr1dbEdC+XVZxOhK7XkT/+Ft2pNdCoZxsNWRTng0f/yU1DjPvsbDEQKBgArj1+VBD7Avget2Mc0k1pEhEVEt0NV4falV6jGUogTUPY1f16qkMVw57vRHvMimRJJb624YVzjsl06k75qgpUGVRJ6+ILLqch8mxaUrlYL4NjQKyZekq7qNabK1OUndQnq++c4mNEEt0nKbdQ/odFTPCe7P4IJYcjQ8Xopj87wBAoGAJOUCWG7QjMEWH5AYzuaE0MRp9Ppw04NNSfRv9ffbpQWnx8XniVWMfYmIG7pt0dHtIZYNAK58HvkCw2gXeOKXwzAd7bfqKMbajKiZvKYamMFpTV2HrIK5rJ2wQ3YgegpUcvzqd8Yusy+t8IVRzfT2bn5WdXvKAs4ONE3co/Te8Ks=";

    public static boolean hasRole(Authentication authentication, String role){
        if (authentication == null){
                return false;
        }

        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();

        if (grantedAuthorities == null || grantedAuthorities.size() <= 0){
                return false;
        }

        for(GrantedAuthority grantedAuthority : grantedAuthorities){
                if (grantedAuthority.getAuthority().equalsIgnoreCase(role)){
                        return true;
                }
        }

        return false;
    }

    public static boolean hasRole(User user, String role){
        if (user == null){
            return false;
        }

        Collection<? extends GrantedAuthority> grantedAuthorities = user.getRoles();

        if (grantedAuthorities == null || grantedAuthorities.size() <= 0){
            return false;
        }

        for(GrantedAuthority grantedAuthority : grantedAuthorities){
            if (grantedAuthority.getAuthority().equalsIgnoreCase(role)){
                return true;
            }
        }

        return false;
    }

    public static final boolean isAdminRole(Authentication authentication){
        return hasRole(authentication, Constants.ROLE_ADMIN);
    }


 

    public static final boolean isCurrentUserAdmin(){
        return isAdminRole(SecurityContextHolder.getContext().getAuthentication());
    }


    public static final User getCurrentUser(){
        try{
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception ex){
            return null;
        }
    }
    public static SecretKey getAesKey() {
    	try {
    		byte[] decodedKey = Base64.getDecoder().decode(AES_KEY);
    		SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
            return key;
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return null;

    }
    public static String encyptAes(SecretKey key, String msg) {
		try {
			Cipher c;
			c = Cipher.getInstance("AES/ECB/PKCS5PADDING");
	  		c.init(Cipher.ENCRYPT_MODE, key);
	  		byte encryptOut[] = c.doFinal(msg.getBytes());
	  		String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);
	  		return strEncrypt;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return "";
    }
    public static String encyptAes( String msg) {
		try {
			SecretKey key = getAesKey();
			Cipher c;
			c = Cipher.getInstance("AES/ECB/PKCS5PADDING");
	  		c.init(Cipher.ENCRYPT_MODE, key);
	  		byte encryptOut[] = c.doFinal(msg.getBytes());
	  		String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);
	  		return strEncrypt;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return "";
    }
}