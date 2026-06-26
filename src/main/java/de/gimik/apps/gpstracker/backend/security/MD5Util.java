/**
 * 
 */
package de.gimik.apps.gpstracker.backend.security;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.gimik.apps.gpstracker.backend.util.HexUtil;

/**
 * @author dungnm
 * 
 */
public class MD5Util {
	public static String getMD5Hash(String message)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (message == null) {
			throw new IllegalArgumentException("message is null");
		}
		
		byte[] messageBytes = message.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] data = md.digest(messageBytes);

		return HexUtil.conventBytesToHexString(data);
	}
}
