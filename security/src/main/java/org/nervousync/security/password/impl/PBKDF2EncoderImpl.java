/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
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

package org.nervousync.security.password.impl;

import org.nervousync.commons.Globals;
import org.nervousync.security.password.PasswordEncoder;
import org.nervousync.utils.commons.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * <h2 class="en-US">PBKDF2 password encoder implement class</h2>
 * <h2 class="zh-CN">PBKDF2密码编码器实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 15, 2026 11:23:13 $
 */
public final class PBKDF2EncoderImpl implements PasswordEncoder {

	private static final int SALT_LENGTH = 16;  //  128 bit
	private static final int ITERATION_MIN_LIMIT_SHA1 = 1300000;
	private static final int ITERATION_MAX_LIMIT_SHA1 = 3000000;
	private static final int ITERATION_MIN_LIMIT_SHA256 = 600000;
	private static final int ITERATION_MAX_LIMIT_SHA256 = 2000000;
	private static final int ITERATION_MIN_LIMIT_SHA512 = 210000;
	private static final int ITERATION_MAX_LIMIT_SHA512 = 1000000;

	/**
	 * <span class="en-US">Algorithm name</span>
	 * <span class="zh-CN">算法名称</span>
	 */
	private final Algorithm algorithm;
	/**
	 * <span class="en-US">Iteration count</span>
	 * <span class="zh-CN">迭代次数</span>
	 */
	private final int iterations;
	/**
	 * <span class="en-US">Derived key length</span>
	 * <span class="zh-CN">派生密钥长度</span>
	 */
	private final int keyLength;

	/**
	 * <h3 class="en-US">Constructor method for the PBKDF2 password encoder implement class</h3>
	 * <h3 class="zh-CN">PBKDF2密码编码器实现类的构造方法</h3>
	 *
	 * @param algorithm  <span class="en-US">Algorithm name</span>
	 *                   <span class="zh-CN">算法名称</span>
	 * @param iterations <span class="en-US">Iteration count</span>
	 *                   <span class="zh-CN">迭代次数</span>
	 */
	public PBKDF2EncoderImpl(final Algorithm algorithm, final int iterations) {
		if (iterations <= 0) {
			throw new IllegalArgumentException("Iterations must be positive");
		}
		Config config = new Config(algorithm, iterations, keyLength(algorithm), new byte[0], new byte[0]);
		this.algorithm = config.algorithm;
		this.iterations = config.iterations;
		this.keyLength = config.keyLength;
	}

	@Override
	public String encode(final char[] password) {
		byte[] salt = new byte[SALT_LENGTH];
		Globals.randomBytes(salt);
		Config config = new Config(this.algorithm, this.iterations, this.keyLength, salt, new byte[0]);
		byte[] hash = this.derive(password, config);
		return config + "$" + StringUtils.base64Encode(hash, Boolean.FALSE);
	}

