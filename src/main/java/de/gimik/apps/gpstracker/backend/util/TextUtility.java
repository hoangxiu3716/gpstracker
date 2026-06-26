package de.gimik.apps.gpstracker.backend.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.io.BaseEncoding;

public class TextUtility {
	public static final boolean isNullOrEmpty(String text){
		if (text == null){
			return true;
		}
		
		return text.trim().equalsIgnoreCase("");
	}
	
	public static final String combineFilePath(String... filePaths){
		StringBuffer sb = new StringBuffer();
		
		for(String filePath : filePaths){
			sb.append(filePath).append("/");
		}
		
		return FilenameUtils.normalize(sb.toString());
	}
	
	public static final String trimStart(String text, String trimmingChars){
		return StringUtils.stripStart(text, trimmingChars);
	}
	
	public static final String trimEnd(String text, String trimmingChars){
		return StringUtils.stripEnd(text, trimmingChars);
	}
	
	public static final String trim(String text, String trimmingChars){
		return trimStart(trimEnd(text, trimmingChars), trimmingChars);
	}
	
	public static final String toSqlLikeValue(String text){
		return "%" + text + "%";
	}
	
	private final static String NON_THIN = "[^iIl1\\.,']";

	private static int textWidth(String str) {
	    return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
	}

	public static String ellipsize(String text, int max) {

	    if (textWidth(text) <= max)
	        return text;

	    // Start by chopping off at the word before max
	    // This is an over-approximation due to thin-characters...
	    int end = text.lastIndexOf(' ', max - 3);

	    // Just one long word. Chop it off.
	    if (end == -1)
	        return text.substring(0, max-3) + "...";

	    // Step forward as long as textWidth allows.
	    int newEnd = end;
	    do {
	        end = newEnd;
	        newEnd = text.indexOf(' ', end + 1);

	        // No more spaces.
	        if (newEnd == -1)
	            newEnd = text.length();

	    } while (textWidth(text.substring(0, newEnd) + "...") < max);

	    return text.substring(0, end) + "...";
	}
	

}
