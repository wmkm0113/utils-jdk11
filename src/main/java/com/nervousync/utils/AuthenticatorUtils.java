package com.nervousync.utils;

import com.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2019-06-04 10:47 $
 */
public final class AuthenticatorUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatorUtils.class);

	//  Unit: Second
	private static final int DEFAULT_SYNC_COUNT = 30;

	//  Default 3, Maximum 17 (From Google Docs)
	private static final int DEFAULT_WINDOW_SIZE = 3;
	private static final int DEFAULT_SECRET_SIZE = 10;
	private static final String DEFAULT_SECRET_SEED = "TmVydm91c3luY0RlZmF1bHRTZWNyZXRTZWVk";
	private static final String DEFAULT_RANDOM_ALGORITHM = "SHA1PRNG";

	public static long calculateFixedTime(String randomKey, int authCode) {
		return calculateFixedTime(CalcType.HmacSHA1, randomKey, authCode, Globals.DEFAULT_VALUE_INT);
	}

	public static long calculateFixedTime(String randomKey, int authCode, int syncCount) {
		return calculateFixedTime(CalcType.HmacSHA1, randomKey, authCode, syncCount);
	}

	public static long calculateFixedTime(CalcType calcType, String randomKey, int authCode) {
		return calculateFixedTime(calcType, randomKey, authCode, Globals.DEFAULT_VALUE_INT);
	}

	public static long calculateFixedTime(CalcType calcType, String randomKey, int authCode, int syncCount) {
		for (int i = -12 ; i <= 12 ; i++) {
			long fixedTime = i * 60 * 60 * 1000L;
			if (validateAuthenticatorCode(calcType, authCode, randomKey, fixedTime, syncCount, Globals.INITIALIZE_INT_VALUE)) {
				return fixedTime;
			}
		}
		return Globals.DEFAULT_VALUE_INT;
	}

	public static String generateAuthenticatorCode(String secret, long fixedTime) {
		return generateAuthenticatorCode(CalcType.HmacSHA1, secret, fixedTime, Globals.DEFAULT_VALUE_INT);
	}

	public static String generateAuthenticatorCode(String secret, long fixedTime, int syncCount) {
		return generateAuthenticatorCode(CalcType.HmacSHA1, secret, fixedTime, syncCount);
	}

	public static String generateAuthenticatorCode(CalcType calcType, String secret, long fixedTime) {
		return generateAuthenticatorCode(calcType, secret, fixedTime, Globals.DEFAULT_VALUE_INT);
	}

	public static String generateAuthenticatorCode(CalcType calcType, String secret, long fixedTime, int syncCount) {
		int authCode = AuthenticatorUtils.generateAuthenticatorCode(calcType, secret,
				fixedTime, syncCount, Globals.INITIALIZE_INT_VALUE);
		if (authCode == Globals.DEFAULT_VALUE_INT) {
			return Globals.DEFAULT_VALUE_STRING;
		}

		StringBuilder returnCode = new StringBuilder(Integer.toString(authCode));
		while (returnCode.length() < 6) {
			returnCode.insert(0, "0");
		}
		return returnCode.toString();
	}

	public static String generateRandomKey() {
		return generateRandomKey(null, null, Globals.DEFAULT_VALUE_INT);
	}

	public static String generateRandomKey(int size) {
		return generateRandomKey(null, null, size);
	}

	public static String generateRandomKey(String algorithm, String seed, int size) {
		String randomKey = null;
		try {
			SecureRandom secureRandom =
					SecureRandom.getInstance(algorithm == null ? DEFAULT_RANDOM_ALGORITHM : algorithm);
			secureRandom.setSeed(StringUtils.base64Decode(seed == null ? DEFAULT_SECRET_SEED : seed));
			byte[] randomKeyBytes =
					secureRandom.generateSeed(size == Globals.DEFAULT_VALUE_INT ? DEFAULT_SECRET_SIZE : size);
			randomKey = StringUtils.base32Encode(randomKeyBytes);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Generate random key error!");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
		}
		return randomKey;
	}

	public static boolean validateAuthenticatorCode(int authCode, String randomKey, long fixedTime) {
		return validateAuthenticatorCode(CalcType.HmacSHA1, authCode, randomKey,
				fixedTime, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
	}

	public static boolean validateAuthenticatorCode(int authCode, String randomKey, long fixedTime, int fixWindow) {
		return validateAuthenticatorCode(CalcType.HmacSHA1, authCode, randomKey,
				fixedTime, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
	}

	public static boolean validateAuthenticatorCode(CalcType calcType, int authCode,
	                                                String randomKey, long fixedTime, int syncCount, int fixWindow) {
		if (authCode > Globals.INITIALIZE_INT_VALUE) {
			int minWindow = fixWindow < 0 ? (-1 * DEFAULT_WINDOW_SIZE) : (-1 * fixWindow);
			int maxWindow = fixWindow < 0 ? DEFAULT_WINDOW_SIZE : fixWindow;
			for (int i = minWindow ; i <= maxWindow ; i++) {
				int generateCode = generateAuthenticatorCode(calcType, randomKey, fixedTime, syncCount, i);
				if (generateCode == authCode) {
					return true;
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	private static int generateAuthenticatorCode(CalcType calcType, String randomKey,
	                                             long fixedTime, int syncCount, int fixWindow) {
		long currentGMTTime = DateTimeUtils.currentGMTTimeMillis();
		long calcTime = (currentGMTTime + fixedTime) / 1000L
				/ (syncCount == Globals.DEFAULT_VALUE_INT ? DEFAULT_SYNC_COUNT : syncCount);
		calcTime += fixWindow;
		byte[] signData = new byte[8];
		for (int i = 8 ; i-- > 0 ; calcTime >>>= 8) {
			signData[i] = (byte)calcTime;
		}
		byte[] secret = StringUtils.base32Decode(randomKey);
		byte[] hash;
		switch (calcType) {
			case HmacSHA1:
				hash = SecurityUtils.SignDataByHmacSHA1(secret, signData);
				break;
			case HmacSHA256:
				hash = SecurityUtils.SignDataByHmacSHA256(secret, signData);
				break;
			case HmacSHA512:
				hash = SecurityUtils.SignDataByHmacSHA512(secret, signData);
				break;
				default:
					return Globals.DEFAULT_VALUE_INT;
		}
		int offset = hash[hash.length - 1] & 0xF;
		long resultCode = 0L;
		for (int i = 0 ; i < 4 ; ++i) {
			resultCode = (resultCode << 8) | (hash[offset + i] & 0xFF);
		}
		resultCode &= 0x7FFFFFFF;
		resultCode %= 1000000;
		return (int)resultCode;
	}

	public enum CalcType {
		HmacSHA1, HmacSHA256, HmacSHA512
	}
}
