package org.nervousync.test.otp;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.OTPUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class OTPTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(OTPTest.class);

	private static String RANDOM_KEY = Globals.DEFAULT_VALUE_STRING;

	@Test
	public void test000GenerateRandomKey() {
		String randomKey = OTPUtils.generateRandomKey();
		Assert.assertNotNull(randomKey);
		LOGGER.info("Generate random key: {}", randomKey);
		//  Test generate random key by given size
		RANDOM_KEY = OTPUtils.generateRandomKey(16);
		Assert.assertNotNull(RANDOM_KEY);
		LOGGER.info("Generate size {} random key: {}", 16, RANDOM_KEY);
	}

	@Test
	public void test010HOTP() {
		int generateCode = OTPUtils.generateHOTPCode(RANDOM_KEY, 0L);
		Assert.assertTrue(generateCode != Globals.DEFAULT_VALUE_INT);
		LOGGER.info("Generate code key: {}", generateCode);
		Assert.assertTrue(OTPUtils.validateHOTPCode(generateCode, RANDOM_KEY, 0L));
		//  HmacSHA256
		generateCode = OTPUtils.generateHOTPCode(OTPUtils.CalcType.HmacSHA256, RANDOM_KEY, 0L);
		Assert.assertTrue(generateCode != Globals.DEFAULT_VALUE_INT);
		LOGGER.info("Generate HmacSHA256 code key: {}", generateCode);
		Assert.assertTrue(OTPUtils.validateHOTPCode(generateCode, OTPUtils.CalcType.HmacSHA256, RANDOM_KEY, 0L));
		//  HmacSHA256
		generateCode = OTPUtils.generateHOTPCode(OTPUtils.CalcType.HmacSHA512, RANDOM_KEY, 0L);
		Assert.assertTrue(generateCode != Globals.DEFAULT_VALUE_INT);
		LOGGER.info("Generate HmacSHA512 code key: {}", generateCode);
		Assert.assertFalse(OTPUtils.validateHOTPCode(generateCode, OTPUtils.CalcType.HmacSHA256, RANDOM_KEY, 0L));
	}

	@Test
	public void test020TOTP() {
		String generateCode = OTPUtils.generateTOTPCode(RANDOM_KEY);
		Assert.assertTrue(StringUtils.notBlank(generateCode));
		LOGGER.info("Generate TOTP code key: {}", generateCode);
		long fixTime = OTPUtils.calculateFixedTime(RANDOM_KEY, Integer.parseInt(generateCode));
		LOGGER.info("Calculate fixed time: {}", fixTime);
		Assert.assertTrue(OTPUtils.validateTOTPCode(Integer.parseInt(generateCode), RANDOM_KEY, fixTime));
		generateCode = OTPUtils.generateTOTPCode(RANDOM_KEY, fixTime);
		LOGGER.info("Generate fixed time: {} code key: {}", fixTime, generateCode);
		Assert.assertTrue(OTPUtils.validateTOTPCode(Integer.parseInt(generateCode), RANDOM_KEY, fixTime, 60));
		//  Fixed time
		long currentFixedTime = -8 * 60 * 60 * 1000L;
		generateCode = OTPUtils.generateTOTPCode(RANDOM_KEY, currentFixedTime, 1);
		Assert.assertTrue(StringUtils.notBlank(generateCode));
		LOGGER.info("Generate fixed time: {} code key: {}", currentFixedTime, generateCode);
		long calcFixedTime = OTPUtils.calculateFixedTime(RANDOM_KEY, Integer.parseInt(generateCode), 1);
		LOGGER.info("Calculate fixed time: {} code key: {}", calcFixedTime, generateCode);
		Assert.assertTrue(OTPUtils.validateTOTPCode(Integer.parseInt(generateCode), OTPUtils.CalcType.HmacSHA1, RANDOM_KEY, calcFixedTime, 1, 60));
		//  Generate by sync count
		generateCode = OTPUtils.generateTOTPCode(RANDOM_KEY, currentFixedTime, 60);
		Assert.assertTrue(StringUtils.notBlank(generateCode));
		LOGGER.info("Generate fixed time: {} code key: {}", currentFixedTime, generateCode);
		Assert.assertTrue(OTPUtils.validateTOTPCode(Integer.parseInt(generateCode), OTPUtils.CalcType.HmacSHA1, RANDOM_KEY, currentFixedTime, 60, 1));
		//  HmacSHA256
		generateCode = OTPUtils.generateTOTPCode(OTPUtils.CalcType.HmacSHA256, RANDOM_KEY, fixTime);
		Assert.assertTrue(StringUtils.notBlank(generateCode));
		LOGGER.info("Generate HmacSHA256 code key: {}", generateCode);
		Assert.assertTrue(OTPUtils.validateTOTPCode(Integer.parseInt(generateCode), OTPUtils.CalcType.HmacSHA256, RANDOM_KEY, fixTime, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT));
		calcFixedTime = OTPUtils.calculateFixedTime(OTPUtils.CalcType.HmacSHA256, RANDOM_KEY, Integer.parseInt(generateCode));
		LOGGER.info("Calculate fixed time: {} code key: {}", calcFixedTime, generateCode);
		//  HmacSHA256
		generateCode = OTPUtils.generateTOTPCode(OTPUtils.CalcType.HmacSHA512, RANDOM_KEY, fixTime);
		Assert.assertTrue(StringUtils.notBlank(generateCode));
		LOGGER.info("Generate HmacSHA512 code key: {}", generateCode);
		Assert.assertFalse(OTPUtils.validateTOTPCode(Integer.parseInt(generateCode), OTPUtils.CalcType.HmacSHA256, RANDOM_KEY, fixTime, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT));
	}
}
