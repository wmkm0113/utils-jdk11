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

package org.nervousync.test.commons;

import org.junit.jupiter.api.Test;
import org.nervousync.tree.huffman.HuffmanTree;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/** 整数规范码扩展专项；保留并回归原字符串 API。 */
final class HuffmanTreeTest {

	@Test
	void canonicalCodesFollowLengthThenSymbolOrder() {
		// 码长分布满足 Kraft 等式，期望编码直接由规范码定义给出。
		int[] lengths = {3, 3, 3, 3, 3, 2, 4, 4};
		int[] expected = {0b010, 0b011, 0b100, 0b101, 0b110, 0b00, 0b1110, 0b1111};
		HuffmanTree.Book book = HuffmanTree.canonicalCodes(lengths);
		assertEquals(2, book.getMinLength());
		assertEquals(4, book.getMaxLength());
		for (int symbol = 0; symbol < lengths.length; symbol++) {
			assertEquals(expected[symbol], book.getCode(symbol));
			assertEquals(lengths[symbol], book.getLength(symbol));
			assertEquals(symbol, book.getSymbol(expected[symbol], lengths[symbol]));
		}
	}

	@Test
	void sparseIncompleteAndSingletonBooksHaveExplicitMissingSymbols() {
		int[] lengths = {0, 2, 0, 3};
		HuffmanTree.Book book = HuffmanTree.canonicalCodes(lengths);
		lengths[1] = 30; // 调用方后续修改数组不得改变已建立的码表。
		assertEquals(2, book.getLength(1));
		assertEquals(0, book.getCode(1));
		assertEquals(2, book.getCode(3));
		assertEquals(-1, book.getCode(0));
		assertEquals(0, book.getLength(0));
		assertEquals(-1, book.getSymbol(3, 3));
		for (int length : new int[] {Integer.MIN_VALUE, -1, 0, 1, 4, 31, Integer.MAX_VALUE}) {
			assertEquals(-1, book.getSymbol(0, length));
		}
		assertEquals(-1, book.getSymbol(-1, 2));
		assertEquals(-1, book.getSymbol(4, 2)); // 有效位之外存在高位。
		HuffmanTree.Book singleton = HuffmanTree.canonicalCodes(new int[] {0, 1, 0});
		assertEquals(1, singleton.getSymbol(0, 1));
		assertEquals(-1, singleton.getSymbol(1, 1));
	}

	@Test
	void supportsThirtyBitCodesWithoutSignedOverflow() {
		int[] lengths = new int[31];
		for (int i = 0; i < 29; i++) lengths[i] = i + 1;
		lengths[29] = lengths[30] = 30;
		HuffmanTree.Book book = HuffmanTree.canonicalCodes(lengths);
		assertEquals(0x3FFFFFFE, book.getCode(29));
		assertEquals(0x3FFFFFFF, book.getCode(30));
		assertEquals(30, book.getSymbol(0x3FFFFFFF, 30));
		assertEquals(-1, book.getSymbol(0x40000000, 30));
	}

	@Test
	void rejectsInvalidAndOversubscribedCodeBooks() {
		assertThrows(NullPointerException.class, () -> HuffmanTree.canonicalCodes(null));
		for (int[] lengths : new int[][] {{}, {0, 0}, {-1}, {31}, {1, 1, 1}, {1, 2, 2, 3}}) {
			assertThrows(IllegalArgumentException.class, () -> HuffmanTree.canonicalCodes(lengths), Arrays.toString(lengths));
		}
	}

	@Test
	void frequencyBoundariesKeepAbsentSymbolsAndInputUnchanged() {
		assertArrayEquals(new int[0], HuffmanTree.codeLengths(new int[0], 1));
		assertArrayEquals(new int[3], HuffmanTree.codeLengths(new int[3], 1));
		assertArrayEquals(new int[] {0, 1, 0}, HuffmanTree.codeLengths(new int[] {0, Integer.MAX_VALUE, 0}, 1));
		int[] weights = {5, 9, 12, 13, 16, 45};
		int[] before = weights.clone();
		int[] lengths = HuffmanTree.codeLengths(weights, 20);
		assertArrayEquals(new int[] {4, 4, 3, 3, 3, 1}, lengths);
		assertArrayEquals(before, weights);
		lengths[0] = 0;
		assertArrayEquals(new int[] {4, 4, 3, 3, 3, 1}, HuffmanTree.codeLengths(weights, 20));
	}

