/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.huffman;

import java.util.Hashtable;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 3, 2017 4:39:41 PM $
 */
public final class HuffmanTree {

	private int nodeCount = 0;
	private HuffmanNode rootNode = null;
	private Hashtable<String, Object> codeMapping = new Hashtable<String, Object>();
	
	public HuffmanTree() {
		
	}

	private HuffmanTree(Hashtable<String, Object> codeMapping) {
		this.codeMapping = codeMapping;
	}

	public void insertNode(HuffmanNode huffmanNode) {
		if (this.rootNode == null) {
			this.rootNode = huffmanNode;
		} else {
			HuffmanNode currentNode = this.rootNode;
			HuffmanNode previousNode = null;
			
			while (currentNode.getFrequency() < huffmanNode.getFrequency()) {
				previousNode = currentNode;
				if (currentNode.getNextNode() == null) {
					currentNode = null;
					break;
				} else {
					currentNode = currentNode.getNextNode();
				}
			}
			
			if (previousNode == null) {
				huffmanNode.setNextNode(this.rootNode);
				this.rootNode = huffmanNode;
			} else if (currentNode == null) {
				previousNode.setNextNode(huffmanNode);
			} else {
				previousNode.setNextNode(huffmanNode);
				huffmanNode.setNextNode(currentNode);
			}
		}
		this.nodeCount++;
	}
	
	public void build() {
		while (this.nodeCount > 1) {
			this.mergeNode();
		}
		
		this.buildCodeMapping(this.rootNode, "");
	}
	
	public static String encodeString(Hashtable<String, Object> codeMapping, String content) {
		HuffmanTree huffmanTree = new HuffmanTree(codeMapping);
		return huffmanTree.encodeString(content).getHuffmanValue();
	}
	
	public HuffmanObject encodeString(String content) {
		if (content == null || content.length() == 0) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		
		while (content.length() > 0) {
			String keyword = String.valueOf(content.charAt(0));
			stringBuilder.append(this.codeMapping.get(keyword));
			content = content.substring(1);
		}
		
		return new HuffmanObject(this.codeMapping, stringBuilder.toString());
	}
	
	private void buildCodeMapping(HuffmanNode currentNode, String currentCode) {
		if (currentNode.getKeyword() != null) {
			this.codeMapping.put(currentNode.getKeyword(), currentCode);
		} else {
			this.buildCodeMapping(currentNode.getLeftNode(), currentCode + "0");
			this.buildCodeMapping(currentNode.getRightNode(), currentCode + "1");
		}
	}
	
	private void mergeNode() {
		HuffmanNode leftNode = this.pollNode();
		HuffmanNode rightNode = this.pollNode();
		
		HuffmanNode mergeNode = new HuffmanNode(leftNode.getFrequency() + rightNode.getFrequency());
		mergeNode.setLeftNode(leftNode);
		mergeNode.setRightNode(rightNode);
		this.insertNode(mergeNode);
	}

	/**
	 * Retrieves and removes the head of this queue, or returns null if this queue is empty.
	 * @return
	 */
	private HuffmanNode pollNode() {
		if (this.rootNode == null) {
			return null;
		}
		
		HuffmanNode removeNode = this.rootNode;
		this.rootNode = this.rootNode.getNextNode();
		this.nodeCount--;
		return removeNode;
	}
}