	@Override
	public boolean verify(final char[] password, final String encodedPassword) {
		Config config = Config.parse(encodedPassword);
		if (VALID_ITERATIONS(config)) {
			byte[] hash = this.derive(password, config);
			try {
				return MessageDigest.isEqual(config.hash, hash);
			} finally {
				Arrays.fill(hash, (byte) 0);
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean needsUpgrade(final String encodedPassword) {
		Config config = Config.parse(encodedPassword);
		return !this.algorithm.equals(config.algorithm) || config.iterations != this.iterations;
	}

	private byte[] derive(final char[] password, final Config config) {
		PBEKeySpec spec = new PBEKeySpec(password, config.salt, config.iterations, config.keyLength * 8);
		try {
			return SecretKeyFactory.getInstance(config.algorithmName()).generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalArgumentException("PBKDF2 is not available", e);
		} finally {
			spec.clearPassword();
		}
	}

	private static int keyLength(final Algorithm algorithm) {
		switch (algorithm) {
			case SHA1:
				return 16;
			case SHA256:
				return 32;
			case SHA512:
				return 64;
			default:
				throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
		}
	}

	/**
	 * <h3 class="en-US">Check whether the number of iterations for the given algorithm falls within the valid range</h3>
	 * <h3 class="zh-CN">检查给定算法的迭代次数是否在合法区间内</h3>
	 *
	 * @param config <span class="en-US">Configure information</span>
	 *               <span class="zh-CN">配置信息</span>
	 * @return <span class="en-US">Check result</span>
	 * <span class="zh-CN">检查结果</span>
	 */
	private static boolean VALID_ITERATIONS(final Config config) {
		if (config.iterations <= Globals.INITIALIZE_INT_VALUE || config.salt.length != SALT_LENGTH
				|| config.keyLength != keyLength(config.algorithm) || config.hash.length != config.keyLength) {
			return Boolean.FALSE;
		}
		switch (config.algorithm) {
			case SHA1:
				return config.iterations >= ITERATION_MIN_LIMIT_SHA1
						&& config.iterations <= ITERATION_MAX_LIMIT_SHA1;
			case SHA256:
				return config.iterations >= ITERATION_MIN_LIMIT_SHA256
						&& config.iterations <= ITERATION_MAX_LIMIT_SHA256;
			case SHA512:
				return config.iterations >= ITERATION_MIN_LIMIT_SHA512
						&& config.iterations <= ITERATION_MAX_LIMIT_SHA512;
			default:
				throw new IllegalArgumentException("Unsupported algorithm: " + config.algorithm);
		}
	}

	public enum Algorithm {
		SHA1, SHA256, SHA512
	}

	private static final class Config {

		private final Algorithm algorithm;
		private final int iterations;
		private final int keyLength;
		private final byte[] salt;
		private final byte[] hash;

		Config(final Algorithm algorithm, final int iterations, final int keyLength, final byte[] salt, final byte[] hash) {
			this.algorithm = algorithm;
			switch (this.algorithm) {
				case SHA1:
					this.iterations = Math.min(Math.max(iterations, ITERATION_MIN_LIMIT_SHA1), ITERATION_MAX_LIMIT_SHA1);
					break;
				case SHA256:
					this.iterations = Math.min(Math.max(iterations, ITERATION_MIN_LIMIT_SHA256), ITERATION_MAX_LIMIT_SHA256);
					break;
				case SHA512:
					this.iterations = Math.min(Math.max(iterations, ITERATION_MIN_LIMIT_SHA512), ITERATION_MAX_LIMIT_SHA512);
					break;
				default:
					throw new IllegalArgumentException("Algorithm not supported: " + this.algorithm);
			}
			this.keyLength = keyLength;
			this.salt = salt;
			this.hash = hash;
		}

		@Override
		public String toString() {
			String identify;
			switch (this.algorithm) {
				case SHA1:
					identify = "sha1";
					break;
				case SHA256:
					identify = "sha256";
					break;
				case SHA512:
					identify = "sha512";
					break;
				default:
					throw new IllegalArgumentException("Algorithm not supported: " + this.algorithm);
			}
			return "$pbkdf2-" + identify + "$i=" + this.iterations
					+ "$" + StringUtils.base64Encode(this.salt, Boolean.FALSE);
		}

		public String algorithmName() {
			switch (this.algorithm) {
				case SHA1:
					return "PBKDF2WithHmacSHA1";
				case SHA256:
					return "PBKDF2WithHmacSHA256";
				case SHA512:
					return "PBKDF2WithHmacSHA512";
				default:
					throw new IllegalArgumentException("Algorithm not supported: " + this.algorithm);
			}
		}

		static Config parse(final String encodedPassword) {
			String[] parts = StringUtils.tokenizeToStringArray(encodedPassword, "$");
			if (parts.length != 4 || !StringUtils.startsWithIgnoreCase(parts[0], "pbkdf2-")
					|| !StringUtils.startsWithIgnoreCase(parts[1], "i=")) {
				throw new IllegalArgumentException("Invalid PBKDF2 hash! " + encodedPassword);
			}
			String string = parts[0].substring("pbkdf2-".length()).toUpperCase();
			Algorithm algorithm;
			int iterations = Integer.parseInt(parts[1].substring(2));
			switch (string) {
				case "SHA1":
					algorithm = Algorithm.SHA1;
					iterations = Math.min(Math.max(iterations, ITERATION_MIN_LIMIT_SHA1), ITERATION_MAX_LIMIT_SHA1);
					break;
				case "SHA256":
					algorithm = Algorithm.SHA256;
					iterations = Math.min(Math.max(iterations, ITERATION_MIN_LIMIT_SHA256), ITERATION_MAX_LIMIT_SHA256);
					break;
				case "SHA512":
					algorithm = Algorithm.SHA512;
					iterations = Math.min(Math.max(iterations, ITERATION_MIN_LIMIT_SHA512), ITERATION_MAX_LIMIT_SHA512);
					break;
				default:
					throw new IllegalArgumentException("Algorithm not supported: " + string);
			}
			return new Config(algorithm, iterations, PBKDF2EncoderImpl.keyLength(algorithm),
					StringUtils.base64Decode(parts[2]), StringUtils.base64Decode(parts[3]));
		}
	}
}