	@Test
	void rejectsInvalidFrequenciesAndInsufficientCapacity() {
		assertThrows(NullPointerException.class, () -> HuffmanTree.codeLengths(null, 20));
		assertThrows(IllegalArgumentException.class, () -> HuffmanTree.codeLengths(new int[] {1, -1}, 20));
		for (int limit : new int[] {Integer.MIN_VALUE, -1, 0, 31, Integer.MAX_VALUE}) {
			assertThrows(IllegalArgumentException.class, () -> HuffmanTree.codeLengths(new int[] {1}, limit));
		}
		assertThrows(IllegalArgumentException.class, () -> HuffmanTree.codeLengths(new int[] {1, 1, 1}, 1));
		assertThrows(IllegalArgumentException.class, () -> HuffmanTree.codeLengths(new int[] {1, 1, 1, 1, 1}, 2));
		assertArrayEquals(new int[] {2, 2, 2, 2}, HuffmanTree.codeLengths(new int[] {1, 1, 1, 1}, 2));
	}

	@Test
	void limitsHighlySkewedAndOverflowingWeights() {
		int[] skewed = new int[258];
		Arrays.fill(skewed, 1);
		skewed[0] = Integer.MAX_VALUE;
		verifyBook(skewed, 9);
		int[] overflowing = new int[258];
		Arrays.fill(overflowing, Integer.MAX_VALUE);
		verifyBook(overflowing, 9); // 频率总和超过 int，不能使树构造溢出。
		int[] fibonacci = new int[28];
		fibonacci[0] = fibonacci[1] = 1;
		for (int i = 2; i < fibonacci.length; i++) fibonacci[i] = fibonacci[i - 1] + fibonacci[i - 2];
		verifyBook(fibonacci, 5);
	}

	@Test
	void generatedBooksAreDeterministicCanonicalAndPrefixFree() {
		Random random = new Random(0xB21F);
		for (int attempt = 0; attempt < 160; attempt++) {
			int[] weights = new int[2 + random.nextInt(257)];
			for (int i = 0; i < weights.length; i++) weights[i] = random.nextInt(5) == 0 ? 0 : 1 + random.nextInt(Integer.MAX_VALUE - 1);
			weights[0] = 1;
			verifyBook(weights, 9 + random.nextInt(12));
		}
	}

	/** 用逐对前缀比较和按长度排序的数值递增规则独立验证，不依赖生产解码器。 */
	private static void verifyBook(int[] weights, int limit) {
		int[] before = weights.clone();
		int[] lengths = HuffmanTree.codeLengths(weights, limit);
		assertArrayEquals(before, weights);
		assertArrayEquals(lengths, HuffmanTree.codeLengths(weights, limit));
		HuffmanTree.Book book = HuffmanTree.canonicalCodes(lengths);
		Integer[] symbols = IntStream.range(0, weights.length).filter(i -> weights[i] > 0).boxed()
				.sorted(Comparator.<Integer>comparingInt(i -> lengths[i]).thenComparingInt(i -> i)).toArray(Integer[]::new);
		long previous = -1;
		int previousLength = 0;
		for (int symbol : symbols) {
			int length = lengths[symbol];
			assertTrue(length >= 1 && length <= limit);
			long expected = (previous + 1) << (length - previousLength);
			assertEquals(expected, book.getCode(symbol));
			assertEquals(symbol, book.getSymbol((int) expected, length));
			previous = expected;
			previousLength = length;
		}
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] == 0) {
				assertEquals(0, lengths[i]);
				assertEquals(-1, book.getCode(i));
				continue;
			}
			for (int j = 0; j < weights.length; j++) {
				if (i != j && lengths[j] >= lengths[i]) {
					assertNotEquals(book.getCode(i), book.getCode(j) >>> (lengths[j] - lengths[i]), "prefix collision");
				}
			}
		}
	}

	@Test
	void legacyStringEncodingRemainsCompatibleAfterIntegerApiCalls() {
		HuffmanTree tree = new HuffmanTree();
		tree.insertNode(new HuffmanTree.Node("a", 5));
		tree.insertNode(new HuffmanTree.Node("b", 2));
		tree.insertNode(new HuffmanTree.Node("c", 1));
		tree.build();
		String content = "abacaba";
		HuffmanTree.Result result = tree.encodeString(content);
		assertNotNull(result);
		StringBuilder expected = new StringBuilder();
		for (char symbol : content.toCharArray()) expected.append(result.getCodeMapping().get(String.valueOf(symbol)));
		assertTrue(expected.toString().matches("[01]+"));
		assertEquals(expected.toString(), result.getHuffmanValue());
		HuffmanTree.canonicalCodes(HuffmanTree.codeLengths(new int[] {7, 1, 2}, 2));
		assertEquals(expected.toString(), tree.encodeString(content).getHuffmanValue());
		assertEquals(expected.toString(), HuffmanTree.encodeString(result.getCodeMapping(), content));
		assertNull(tree.encodeString(""));
		assertEquals("", HuffmanTree.encodeString(result.getCodeMapping(), ""));
	}
}
