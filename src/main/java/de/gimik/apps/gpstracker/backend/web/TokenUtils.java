package de.gimik.apps.gpstracker.backend.web;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.security.DefaultUserDetails;
import de.gimik.apps.gpstracker.backend.util.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;


public class TokenUtils {

    public static final String MAGIC_KEY = "B51393538257570A";
    private static final long EXPIRE_TIME = 1000L * 60 * 60 * 24; // 8 hours

    public static String createToken(UserDetails userDetails, String sessionType, Double sessionTime,String appCode) {
        /* Expires in one hour */
    	long expires= 0L;
    	if(StringUtils.isEmpty(sessionType) ||  Constants.Token.SESSION_TYPE_ONCE.equals(sessionType))
    		expires = System.currentTimeMillis() + EXPIRE_TIME * 90;
    	else {
    		if(sessionType.equals(Constants.Token.SESSION_TYPE_ONCE_DAY))
    			expires = System.currentTimeMillis() + EXPIRE_TIME ;
    		if(sessionType.equals(Constants.Token.SESSION_TYPE_SPECIFIED)) {
    			long addTime = (long) (EXPIRE_TIME * (sessionTime == null ? 90 : sessionTime));
    			expires = System.currentTimeMillis() + addTime;
    		}
    	}

        StringBuilder tokenBuilder = new StringBuilder();
        tokenBuilder.append(userDetails.getUsername());
        tokenBuilder.append(":");
        tokenBuilder.append(expires);
        tokenBuilder.append(":");
        tokenBuilder.append(TokenUtils.computeSignature(userDetails, expires));
    	tokenBuilder.append(":");
        tokenBuilder.append(appCode);
        tokenBuilder.append(":");
        tokenBuilder.append(buildRole(userDetails.getAuthorities()));
        
        return tokenBuilder.toString();
    }
    public static String createToken(UserDetails userDetails, String sessionType, Double sessionTime,String appCode,String pasword) {
        /* Expires in one hour */
    	long expires= 0L;
    	if(StringUtils.isEmpty(sessionType) ||  Constants.Token.SESSION_TYPE_ONCE.equals(sessionType))
    		expires = System.currentTimeMillis() + EXPIRE_TIME * 90;
    	else {
    		if(sessionType.equals(Constants.Token.SESSION_TYPE_ONCE_DAY))
    			expires = System.currentTimeMillis() + EXPIRE_TIME ;
    		if(sessionType.equals(Constants.Token.SESSION_TYPE_SPECIFIED)) {
    			long addTime = (long) (EXPIRE_TIME * (sessionTime == null ? 90 : sessionTime));
    			expires = System.currentTimeMillis() + addTime;
    		}
    	}

        StringBuilder tokenBuilder = new StringBuilder();
        tokenBuilder.append(userDetails.getUsername());
        tokenBuilder.append(":");
        tokenBuilder.append(expires);
        tokenBuilder.append(":");
        tokenBuilder.append(TokenUtils.computeSignature(userDetails, expires,pasword));
    	tokenBuilder.append(":");
        tokenBuilder.append(appCode);
        tokenBuilder.append(":");
        tokenBuilder.append(buildRole(userDetails.getAuthorities()));
        
        return tokenBuilder.toString();
    }

    public static String computeSignature(UserDetails userDetails, long expires) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(userDetails.getUsername());
        signatureBuilder.append(":");
        signatureBuilder.append(expires);
        signatureBuilder.append(":");
        signatureBuilder.append(userDetails.getPassword());
        signatureBuilder.append(":");
        signatureBuilder.append(TokenUtils.MAGIC_KEY);
       

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public static String computeSignature(UserDetails userDetails, long expires,String password) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(userDetails.getUsername());
        signatureBuilder.append(":");
        signatureBuilder.append(expires);
        signatureBuilder.append(":");
        signatureBuilder.append(password);
        signatureBuilder.append(":");
        signatureBuilder.append(TokenUtils.MAGIC_KEY);
       

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    
    public static String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }

        String[] parts = authToken.split(":");
        return parts[0];
    }


    public static boolean validateToken(String authToken, UserDetails userDetails) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];
//        if (expires < System.currentTimeMillis()) {
//        	return false;
//        }
//        String oldRole = parts[parts.length-1];
//        String role = buildRole(userDetails.getAuthorities());
//        if(!StringUtils.isEmpty(oldRole) && !StringUtils.isEmpty(role) && !oldRole.equals(role))
//        	return false;
        return signature.equals(TokenUtils.computeSignature(userDetails, expires));
    }
    
    public static void validateTokenFromRequest(String authToken, DefaultUserDetails userDetails, Date effectiveFrom, Date effectiveTo) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];
        if (expires < System.currentTimeMillis()) {
        	throw new BackendException(Constants.ErrorCode.TOKEN_TIMEOUT, Constants.ERROR_MESSAGE.TOKEN_TIMEOUT);
        }
        
        String oldRole = parts[4];
        String role = buildRole(userDetails.getAuthorities());
        if(!StringUtils.isEmpty(oldRole) && !StringUtils.isEmpty(role) && !oldRole.equals(role))
        	throw new BackendException(Constants.ErrorCode.ROLE_CHANGED, Constants.ERROR_MESSAGE.ROLE_CHANGED);
        
        if(!StringUtils.isEmpty(role) && role.equals(Constants.ROLE_GUEST) && effectiveFrom != null && effectiveTo !=null) {
        	Date sysDate = new Date();
        	if(sysDate.compareTo(effectiveFrom) < 0 || sysDate.compareTo(effectiveTo) > 0)
        		throw new BackendException(Constants.ErrorCode.APP_IS_EXPRIED, Constants.ERROR_MESSAGE.APP_IS_EXPRIED);
        }
        
        boolean checkToken = signature.equals(TokenUtils.computeSignature(userDetails, expires));
        if(!checkToken)
        	throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
    }
    public static String buildRole(  Collection<? extends GrantedAuthority> grantedAuthorities) {
    	String role ="";
    	for(GrantedAuthority grantedAuthority : grantedAuthorities){
    		role = grantedAuthority.getAuthority();
	    }
    	return role;
    }
}