package de.gimik.apps.gpstracker.backend.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.StringUtils;

import com.google.common.io.BaseEncoding;

public class SecurityUtility {
	private static String AES_KEY = "m+vT4BsWVkrJY/qCK4d+SnYHZ7QDmojN1QIZrxsO/K8=";
	private static String RSA_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCYv9W0gV31zWWeERWOPgOMo8f3Ap6JhFrMW3CurCsQyTDFzGqg9v3Fptk9TfRG6sfqOmR7KPwQF87VZXmiMHzyvFErZr5dme1qCtVi107tKGyP5txQzXKtyCP8kjwbO2yVw6Wg0QfwNbOFKGjIoMtrATz6yJ9haAM7zY0sGw+/rIvs5+DQZCkMQ/jKFzOnD2tzUoUhbXFgap6rAlwc+5T8NOCn5LwolM9K97qn8Zh6A/GdOmCkd0kXkUp155hb/lQucDjOk2HY9C77NV0lsl2MwkWUexifWXzuu9jYHzCgp5bSV6tTPqAARSZFkXZxa9wIzH9loEjOunVfbilruDUdAgMBAAECggEAO9PfWzzUVYMmivu8CqOFdl9r4fa3q7sYaTRjdTbgA0oZ/U8scHFtQi4wpk9THze0J6zJUGfkGlWI4JJHStxdBCH4VNyAMV8p2z/1Biq4sjEEHHTa1Bc0FJbwaWvJeQ0UYbBovg1BY7dU4hHKwNIh+n5YqQ1NR7Sal6DR63WJ/JN4lCAW84jySioozwwCa1Alh9osZDUAmZFkJT1xppjm4tnz4fGbctYxxU6h7zMd0DwzIHm3cxnHCVD6UA7tNhIoikYZYXgup9qfBH31Segu3SAyDIoOpNrfqi7e+VO8HUT+RvEHClGukrtCP5Kaz5ITyyFsixzkRFE3HFaScKKogQKBgQDNIp3IX95kG+cAt/W8UIcawD2S8hZdCYv/uZBrETr40wybypDcOP+5hnh4TMnf/GBjmtidK/eXSRaC8J2PxEQfYYcA7h0QlSPNIWLQj5cXBdaENw2znMDnjm06MLvNYTQp+2FLYrj2ECxvbOfs5e+mHcC1X56y0xEJGt2oUSsUPQKBgQC+n+q94JyHmmZ+XeX4y72sSF9VRieb/O6e4IPLaC2AbZCFSrzjRb5jO9dmM33WotGMEVpk6EuS2+nLOvUHrNjEjHVVhW6RLBrpoj4U3tDbveRkt6IUquWwwwaShkpUr+MCSZw/dCFTSYwQRFp8jFL1Sk6fTaD49EbiXS5pMjJSYQKBgQCJAmHX/xAOVQSI2lVqnW5fU57MkpyVL1cuScZHfI6iunNlanouXHOQelvkHYWH8IyAel/LOh54EPzUH9lcuH23Z/A/ksoe1T3AOFhDbGRBoEiSriKcoEnHXr1dbEdC+XVZxOhK7XkT/+Ft2pNdCoZxsNWRTng0f/yU1DjPvsbDEQKBgArj1+VBD7Avget2Mc0k1pEhEVEt0NV4falV6jGUogTUPY1f16qkMVw57vRHvMimRJJb624YVzjsl06k75qgpUGVRJ6+ILLqch8mxaUrlYL4NjQKyZekq7qNabK1OUndQnq++c4mNEEt0nKbdQ/odFTPCe7P4IJYcjQ8Xopj87wBAoGAJOUCWG7QjMEWH5AYzuaE0MRp9Ppw04NNSfRv9ffbpQWnx8XniVWMfYmIG7pt0dHtIZYNAK58HvkCw2gXeOKXwzAd7bfqKMbajKiZvKYamMFpTV2HrIK5rJ2wQ3YgegpUcvzqd8Yusy+t8IVRzfT2bn5WdXvKAs4ONE3co/Te8Ks=";


	public static String encoderClaimstar(Integer value) {
		if(value == null)
			return "";
		return encoderClaimstar(value.toString());
	}
	public static String encoderClaimstar(String value) {
		if(StringUtils.isEmpty(value))
			return "";
		String result = "" ;
//		result += generateRandomKey(2);
		try {
			result += BaseEncoding.base64().encode(value.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
//		result += generateRandomKey(3);
		return result;
	}
	public static String decoderClaimstar(String value) {
		if(StringUtils.isEmpty(value))
			return "";
//		value = value.substring(2, value.length() -3);
		byte[] contentInBytes = BaseEncoding.base64().decode(value);
		String result;
		try {
			result = new String(contentInBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return result ;
	}
	public static String decoderClaimstar(Integer value) {
		if(value ==null)
			return null;
		String valueStr = value.toString();
		String result = decoderClaimstar(valueStr);
	
			return result;
	
	}
	public static Integer decoderClaimstarIntValue(String value) {
		if(StringUtils.isEmpty(value))
			return null;
		String result = decoderClaimstar(value);
		try {
			return Integer.valueOf(result);
		} catch (Exception e) {
			return null;
		}
	}
	public static String generateRandomKey(int length){
		String alphabet = 
		        new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"); //9
		int n = alphabet.length(); //10

		String result = new String(); 
		Random r = new Random(); //11

		for (int i=0; i<length; i++) //12
		    result = result + alphabet.charAt(r.nextInt(n)); //13
		return result;
		}
	public static void main(String[] args) {
		String a = SecurityUtility.decoderClaimstar("NC43MDAwMDE5RTc=");
		System.out.println(a);
//		
//		String result ="";
//		for(int i=0 ;i <98; i++){
//			System.out.println("<div class=\"col\"><img src=\"image-frame/cat_face/cat_face"+i+".png\" ng-click=\"chooseSticker('image-frame/cat_face/cat_face"+i+".png')\"  class=\"fullscreen-image\" /></div>");
//		}
	}
}
