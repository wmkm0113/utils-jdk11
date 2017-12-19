/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.huffman;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 3, 2017 4:26:45 PM $
 */
public final class HuffmanNode {

	private String keyword;
	private int frequency;
	private HuffmanNode leftNode;
	private HuffmanNode rightNode;
	private HuffmanNode nextNode;
	
	public HuffmanNode(int frequency) {
		this.frequency = frequency;
	}
	
	public HuffmanNode(String keyword, int frequency) {
		this.keyword = keyword;
		this.frequency = frequency;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the leftNode
	 */
	public HuffmanNode getLeftNode() {
		return leftNode;
	}

	/**
	 * @param leftNode the leftNode to set
	 */
	public void setLeftNode(HuffmanNode leftNode) {
		this.leftNode = leftNode;
	}

	/**
	 * @return the rightNode
	 */
	public HuffmanNode getRightNode() {
		return rightNode;
	}

	/**
	 * @param rightNode the rightNode to set
	 */
	public void setRightNode(HuffmanNode rightNode) {
		this.rightNode = rightNode;
	}

	/**
	 * @return the nextNode
	 */
	public HuffmanNode getNextNode() {
		return nextNode;
	}

	/**
	 * @param nextNode the nextNode to set
	 */
	public void setNextNode(HuffmanNode nextNode) {
		this.nextNode = nextNode;
	}
}
