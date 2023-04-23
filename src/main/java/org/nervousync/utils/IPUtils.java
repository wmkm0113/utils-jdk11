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

import org.nervousync.beans.ip.IPRange;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.enumerations.ip.IPType;

import java.math.BigInteger;

/**
 * IP Utils
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Aug 15, 2022 16:23:13 AM $
 */
public final class IPUtils {

	private static final String SPLIT_CHARACTER_IPV4 = ".";
	private static final String SPLIT_CHARACTER_IPV6 = ":";
	private static final String SPLIT_COMBO_CHARACTER_IPV6 = "::";

	/**
	 * Calculate IP range address
	 *
	 * @param ipAddress IP address in range
	 * @param cidr      CIDR
	 * @return IPRange object
	 */
	public static IPRange calcRange(final String ipAddress, final int cidr) {
		IPRange ipRange = new IPRange();
		String beginAddress;
		String endAddress;
		if (ipAddress.contains(SPLIT_CHARACTER_IPV6)) {
			ipRange.setIpType(IPType.IPv6);
			beginAddress = beginIPv6(ipAddress, cidr);
			endAddress = endIPv6(beginAddress, cidr);
		} else {
			ipRange.setIpType(IPType.IPv4);
			beginAddress = beginIPv4(ipAddress, CIDRToNetmask(cidr));
			endAddress = endIPv4(beginAddress, CIDRToNetmask(cidr));
		}

		ipRange.setBeginAddress(beginAddress);
		ipRange.setEndAddress(endAddress);

		return ipRange;
	}

	/**
	 * Convert net mask to CIDR
	 *
	 * @param netmask Net mask address
	 * @return CIDR int
	 */
	public static int NetmaskToCIDR(final String netmask) {
		int result = 0;

		String[] splitItems = StringUtils.tokenizeToStringArray(netmask, SPLIT_CHARACTER_IPV4);

		for (String splitItem : splitItems) {
			int number = Integer.parseInt(splitItem);
			while (number > 0) {
				if ((number % 2) == 1) {
					result++;
				}
				number /= 2;
			}
		}

		return result;
	}

	/**
	 * Convert CIDR to net mask address
	 *
	 * @param cidr CIDR
	 * @return Net mask address
	 */
	public static String CIDRToNetmask(final int cidr) {
		int calcCIDR = cidr;
		if (calcCIDR >= 0 && calcCIDR <= 32) {
			String[] arrays = new String[4];
			int index = 0;
			while (index < 4 && calcCIDR >= 0) {
				arrays[index] = Integer.toString(fillBitsFromLeft(calcCIDR));
				calcCIDR -= 8;
				index++;
			}
			return String.join(SPLIT_CHARACTER_IPV4, arrays);
		}
		return null;
	}


