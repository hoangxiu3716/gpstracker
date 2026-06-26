package de.gimik.apps.gpstracker.backend.util;

public class NumberUtility {
	public static float round(float value, int decimalPlace) {
		float p = (float) Math.pow(10, decimalPlace);
		value = value * p;
		float tmp = Math.round(value);
		return (float) tmp / p;
	}
}
