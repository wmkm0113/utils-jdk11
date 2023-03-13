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
package org.nervousync.huffman;

/**
 * Huffman Node
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 3, 2017 4:26:45 PM $
 */
public final class HuffmanNode {

	/**
	 * Key word
	 */
	private String keyword;
	/**
	 * Frequency
	 */
	private int frequency;
	/**
	 * Left node
	 */
	private HuffmanNode leftNode;
	/**
	 * Right node
	 */
	private HuffmanNode rightNode;
	/**
	 * Next node
	 */
	private HuffmanNode nextNode;

	/**
	 * Instantiates a new Huffman node.
	 *
	 * @param frequency the frequency
	 */
	public HuffmanNode(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Instantiates a new Huffman node.
	 *
	 * @param keyword   the keyword
	 * @param frequency the frequency
	 */
	public HuffmanNode(String keyword, int frequency) {
		this.keyword = keyword;
		this.frequency = frequency;
	}

	/**
	 * Gets keyword.
	 *
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * Sets keyword.
	 *
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * Gets frequency.
	 *
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Sets frequency.
	 *
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets left node.
	 *
	 * @return the leftNode
	 */
	public HuffmanNode getLeftNode() {
		return leftNode;
	}

	/**
	 * Sets left node.
	 *
	 * @param leftNode the leftNode to set
	 */
	public void setLeftNode(HuffmanNode leftNode) {
		this.leftNode = leftNode;
	}

	/**
	 * Gets right node.
	 *
	 * @return the rightNode
	 */
	public HuffmanNode getRightNode() {
		return rightNode;
	}

	/**
	 * Sets right node.
	 *
	 * @param rightNode the rightNode to set
	 */
	public void setRightNode(HuffmanNode rightNode) {
		this.rightNode = rightNode;
	}

	/**
	 * Gets next node.
	 *
	 * @return the nextNode
	 */
	public HuffmanNode getNextNode() {
		return nextNode;
	}

	/**
	 * Sets next node.
	 *
	 * @param nextNode the nextNode to set
	 */
	public void setNextNode(HuffmanNode nextNode) {
		this.nextNode = nextNode;
	}
}
