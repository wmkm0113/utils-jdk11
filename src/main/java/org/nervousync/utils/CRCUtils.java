/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.utils;

import org.nervousync.exceptions.crc.CRCException;

import java.util.*;

/**
 * CRC3 to CRC32 Calculator
 *
 * Support algorithms:
 * CRC-3/GSM, CRC-3/ROHC, CRC-4/G-704, CRC-4/INTERLAKEN, CRC-5/EPC-C1G2, CRC-5/G-704, CRC-5/USB, CRC-6/CDMA2000-A,
 * CRC-6/CDMA2000-B, CRC-6/DARC, CRC-6/G-704, CRC-6/GSM, CRC-7/MMC, CRC-7/ROHC, CRC-7/UMTS, CRC-8/AUTOSAR,
 * CRC-8/BLUETOOTH, CRC-8/CDMA2000, CRC-8/DARC, CRC-8/DVB-S2, CRC-8/GSM-A, CRC-8/GSM-B, CRC-8/I-432-1, CRC-8/I-CODE,
 * CRC-8/LTE, CRC-8/MAXIM-DOW, CRC-8/MIFARE-MAD, CRC-8/NRSC-5, CRC-8/OPENSAFETY, CRC-8/ROHC, CRC-8/SAE-J1850, CRC-8/SMBUS,
 * CRC-8/TECH-3250, CRC-8/WCDMA, CRC-10/ATM, CRC-10/CDMA2000, CRC-10/GSM, CRC-11/FLEXRAY, CRC-11/UMTS, CRC-12/CDMA2000,
 * CRC-12/DECT, CRC-12/GSM, CRC-12/UMTS, CRC-13/BBC, CRC-14/DARC, CRC-14/GSM, CRC-15/CAN, CRC-15/MPT1327, CRC-16/ARC,
 * CRC-16/CDMA2000, CRC-16/CMS, CRC-16/DDS-110, CRC-16/DECT-R, CRC-16/DECT-X, CRC-16/DNP, CRC-16/EN-13757, CRC-16/GENIBUS,
 * CRC-16/GSM, CRC-16/IBM-3740, CRC-16/IBM-SDLC, CRC-16/ISO-IEC-14443-3-A, CRC-16/KERMIT, CRC-16/LJ1200, CRC-16/MAXIM-DOW,
 * CRC-16/MCRF4XX, CRC-16/MODBUS, CRC-16/NRSC-5, CRC-16/OPENSAFETY-A, CRC-16/OPENSAFETY-B, CRC-16/PROFIBUS, CRC-16/RIELLO,
 * CRC-16/SPI-FUJITSU, CRC-16/T10-DIF, CRC-16/TELEDISK, CRC-16/TMS37157, CRC-16/UMTS, CRC-16/USB, CRC-16/XMODEM,
 * CRC-17/CAN-FD, CRC-21/CAN-FD, CRC-24/BLE, CRC-24/FLEXRAY-A, CRC-24/FLEXRAY-B, CRC-24/INTERLAKEN, CRC-24/LTE-A,
 * CRC-24/LTE-B, CRC-24/OPENPGP, CRC-24/OS-9, CRC-30/CDMA, CRC-31/PHILIPS, CRC-32/AIXM, CRC-32/AUTOSAR, CRC-32/BASE91-D,
 * CRC-32/BZIP2, CRC-32/CD-ROM-EDC, CRC-32/CKSUM, CRC-32/ISCSI, CRC-32/ISO-HDLC, CRC-32/JAMCRC, CRC-32/MPEG-2, CRC-32/XFER
 *
 * Support register algorithm
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 4/10/2020 3:04 PM $
 */
public final class CRCUtils {

	/**
	 * Registered CRC config map
	 */
	private static final Hashtable<String, CRCConfig> REGISTERED_CRC_CONFIG = new Hashtable<>();
	
