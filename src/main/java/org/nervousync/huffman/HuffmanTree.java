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

import java.util.Hashtable;

/**
 * The type Huffman tree.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 3, 2017 4:39:41 PM $
 */
public final class HuffmanTree {

	private int nodeCount = 0;
	private HuffmanNode rootNode = null;
	private Hashtable<String, Object> codeMapping = new Hashtable<>();

	/**
	 * Instantiates a new Huffman tree.
	 */
	public HuffmanTree() {
	}

	private HuffmanTree(Hashtable<String, Object> codeMapping) {
		this.codeMapping = codeMapping;
	}

	/**
	 * Insert node.
	 *
	 * @param huffmanNode the huffman node
	 */
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

	/**
	 * Build.
	 */
	public void build() {
		while (this.nodeCount > 1) {
			this.mergeNode();
		}
		
		this.buildCodeMapping(this.rootNode, "");
	}

	/**
	 * Encode string string.
	 *
	 * @param codeMapping the code mapping
	 * @param content     the content
	 * @return the string
	 */
	public static String encodeString(Hashtable<String, Object> codeMapping, String content) {
		HuffmanTree huffmanTree = new HuffmanTree(codeMapping);
		return huffmanTree.encodeString(content).getHuffmanValue();
	}

	/**
	 * Encode string huffman object.
	 *
	 * @param content the content
	 * @return the huffman object
	 */
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

		if (leftNode != null && rightNode != null) {
			HuffmanNode mergeNode = new HuffmanNode(leftNode.getFrequency() + rightNode.getFrequency());
			mergeNode.setLeftNode(leftNode);
			mergeNode.setRightNode(rightNode);
			this.insertNode(mergeNode);
		}
	}

	/**
	 * Retrieves and removes the head of this queue, or returns null if this queue is empty.
	 * @return  poll node
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