	/**
	 * Is i pv 4 address boolean.
	 *
	 * @param ipAddress the ip address
	 * @return the boolean
	 */
	public static boolean isIPv4Address(final String ipAddress) {
		return StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX);
	}

	/**
	 * Is i pv 6 address boolean.
	 *
	 * @param ipAddress the ip address
	 * @return the boolean
	 */
	public static boolean isIPv6Address(final String ipAddress) {
		return StringUtils.matches(ipAddress, RegexGlobals.IPV6_REGEX)
				|| StringUtils.matches(ipAddress, RegexGlobals.IPV6_COMPRESS_REGEX);
	}
	/**
	 * Convert IPv4 address to compatible IPv6 address
	 *
	 * @param ipAddress IPv4 address
	 * @return Compatible IPv6 address
	 */
	public static String IPv4ToCompatibleIPv6(final String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return SPLIT_COMBO_CHARACTER_IPV6 + ipAddress;
		}
		return null;
	}

	/**
	 * Convert IPv4 address to IPv6 address and collapse
	 *
	 * @param ipAddress IPv4 address
	 * @return Collapse IPv6 address
	 */
	public static String IPv4ToIPv6(final String ipAddress) {
		return IPv4ToIPv6(ipAddress, Boolean.TRUE);
	}

	/**
	 * Convert IPv4 address to IPv6 address
	 *
	 * @param ipAddress IPv4 address
	 * @param collapse  Collapse IPv6 address
	 * @return IPv6 address
	 */
	public static String IPv4ToIPv6(final String ipAddress, final boolean collapse) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, SPLIT_CHARACTER_IPV4);
			StringBuilder stringBuilder;
			if (collapse) {
				stringBuilder = new StringBuilder(SPLIT_CHARACTER_IPV6);
			} else {
				stringBuilder = new StringBuilder("0000:0000:0000:0000:0000:0000");
			}
			int index = 0;
			for (String addressItem : splitAddress) {
				if (index % 2 == 0) {
					stringBuilder.append(SPLIT_CHARACTER_IPV6);
				}
				stringBuilder.append(Integer.toHexString(Integer.parseInt(addressItem)));
				index++;
			}

			return stringBuilder.toString().toUpperCase();
		}
		return null;
	}

	/**
	 * Convert IP Address to byte arrays
	 *
	 * @param ipAddress IP Address
	 * @return byte array
	 */
	public static byte[] IPToBytes(final String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return IPv4ToBytes(ipAddress);
		} else if (StringUtils.matches(ipAddress, RegexGlobals.IPV6_REGEX)
				|| StringUtils.matches(ipAddress, RegexGlobals.IPV6_COMPRESS_REGEX)) {
			return IPv6ToBytes(ipAddress);
		} else {
			return new byte[0];
		}
	}

	/**
	 * Convert IPv4 address to byte arrays
	 *
	 * @param ipAddress IPv4 address
	 * @return byte array length 4
	 */
	public static byte[] IPv4ToBytes(final String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, SPLIT_CHARACTER_IPV4);
			byte[] addressBytes = new byte[4];

			addressBytes[0] = (byte) Integer.parseInt(splitAddress[0]);
			addressBytes[1] = (byte) Integer.parseInt(splitAddress[1]);
			addressBytes[2] = (byte) Integer.parseInt(splitAddress[2]);
			addressBytes[3] = (byte) Integer.parseInt(splitAddress[3]);

			return addressBytes;
		}
		return null;
	}

	/**
	 * Convert IPv6 address to byte arrays
	 *
	 * @param ipAddress IPv6 address
	 * @return byte array length 16
	 */
	public static byte[] IPv6ToBytes(final String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV6_REGEX)
				|| StringUtils.matches(ipAddress, RegexGlobals.IPV6_COMPRESS_REGEX)) {
			String ipv6Address = expandIPv6(ipAddress);
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipv6Address, SPLIT_CHARACTER_IPV6);
			byte[] addressBytes = new byte[16];
			int index = 0;
			for (String address : splitAddress) {
				int tmp = Integer.parseInt(address, 16);
				addressBytes[index] = (byte) (tmp >> 8);
				addressBytes[index + 1] = (byte) tmp;
				index += 2;
			}
			return addressBytes;
		}
		return null;
	}

	/**
	 * Expand i pv 6 string.
	 *
	 * @param ipAddress the ip address
	 * @return the string
	 */
	public static String expandIPv6(final String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV6_COMPRESS_REGEX)) {
			int sigCount = StringUtils.countOccurrencesOf(ipAddress, SPLIT_CHARACTER_IPV6);
			int expandCount = 8 - sigCount;
			int position = 0;
			StringBuilder stringBuilder = new StringBuilder();
			while (true) {
				int index = ipAddress.indexOf(SPLIT_CHARACTER_IPV6, position);
				if (index == Globals.DEFAULT_VALUE_INT) {
					stringBuilder.append(SPLIT_CHARACTER_IPV6).append(ipAddress.substring(position));
					break;
				} else {
					if (index == position) {
						while (expandCount > 0) {
							stringBuilder.append(":0");
							expandCount--;
						}
					} else {
						stringBuilder.append(SPLIT_CHARACTER_IPV6).append(ipAddress, position, index);
					}
					position = index + 1;
				}
			}
			return stringBuilder.substring(1);
		}
		return ipAddress;
	}

	/**
	 * Convert IP address to BigInteger(supported IPv4 and IPv6)
	 *
	 * @param ipAddress IP address
	 * @return BigInteger big integer
	 */
	public static BigInteger IPtoBigInteger(final String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return IPv4ToBigInteger(ipAddress);
		} else {
			return IPv6ToBigInteger(ipAddress);
		}
	}

	/**
	 * Convert IPv4 address to BigInteger
	 *
	 * @param ipAddress IPv4 address
	 * @return BigInteger big integer
	 */
	public static BigInteger IPv4ToBigInteger(final String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, SPLIT_CHARACTER_IPV4);
			if (splitAddress.length == 4) {
				long result = 0L;
				for (int i = 0; i < splitAddress.length; i++) {
					result += (Long.parseLong(splitAddress[i]) << 8 * (3 - i));
				}
				return BigInteger.valueOf(result);
			}
		}
		return BigInteger.ZERO;
	}

	/**
	 * Convert IPv6 address to BigInteger
	 *
	 * @param ipAddress IPv6 address
	 * @return BigInteger big integer
	 */
	public static BigInteger IPv6ToBigInteger(final String ipAddress) {
		String fullAddress = expandIgnore(ipAddress);
		if (StringUtils.matches(fullAddress, RegexGlobals.IPV6_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(fullAddress, SPLIT_CHARACTER_IPV6);
			BigInteger bigInteger = BigInteger.ZERO;
			int index = 0;
			for (String split : splitAddress) {
				BigInteger currentInteger;
				if (StringUtils.matches(split, RegexGlobals.IPV4_REGEX)) {
					currentInteger = IPv4ToBigInteger(split);
				} else {
					currentInteger = BigInteger.valueOf(Long.valueOf(split, 16));
				}
				if (currentInteger == null) {
					return BigInteger.ZERO;
				}
				bigInteger = bigInteger.add(currentInteger.shiftLeft(16 * (splitAddress.length - index - 1)));
				index++;
			}
			return bigInteger;
		}
		return BigInteger.ZERO;
	}

	/**
	 * Convert BigInteger value to IPv4 address(x.x.x.x)
	 *
	 * @param bigInteger BigInteger value
	 * @return IPv4 address
	 */
	public static String BigIntegerToIPv4(final BigInteger bigInteger) {
		StringBuilder ipv4Address = new StringBuilder();
		BigInteger calcInteger = new BigInteger(bigInteger.toByteArray());
		BigInteger ff = BigInteger.valueOf(0xFFL);

		for (int i = 0; i < 4; i++) {
			ipv4Address.insert(0, SPLIT_CHARACTER_IPV4 + calcInteger.and(ff));
			calcInteger = calcInteger.shiftRight(8);
		}

		return ipv4Address.substring(1);
	}

	/**
	 * Convert BigInteger value to IPv6 address(xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx or :xxxx:xxxx::xxxx)
	 *
	 * @param bigInteger BigInteger value
	 * @return IPv6 address
	 */
	public static String BigIntegerToIPv6Address(final BigInteger bigInteger) {
		StringBuilder ipv6Address = new StringBuilder();
		BigInteger calcInteger = new BigInteger(bigInteger.toByteArray());
		BigInteger ff = BigInteger.valueOf(0xFFFFL);

		for (int i = 0; i < 8; i++) {
			ipv6Address.insert(0, SPLIT_CHARACTER_IPV6 + calcInteger.and(ff).toString(16));
			calcInteger = calcInteger.shiftRight(16);
		}

		return ipv6Address.substring(1).replaceFirst(RegexGlobals.IPV6_COMPRESS_REGEX, SPLIT_COMBO_CHARACTER_IPV6);
	}

	/**
	 * Expand IPv6 address ignore data
	 *
	 * @param ipv6Address IPv6 address
	 * @return Expand IPv6 address
	 */
	public static String expandIgnore(final String ipv6Address) {
		String resultAddress = ipv6Address;
		if (resultAddress.contains("::")) {
			int count = StringUtils.countOccurrencesOf(resultAddress, SPLIT_CHARACTER_IPV6);
			resultAddress = StringUtils.replace(resultAddress, SPLIT_COMBO_CHARACTER_IPV6,
					":0000".repeat(Math.max(0, 8 - count)) + SPLIT_CHARACTER_IPV6);
			if (resultAddress.startsWith(SPLIT_CHARACTER_IPV6)) {
				resultAddress = "0000" + resultAddress;
			}
			if (resultAddress.endsWith(SPLIT_CHARACTER_IPV6)) {
				resultAddress += "0000";
			}
		}

		String[] addressItems = StringUtils.delimitedListToStringArray(resultAddress, SPLIT_CHARACTER_IPV6);
		StringBuilder stringBuilder = new StringBuilder();
		for (String addressItem : addressItems) {
			StringBuilder addressItemBuilder = new StringBuilder(addressItem);
			while (addressItemBuilder.length() < 4) {
				addressItemBuilder.insert(0, "0");
			}
			addressItem = addressItemBuilder.toString();
			stringBuilder.append(SPLIT_CHARACTER_IPV6).append(addressItem);
		}
		return stringBuilder.substring(1);
	}

	/**
	 * Calculate IP range begins address
	 *
	 * @param ipAddress IP address in range
	 * @param netmask   Net mask address
	 * @return Begin IP address
	 */
	private static String beginIPv4(final String ipAddress, final String netmask) {
		String[] addressItems = StringUtils.tokenizeToStringArray(ipAddress, SPLIT_CHARACTER_IPV4);
		String[] maskItems = StringUtils.tokenizeToStringArray(netmask, SPLIT_CHARACTER_IPV4);

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			int itemValue = i < addressItems.length ? Integer.parseInt(addressItems[i])
					: Globals.INITIALIZE_INT_VALUE;
			int beginItem = itemValue & Integer.parseInt(maskItems[i]);
			if (itemValue == 0 && i == 3) {
				beginItem++;
			}
			stringBuilder.append(SPLIT_CHARACTER_IPV4).append(beginItem);
		}

		return stringBuilder.substring(1);
	}

	/**
	 * Calculate IP range end address
	 *
	 * @param beginIP Begin address of range
	 * @param netmask Net mask address
	 * @return End IP address
	 */
	private static String endIPv4(final String beginIP, final String netmask) {
		String[] addressItems = StringUtils.tokenizeToStringArray(beginIP, SPLIT_CHARACTER_IPV4);
		String[] maskItems = StringUtils.tokenizeToStringArray(netmask, SPLIT_CHARACTER_IPV4);

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			int endItem = 255 - Integer.parseInt(addressItems[i]) ^ Integer.parseInt(maskItems[i]);
			stringBuilder.append(SPLIT_CHARACTER_IPV4).append(endItem);
		}

		return stringBuilder.substring(1);
	}

	/**
	 * Calculate IPv6 range begins address
	 *
	 * @param ipAddress IPv6 address
	 * @param cidr      CIDR
	 * @return Begin address
	 */
	private static String beginIPv6(final String ipAddress, final int cidr) {
		if (cidr >= 0 && cidr <= 128) {
			String hexAddress = StringUtils.replace(expandIgnore(ipAddress), SPLIT_CHARACTER_IPV6, Globals.DEFAULT_VALUE_STRING);
			StringBuilder baseIP = new StringBuilder(hexToBin(hexAddress).substring(0, cidr));

			while (baseIP.length() < 128) {
				baseIP.append("0");
			}

			return binToHex(baseIP.toString());
		}
		return null;
	}

	/**
	 * Calculate IPv6 range end address
	 *
	 * @param ipAddress IPv6 address
	 * @param cidr      CIDR
	 * @return End address
	 */
	private static String endIPv6(final String ipAddress, final int cidr) {
		if (cidr >= 0 && cidr <= 128) {
			String hexAddress = StringUtils.replace(expandIgnore(ipAddress), SPLIT_CHARACTER_IPV6, Globals.DEFAULT_VALUE_STRING);
			StringBuilder baseIP = new StringBuilder(hexToBin(hexAddress).substring(0, cidr));

			while (baseIP.length() < 128) {
				baseIP.append("1");
			}

			return binToHex(baseIP.toString());
		}
		return null;
	}

	/**
	 * Convert IPv6 address from hex data to binary data
	 *
	 * @param hexAddress hex data address
	 * @return binary data address
	 */
	private static String hexToBin(final String hexAddress) {
		StringBuilder binBuilder = new StringBuilder();
		int index = 0;
		while (index < hexAddress.length()) {
			int hexInt = Integer.parseInt(hexAddress.substring(index, index + 1), 16);
			StringBuilder binItem = new StringBuilder(Integer.toString(hexInt, 2));
			while (binItem.length() < 4) {
				binItem.insert(0, "0");
			}
			binBuilder.append(binItem);
			index++;
		}
		return binBuilder.toString();
	}

	/**
	 * Convert IPv6 address from binary data to hex data
	 *
	 * @param binAddress binary data address
	 * @return hex data address
	 */
	private static String binToHex(final String binAddress) {
		StringBuilder binBuilder = new StringBuilder();
		int index = 0;
		while (index < binAddress.length()) {
			if (index % 16 == 0) {
				binBuilder.append(SPLIT_CHARACTER_IPV6);
			}
			int binInt = Integer.parseInt(binAddress.substring(index, index + 4), 2);
			binBuilder.append(Integer.toString(binInt, 16).toUpperCase());
			index += 4;
		}
		return binBuilder.substring(1);
	}

	private static int fillBitsFromLeft(final int value) {
		if (value >= 8) {
			return 255;
		} else {
			return 256 - Double.valueOf(Math.pow(2, (8 - value))).intValue();
		}
	}
}