	static {
		CRCUtils.registerAlgorithm("CRC-3/GSM", 3, 0x3, 0x0, 0x7, false, false);
		CRCUtils.registerAlgorithm("CRC-3/ROHC", 3, 0x3, 0x7, 0x0, true, true);
		CRCUtils.registerAlgorithm("CRC-4/G-704", 4, 0x3, 0x0, 0x0, true, true);
		CRCUtils.registerAlgorithm("CRC-4/INTERLAKEN", 4, 0x3, 0xF, 0xF, false, false);
		CRCUtils.registerAlgorithm("CRC-5/EPC-C1G2", 5, 0x09, 0x09, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-5/G-704", 5, 0x15, 0x00, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-5/USB", 5, 0x05, 0x1F, 0x1F, true, true);
		CRCUtils.registerAlgorithm("CRC-6/CDMA2000-A", 6, 0x27, 0x3F, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-6/CDMA2000-B", 6, 0x07, 0x3F, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-6/DARC", 6, 0x19, 0x00, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-6/G-704", 6, 0x03, 0x00, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-6/GSM", 6, 0x2F, 0x00, 0x3F, false, false);
		CRCUtils.registerAlgorithm("CRC-7/MMC", 7, 0x09, 0x00, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-7/ROHC", 7, 0x4F, 0x7F, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-7/UMTS", 7, 0x45, 0x00, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/AUTOSAR", 8, 0x2F, 0xFF, 0xFF, false, false);
		CRCUtils.registerAlgorithm("CRC-8/BLUETOOTH", 8, 0xA7, 0x00, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-8/CDMA2000", 8, 0x9B, 0xFF, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/DARC", 8, 0x39, 0x00, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-8/DVB-S2", 8, 0xD5, 0x00, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/GSM-A", 8, 0x1D, 0x00, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/GSM-B", 8, 0x49, 0x00, 0xFF, false, false);
		CRCUtils.registerAlgorithm("CRC-8/I-432-1", 8, 0x07, 0x00, 0x55, false, false);
		CRCUtils.registerAlgorithm("CRC-8/I-CODE", 8, 0x1D, 0xFD, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/LTE", 8, 0x9B, 0x00, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/MAXIM-DOW", 8, 0x31, 0x00, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-8/MIFARE-MAD", 8, 0x1D, 0xC7, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/NRSC-5", 8, 0x31, 0xFF, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/OPENSAFETY", 8, 0x2F, 0x00, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/ROHC", 8, 0x07, 0xFF, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-8/SAE-J1850", 8, 0x1D, 0xFF, 0xFF, false, false);
		CRCUtils.registerAlgorithm("CRC-8/SMBUS", 8, 0x07, 0x00, 0x00, false, false);
		CRCUtils.registerAlgorithm("CRC-8/TECH-3250", 8, 0x1D, 0xFF, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-8/WCDMA", 8, 0x9B, 0x00, 0x00, true, true);
		CRCUtils.registerAlgorithm("CRC-10/ATM", 10, 0x233, 0x000, 0x000, false, false);
		CRCUtils.registerAlgorithm("CRC-10/CDMA2000", 10, 0x3D9, 0x3FF, 0x000, false, false);
		CRCUtils.registerAlgorithm("CRC-10/GSM", 10, 0x175, 0x000, 0x3FF, false, false);
		CRCUtils.registerAlgorithm("CRC-11/FLEXRAY", 11, 0x385, 0x01A, 0x000, false, false);
		CRCUtils.registerAlgorithm("CRC-11/UMTS", 11, 0x307, 0x000, 0x000, false, false);
		CRCUtils.registerAlgorithm("CRC-12/CDMA2000", 12, 0xF13, 0xFFF, 0x000, false, false);
		CRCUtils.registerAlgorithm("CRC-12/DECT", 12, 0x80F, 0x000, 0x000, false, false);
		CRCUtils.registerAlgorithm("CRC-12/GSM", 12, 0xD31, 0x000, 0xFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-12/UMTS", 12, 0x80F, 0x000, 0x000, false, true);
		CRCUtils.registerAlgorithm("CRC-13/BBC", 13, 0x1CF5, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-14/DARC", 14, 0x0805, 0x0000, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-14/GSM", 14, 0x202D, 0x0000, 0x3FFF, false, false);
		CRCUtils.registerAlgorithm("CRC-15/CAN", 15, 0x4599, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-15/MPT1327", 15, 0x6815, 0x0000, 0x0001, false, false);
		CRCUtils.registerAlgorithm("CRC-16/ARC", 16, 0x8005, 0x0000, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/CDMA2000", 16, 0xC867, 0xFFFF, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/CMS", 16, 0x8005, 0xFFFF, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/DDS-110", 16, 0x8005, 0x800D, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/DECT-R", 16, 0x0589, 0x0000, 0x0001, false, false);
		CRCUtils.registerAlgorithm("CRC-16/DECT-X", 16, 0x0589, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/DNP", 16, 0x3D65, 0x0000, 0xFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-16/EN-13757", 16, 0x3D65, 0x0000, 0xFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-16/GENIBUS", 16, 0x1021, 0xFFFF, 0xFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-16/GSM", 16, 0x1021, 0x0000, 0xFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-16/IBM-3740", 16, 0x1021, 0xFFFF, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/IBM-SDLC", 16, 0x1021, 0xFFFF, 0xFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-16/ISO-IEC-14443-3-A", 16, 0x1021, 0xC6C6, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/KERMIT", 16, 0x1021, 0x0000, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/LJ1200", 16, 0x6F63, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/MAXIM-DOW", 16, 0x8005, 0x0000, 0xFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-16/MCRF4XX", 16, 0x1021, 0xFFFF, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/MODBUS", 16, 0x8005, 0xFFFF, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/NRSC-5", 16, 0x080B, 0xFFFF, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/OPENSAFETY-A", 16, 0x5935, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/OPENSAFETY-B", 16, 0x755B, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/PROFIBUS", 16, 0x1DCF, 0xFFFF, 0xFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-16/RIELLO", 16, 0x1021, 0xB2AA, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/SPI-FUJITSU", 16, 0x1021, 0x1D0F, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/T10-DIF", 16, 0x8BB7, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/TELEDISK", 16, 0xA097, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/TMS37157", 16, 0x1021, 0x89EC, 0x0000, true, true);
		CRCUtils.registerAlgorithm("CRC-16/UMTS", 16, 0x8005, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-16/USB", 16, 0x8005, 0xFFFF, 0xFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-16/XMODEM", 16, 0x1021, 0x0000, 0x0000, false, false);
		CRCUtils.registerAlgorithm("CRC-17/CAN-FD", 17, 0x1685B, 0x00000, 0x00000, false, false);
		CRCUtils.registerAlgorithm("CRC-21/CAN-FD", 21, 0x102899, 0x000000, 0x000000, false, false);
		CRCUtils.registerAlgorithm("CRC-24/BLE", 24, 0x00065B, 0x555555, 0x000000, true, true);
		CRCUtils.registerAlgorithm("CRC-24/FLEXRAY-A", 24, 0x5D6DCB, 0xFEDCBA, 0x000000, false, false);
		CRCUtils.registerAlgorithm("CRC-24/FLEXRAY-B", 24, 0x5D6DCB, 0xABCDEF, 0x000000, false, false);
		CRCUtils.registerAlgorithm("CRC-24/INTERLAKEN", 24, 0x328B63, 0xFFFFFF, 0xFFFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-24/LTE-A", 24, 0x864CFB, 0x000000, 0x000000, false, false);
		CRCUtils.registerAlgorithm("CRC-24/LTE-B", 24, 0x800063, 0x000000, 0x000000, false, false);
		CRCUtils.registerAlgorithm("CRC-24/OPENPGP", 24, 0x864CFB, 0xB704CE, 0x000000, false, false);
		CRCUtils.registerAlgorithm("CRC-24/OS-9", 24, 0x800063, 0xFFFFFF, 0xFFFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-30/CDMA", 30, 0x2030B9C7, 0x3FFFFFFF, 0x3FFFFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-31/PHILIPS", 31, 0x04C11DB7, 0x7FFFFFFF, 0x7FFFFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-32/AIXM", 32, 0x814141AB, 0x00000000, 0x00000000, false, false);
		CRCUtils.registerAlgorithm("CRC-32/AUTOSAR", 32, 0xF4ACFB13, 0xFFFFFFFF, 0xFFFFFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-32/BASE91-D", 32, 0xA833982B, 0xFFFFFFFF, 0xFFFFFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-32/BZIP2", 32, 0x04C11DB7, 0xFFFFFFFF, 0xFFFFFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-32/CD-ROM-EDC", 32, 0x8001801B, 0x00000000, 0x00000000, true, true);
		CRCUtils.registerAlgorithm("CRC-32/CKSUM", 32, 0x04C11DB7, 0x00000000, 0xFFFFFFFF, false, false);
		CRCUtils.registerAlgorithm("CRC-32/ISCSI", 32, 0x1EDC6F41, 0xFFFFFFFF, 0xFFFFFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-32/ISO-HDLC", 32, 0x04C11DB7, 0xFFFFFFFF, 0xFFFFFFFF, true, true);
		CRCUtils.registerAlgorithm("CRC-32/JAMCRC", 32, 0x04C11DB7, 0xFFFFFFFF, 0x00000000, true, true);
		CRCUtils.registerAlgorithm("CRC-32/MPEG-2", 32, 0x04C11DB7, 0xFFFFFFFF, 0x00000000, false, false);
		CRCUtils.registerAlgorithm("CRC-32/XFER", 32, 0x000000AF, 0x00000000, 0x00000000, false, false);
	}

	private CRCUtils() {
	}

	/**
	 * Check register status of given algorithm
	 * @param algorithm     CRC algorithm name
	 * @return              Check result
	 */
	public static boolean existsAlgorithm(String algorithm) {
		return REGISTERED_CRC_CONFIG.containsKey(algorithm);
	}

	/**
	 * Registered algorithm name list
	 * @return  Algorithm name list
	 */
	public static List<String> registeredAlgorithms() {
		List<String> algorithms = new ArrayList<>(REGISTERED_CRC_CONFIG.size());
		algorithms.addAll(REGISTERED_CRC_CONFIG.keySet());
		algorithms.sort(String::compareTo);
		return algorithms;
	}

	/**
	 * Register CRC algorithm
	 * @param algorithm         Algorithm name
	 * @param bit               CRC bit
	 * @param polynomial        CRC polynomial
	 * @param init              Init value
	 * @param xorOut            XorOut value
	 * @param refIn             Reverse data bytes
	 * @param refOut            Reverse CRC result before final XOR
	 * @throws CRCException     CRC bit is lager than 32, or algorithm name was exists
	 */
	public static void registerAlgorithm(String algorithm, int bit, long polynomial, long init, long xorOut,
	                                     boolean refIn, boolean refOut) throws CRCException {
		if (bit > 32) {
			throw new CRCException("Cannot calculate CRC value lager than 32 bit");
		}
		if (REGISTERED_CRC_CONFIG.containsKey(algorithm)) {
			throw new CRCException("Algorithm name: " + algorithm + " was exists!");
		}
		REGISTERED_CRC_CONFIG.put(algorithm, new CRCConfig(bit, polynomial, init, xorOut, refIn, refOut));
	}

	/**
	 * Initialize CRC adapter instance using given algorithm
	 * @param algorithm     CRC algorithm name
	 * @return              CRC adapter instance
	 * @throws CRCException CRC algorithm can not found
	 */
	public static CRCAdapter initialize(String algorithm) throws CRCException {
		if (!REGISTERED_CRC_CONFIG.containsKey(algorithm)) {
			throw new CRCException("CRC algorithm: " + algorithm + " not registered! ");
		}
		return new CRC(REGISTERED_CRC_CONFIG.get(algorithm));
	}

	/**
	 * Calculate crc value of string data using given algorithms
	 * @param algorithm     CRC algorithm name
	 * @param data          String data
	 * @return              CRC result
	 * @throws CRCException CRC algorithm can not found
	 */
	public static String calculate(String algorithm, String data) throws CRCException {
		CRCAdapter crcAdapter = CRCUtils.initialize(algorithm);
		crcAdapter.append(data);
		return crcAdapter.finish();
	}

	/**
	 * Calculate crc value of data bytes using given algorithms
	 * @param algorithm     CRC algorithm name
	 * @param dataBytes     Data bytes
	 * @return              CRC result
	 * @throws CRCException CRC algorithm can not found
	 */
	public static String calculate(String algorithm, byte[] dataBytes) throws CRCException {
		CRCAdapter crcAdapter = CRCUtils.initialize(algorithm);
		crcAdapter.appendBinary(dataBytes);
		return crcAdapter.finish();
	}

	/**
	 * CRC adapter interface
	 */
	public interface CRCAdapter {

		/**
		 * Append string data
		 * @param string    string data
		 */
		void append(String string);

		/**
		 * Append binary data bytes
		 * @param dataBytes data bytes
		 */
		void appendBinary(byte[] dataBytes);

		/**
		 * Finish append data and calculate crc result
		 * @return  crc result(Hex string)
		 */
		String finish();

		/**
		 * Reset the crc value to initialize value
		 */
		void reset();
	}

	/**
	 * CRC3 to CRC32 implement class
	 */
	private static final class CRC implements CRCAdapter {

		//  Current crc config
		private final CRCConfig crcConfig;
		//  Polynomial
		private final long polynomial;
		//  Initialize CRC Value
		private final long init;
		//  Check Value
		private final long check;
		//  Mask Value
		private final long mask;
		//  CRC Value
		private long crc;

		/**
		 * Constructor for initialize CRC implement
		 * @param crcConfig     CRC Config Information
		 */
		public CRC(CRCConfig crcConfig) {
			this.crcConfig = crcConfig;
			if (this.crcConfig.isRefIn()) {
				this.polynomial = CRCUtils.CRC.reverseBit(this.crcConfig.getPolynomial(), this.crcConfig.getBit());
				this.init = CRCUtils.CRC.reverseBit(this.crcConfig.getInit(), this.crcConfig.getBit());
			} else {
				this.polynomial = (this.crcConfig.getBit() < 8)
						? (this.crcConfig.getPolynomial() << (8 - this.crcConfig.getBit()))
						: this.crcConfig.getPolynomial();
				this.init = (this.crcConfig.getBit() < 8)
						? (this.crcConfig.getInit() << (8 - this.crcConfig.getBit()))
						: this.crcConfig.getInit();
			}
			this.crc = this.init;
			if (this.crcConfig.isRefIn()) {
				this.check = 0x1L;
			} else {
				if (this.crcConfig.getBit() <= 8) {
					this.check = 0x80;
				} else {
					this.check = Double.valueOf(Math.pow(2, this.crcConfig.getBit() - 1)).longValue();
				}
			}
			if (this.crcConfig.getBit() <= 8) {
				this.mask = 0xFF;
			} else {
				StringBuilder stringBuilder = new StringBuilder();
				while (stringBuilder.length() < this.crcConfig.getBit()) {
					stringBuilder.append("1");
				}
				this.mask = Long.valueOf(stringBuilder.toString(), 2);
			}
		}

		/**
		 * Append string data
		 * @param string    string data
		 */
		public void append(String string) {
			this.appendBinary(string.getBytes());
		}

		/**
		 * Append binary data bytes
		 * @param dataBytes data bytes
		 */
		public void appendBinary(byte[] dataBytes) {
			for (byte dataByte : dataBytes) {
				int crc = (dataByte < 0) ? ((int)dataByte) + 256 : dataByte;
				if (this.crcConfig.getBit() <= 8) {
					this.crc ^= crc;
				} else {
					this.crc ^= ((this.crcConfig.isRefIn() ? crc : (crc << (this.crcConfig.getBit() - 8)))
							& this.mask);
				}

				for (int j = 0; j < 8; j++) {
					if ((this.crc & this.check) > 0) {
						this.crc = (this.crcConfig.isRefIn() ? (this.crc >>> 1) : (this.crc << 1)) ^ this.polynomial;
					} else {
						this.crc = this.crcConfig.isRefIn() ? (this.crc >>> 1) : (this.crc << 1);
					}
				}
			}
			this.crc &= this.mask;
		}

		/**
		 * Finish append data and calculate crc result
		 * @return  crc result(Hex string)
		 */
		public String finish() {
			if (this.crcConfig.getBit() < 8 && !this.crcConfig.isRefIn()) {
				this.crc >>>= (8 - this.crcConfig.getBit());
			}
			if (!Objects.equals(this.crcConfig.isRefIn(), this.crcConfig.isRefOut()) && this.crcConfig.isRefOut()) {
				//  Just using for CRC-12/UMTS
				this.crc &= this.mask;
				this.crc = (CRCUtils.CRC.reverseBit(this.crc, Long.toString(this.crc, 2).length())
						^ this.crcConfig.getXorOut());
			} else {
				this.crc = (this.crc ^ this.crcConfig.getXorOut()) & this.mask;
			}
			StringBuilder result = new StringBuilder(Long.toString(this.crc, 16));
			while (result.length() < this.crcConfig.getOutLength()) {
				result.insert(0, "0");
			}
			this.reset();
			return "0x" + result.toString();
		}

		/**
		 * Reset the crc value to initialize value
		 */
		public void reset() {
			this.crc = this.init;
		}

		/**
		 * Reverse data
		 * @param value     input data
		 * @param bit       crc bit
		 * @return          reversed data
		 */
		private static long reverseBit(long value, int bit) {
			if (value < 0) {
				value += Math.pow(2, bit);
			}
			String reverseValue = new StringBuilder(Long.toString(value, 2)).reverse().toString();
			if (reverseValue.length() < bit) {
				StringBuilder stringBuilder = new StringBuilder(reverseValue);
				while (stringBuilder.length() < bit) {
					stringBuilder.append("0");
				}
				reverseValue = stringBuilder.toString();
			} else {
				reverseValue = reverseValue.substring(0, bit);
			}
			return Long.parseLong(reverseValue, 2);
		}
	}

	/**
	 * CRC config
	 */
	private static final class CRCConfig {

		//  CRC bit
		private final int bit;
		//  CRC polynomial
		private final long polynomial;
		//  CRC initialize value
		private final long init;
		//  CRC XOR out value
		private final long xorOut;
		//  CRC output data length
		private final int outLength;
		//  Reverse data bytes
		private final boolean refIn;
		//  Reverse CRC result before final XOR
		private final boolean refOut;

		/**
		 * Constructor
		 * @param bit               CRC bit
		 * @param polynomial        CRC polynomial
		 * @param init              Init value
		 * @param xorOut            XorOut value
		 * @param refIn             Reverse data bytes
		 * @param refOut            Reverse CRC result before final XOR
		 */
		public CRCConfig(int bit, long polynomial, long init, long xorOut, boolean refIn, boolean refOut) {
			this.bit = bit;
			this.polynomial = polynomial;
			this.init = init;
			this.xorOut = xorOut;
			this.outLength = (bit % 4 != 0) ? ((bit / 4) + 1) : (bit / 4);
			this.refIn = refIn;
			this.refOut = refOut;
		}

		/**
		 * Gets the value of bit
		 *
		 * @return the value of bit
		 */
		public int getBit() {
			return bit;
		}

		/**
		 * Gets the value of polynomial
		 *
		 * @return the value of polynomial
		 */
		public long getPolynomial() {
			return polynomial;
		}

		/**
		 * Gets the value of init
		 *
		 * @return the value of init
		 */
		public long getInit() {
			return init;
		}

		/**
		 * Gets the value of xorOut
		 *
		 * @return the value of xorOut
		 */
		public long getXorOut() {
			return xorOut;
		}

		/**
		 * Gets the value of outLength
		 *
		 * @return the value of outLength
		 */
		public int getOutLength() {
			return outLength;
		}

		/**
		 * Gets the value of refIn
		 *
		 * @return the value of refIn
		 */
		public boolean isRefIn() {
			return refIn;
		}

		/**
		 * Gets the value of refOut
		 *
		 * @return the value of refOut
		 */
		public boolean isRefOut() {
			return refOut;
		}
	}
}
