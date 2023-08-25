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
package org.nervousync.beans.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.commons.adapter.xml.CDataAdapter;
import org.nervousync.commons.Globals;
import org.nervousync.utils.*;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <h2 class="en-US">Abstract class of JavaBean</h2>
 * <span class="en-US">
 *     If JavaBean class extends current abstract class, it's can easier convert object to JSON/XML/YAML string.
 *     Default encoding is UTF-8
 *     Convert object to XML must add annotation to class and fields, using JAXB annotation
 *     Convert custom fields in object to JSON/YAML must add annotation to fields, using jackson annotation
 * </span>
 * <h2 class="zh-CN">JavaBean的抽象类</h2>
 * <span class="zh-CN">
 *     <p>如果JavaBean类继承此抽象类，将可以简单的转化对象为JSON/XML/YAML字符串。默认编码集为UTF-8</p>
 *     <p>转换对象为XML时，必须使用JAXB注解对类和属性进行标注</p>
 *     <p>转换对象中的指定属性值为JSON/YAML时，必须使用Jackson注解对属性进行标注</p>
 * </span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.2.0 $ $Date: Jan 6, 2021 17:10:23 $
 */
@OutputConfig(type = StringUtils.StringType.XML, formatted = true)
public abstract class BeanObject implements Serializable {
	/**
	 * <span class="en-US">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 6900853002518080456L;
	/**
	 * <span class="en-US">XML fragment template</span>
	 * <span class="zh-CN">XML声明模板</span>
	 */
	private static final String FRAGMENT_TEMPLATE = "<?xml version=\"1.0\" encoding=\"{}\"?>";
	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志对象</span>
	 */
	protected transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());
	/**
	 * <h3 class="en-US">Convert current object to not formatted JSON string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为未经格式化的JSON字符串</h3>
	 *
	 * @return 	<span class="en-US">Converted JSON string</span>
	 * 			<span class="zh-CN">转换后的JSON字符串</span>
	 */
	public final String toJson() {
		return StringUtils.objectToString(this, StringUtils.StringType.JSON, Boolean.FALSE);
	}
	/**
	 * <h3 class="en-US">Convert current object to formatted JSON string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为格式化的JSON字符串</h3>
	 *
	 * @return 	<span class="en-US">Converted JSON string</span>
	 * 			<span class="zh-CN">转换后的JSON字符串</span>
	 */
	public final String toFormattedJson() {
		return StringUtils.objectToString(this, StringUtils.StringType.JSON, Boolean.TRUE);
	}
	/**
	 * <h3 class="en-US">Convert current object to not formatted YAML string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为未经格式化的YAML字符串</h3>
	 *
	 * @return 	<span class="en-US">Converted YAML string</span>
	 * 			<span class="zh-CN">转换后的YAML字符串</span>
	 */
	public final String toYaml() {
		return StringUtils.objectToString(this, StringUtils.StringType.YAML, Boolean.FALSE);
	}
	/**
	 * <h3 class="en-US">Convert current object to formatted YAML string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为格式化的YAML字符串</h3>
	 *
	 * @return 	<span class="en-US">Converted JSON string</span>
	 * 			<span class="zh-CN">转换后的JSON字符串</span>
	 */
	public final String toFormattedYaml() {
		return StringUtils.objectToString(this, StringUtils.StringType.YAML, Boolean.TRUE);
	}
	/**
	 * <h3 class="en-US">Convert current object to not formatted XML string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为未经格式化的XML字符串</h3>
	 *
	 * @return 	<span class="en-US">Converted XML string, or empty string "" if an error occurs</span>
	 * 			<span class="zh-CN">转换后的XML字符串，如果转换过程中出现异常则返回空字符串</span>
	 */
	public final String toXML() {
		return this.toXML(Boolean.FALSE);
	}
	/**
	 * <h3 class="en-US">Convert current object to XML string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为XML字符串</h3>
	 *
	 * @param formattedOutput 	<span class="en-US">Output formatted XML string status. <code>TRUE</code> or <code>FALSE</code></span>
	 *                          <span class="zh-CN">输出格式化的XML字符串状态。<code>TRUE</code>或<code>FALSE</code></span>
	 * @return 	<span class="en-US">Converted XML string, or empty string "" if an error occurs</span>
	 * 			<span class="zh-CN">转换后的XML字符串，如果转换过程中出现异常则返回空字符串</span>
	 */
	public final String toXML(final boolean formattedOutput) {
		return this.toXML(Boolean.TRUE, formattedOutput);
	}
	/**
	 * <h3 class="en-US">Convert current object to XML string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为XML字符串</h3>
	 *
	 * @param formattedOutput 	<span class="en-US">Output formatted XML string status. <code>TRUE</code> or <code>FALSE</code></span>
	 *                          <span class="zh-CN">输出格式化的XML字符串状态。<code>TRUE</code>或<code>FALSE</code></span>
	 * @param encoding			<span class="en-US">Output string encoding</span>
	 *                          <span class="zh-CN">输出字符串使用的字符集</span>
	 * @return 	<span class="en-US">Converted XML string, or empty string "" if an error occurs</span>
	 * 			<span class="zh-CN">转换后的XML字符串，如果转换过程中出现异常则返回空字符串</span>
	 */
	public final String toXML(final boolean formattedOutput, final String encoding) {
		return this.toXML(Boolean.TRUE, formattedOutput, encoding);
	}
	/**
	 * <h3 class="en-US">Convert current object to XML string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为XML字符串</h3>
	 *
	 * @param outputFragment 	<span class="en-US">Output XML fragment status. <code>TRUE</code> or <code>FALSE</code></span>
	 *                          <span class="zh-CN">输出的XML声明字符串状态。<code>TRUE</code>或<code>FALSE</code></span>
	 * @param formattedOutput 	<span class="en-US">Output formatted XML string status. <code>TRUE</code> or <code>FALSE</code></span>
	 *                          <span class="zh-CN">输出格式化的XML字符串状态。<code>TRUE</code>或<code>FALSE</code></span>
	 * @return 	<span class="en-US">Converted XML string, or empty string "" if an error occurs</span>
	 * 			<span class="zh-CN">转换后的XML字符串，如果转换过程中出现异常则返回空字符串</span>
	 */
	public final String toXML(final boolean outputFragment, final boolean formattedOutput) {
		return this.toXML(outputFragment, formattedOutput, Globals.DEFAULT_ENCODING);
	}
	/**
	 * <h3 class="en-US">Convert current object to XML string</h3>
	 * <h3 class="zh-CN">转换当前实例对象为XML字符串</h3>
	 *
	 * @param outputFragment 	<span class="en-US">Output XML fragment status. <code>TRUE</code> or <code>FALSE</code></span>
	 *                          <span class="zh-CN">输出的XML声明字符串状态。<code>TRUE</code>或<code>FALSE</code></span>
	 * @param formattedOutput 	<span class="en-US">Output formatted XML string status. <code>TRUE</code> or <code>FALSE</code></span>
	 *                          <span class="zh-CN">输出格式化的XML字符串状态。<code>TRUE</code>或<code>FALSE</code></span>
	 * @param encoding			<span class="en-US">Output string encoding</span>
	 *                          <span class="zh-CN">输出字符串使用的字符集</span>
	 * @return 	<span class="en-US">Converted XML string, or empty string "" if an error occurs</span>
	 * 			<span class="zh-CN">转换后的XML字符串，如果转换过程中出现异常则返回空字符串</span>
	 */
	public final String toXML(final boolean outputFragment, final boolean formattedOutput, final String encoding) {
		StringWriter stringWriter = null;

		try {
			String characterEncoding = StringUtils.isEmpty(encoding) ? Globals.DEFAULT_ENCODING : encoding;
			stringWriter = new StringWriter();
			XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
			CDataStreamWriter streamWriter = new CDataStreamWriter(xmlWriter);

			JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formattedOutput);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, characterEncoding);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			marshaller.marshal(this, streamWriter);

			streamWriter.flush();
			streamWriter.close();

			if (formattedOutput) {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, characterEncoding);
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");

				String xml = stringWriter.toString();
				stringWriter = new StringWriter();

				transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(stringWriter));
			}

			StringBuilder stringBuilder = new StringBuilder();
			if (outputFragment) {
				stringBuilder.append(StringUtils.replace(FRAGMENT_TEMPLATE, "{}", characterEncoding));
				if (formattedOutput) {
					stringBuilder.append(FileUtils.LF);
				}
			}
			stringBuilder.append(stringWriter);
			return stringBuilder.toString();
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Stack_Message_Error", e);
			}
			return Globals.DEFAULT_VALUE_STRING;
		} finally {
			IOUtils.closeStream(stringWriter);
		}
	}
	/**
	 * (non-javadoc)
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(final Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) {
			return Boolean.FALSE;
		}
		if (this == o) {
			return Boolean.TRUE;
		}
		return Arrays.stream(this.getClass().getDeclaredFields())
				.filter(field -> !ReflectionUtils.staticMember(field))
				.allMatch(field ->
					Objects.equals(ReflectionUtils.getFieldValue(field, this),
							ReflectionUtils.getFieldValue(field, o)));
	}
	/**
	 * (non-javadoc)
	 * @see Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		int result = Globals.INITIALIZE_INT_VALUE;
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				Object origValue = ReflectionUtils.getFieldValue(field, this);
				result = Globals.MULTIPLIER * result + (origValue != null ? origValue.hashCode() : 0);
			}
		} catch (Exception e) {
			result = Globals.DEFAULT_VALUE_INT;
		}
		return result;
	}
	/**
	 * (non-javadoc)
	 * @see Object#toString()
	 */
	@Override
	public final String toString() {
		OutputConfig outputConfig = this.getClass().getAnnotation(OutputConfig.class);
		StringUtils.StringType stringType = Optional.ofNullable(outputConfig)
				.map(OutputConfig::type)
				.orElse(StringUtils.StringType.SIMPLE);
		switch (stringType) {
			case XML:
				return this.toXML(outputConfig.formatted(), outputConfig.encoding());
			case JSON:
				return outputConfig.formatted() ? this.toFormattedJson() : this.toJson();
			case YAML:
				return outputConfig.formatted() ? this.toFormattedYaml() : this.toYaml();
			default:
				return super.toString();
		}
	}

	/**
	 * Writer for output CData string
	 */
	private static final class CDataStreamWriter implements XMLStreamWriter {

		private final XMLStreamWriter xmlStreamWriter;

		/**
		 * Instantiates a new C data stream writer.
		 *
		 * @param xmlStreamWriter the xml stream writer
		 */
		CDataStreamWriter(final XMLStreamWriter xmlStreamWriter) {
			this.xmlStreamWriter = xmlStreamWriter;
		}

		/**
		 * Writes a start tag to the output.  All writeStartElement methods
		 * open a new scope in the internal namespace context.  Writing the
		 * corresponding EndElement causes the scope to be closed.
		 *
		 * @param localName the local name of the tag may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeStartElement(final String localName) throws XMLStreamException {
			this.xmlStreamWriter.writeStartElement(localName);
		}

		/**
		 * Writes a start tag to the output
		 *
		 * @param namespaceURI the namespaceURI of the prefix to use, may not be null
		 * @param localName    the local name of the tag may not be null
		 * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
		 *                            javax.xml.stream.isRepairingNamespaces has not been set to true
		 */
		@Override
		public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
			this.xmlStreamWriter.writeStartElement(namespaceURI, localName);
		}

		/**
		 * Writes a start tag to the output
		 *
		 * @param prefix       the prefix of the tag may not be null
		 * @param localName    the local name of the tag may not be null
		 * @param namespaceURI the uri to bind the prefix to, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeStartElement(final String prefix, final String localName, final String namespaceURI)
				throws XMLStreamException {
			this.xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
		}

		/**
		 * Writes an empty element tag to the output
		 *
		 * @param namespaceURI the uri to bind the tag to, may not be null
		 * @param localName    the local name of the tag may not be null
		 * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
		 *                            javax.xml.stream.isRepairingNamespaces has not been set to true
		 */
		@Override
		public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
			this.xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
		}

		/**
		 * Writes an empty element tag to the output
		 *
		 * @param prefix       the prefix of the tag may not be null
		 * @param localName    the local name of the tag may not be null
		 * @param namespaceURI the uri to bind the tag to, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI)
				throws XMLStreamException {
			this.xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
		}

		/**
		 * Writes an empty element tag to the output
		 *
		 * @param localName the local name of the tag may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEmptyElement(final String localName) throws XMLStreamException {
			this.xmlStreamWriter.writeEmptyElement(localName);
		}

		/**
		 * Writes an end tag to the output relying on the internal
		 * state of the writer to determine the prefix and local name
		 * of the event.
		 *
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEndElement() throws XMLStreamException {
			this.xmlStreamWriter.writeEndElement();
		}

		/**
		 * Closes any start tags and writes corresponding end tags.
		 *
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEndDocument() throws XMLStreamException {
			this.xmlStreamWriter.writeEndDocument();
		}

		/**
		 * Close this writer and free any resources associated with the
		 * writer.  This must not close the underlying output stream.
		 *
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void close() throws XMLStreamException {
			this.xmlStreamWriter.close();
		}

		/**
		 * Write any cached data to the underlying output mechanism.
		 *
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void flush() throws XMLStreamException {
			this.xmlStreamWriter.flush();
		}

		/**
		 * Writes an attribute to the output stream without
		 * a prefix.
		 *
		 * @param localName the local name of the attribute
		 * @param value     the value of the attribute
		 * @throws IllegalStateException if the current state does not allow Attribute writing
		 * @throws XMLStreamException    if the namespace URI has not been bound to a prefix and
		 *                               javax.xml.stream.isRepairingNamespaces has not been set to true
		 */
		@Override
		public void writeAttribute(final String localName, final String value) throws XMLStreamException {
			this.xmlStreamWriter.writeAttribute(localName, value);
		}

		/**
		 * Writes an attribute to the output stream
		 *
		 * @param prefix       the prefix for this attribute
		 * @param namespaceURI the uri of the prefix for this attribute
		 * @param localName    the local name of the attribute
		 * @param value        the value of the attribute
		 * @throws IllegalStateException if the current state does not allow Attribute writing
		 * @throws XMLStreamException    if the namespace URI has not been bound to a prefix and
		 *                               javax.xml.stream.isRepairingNamespaces has not been set to true
		 */
		@Override
		public void writeAttribute(final String prefix, final String namespaceURI,
								   final String localName, final String value) throws XMLStreamException {
			this.xmlStreamWriter.writeAttribute(prefix, namespaceURI, localName, value);
		}

		/**
		 * Writes an attribute to the output stream
		 *
		 * @param namespaceURI the uri of the prefix for this attribute
		 * @param localName    the local name of the attribute
		 * @param value        the value of the attribute
		 * @throws IllegalStateException if the current state does not allow Attribute writing
		 * @throws XMLStreamException    if the namespace URI has not been bound to a prefix and
		 *                               javax.xml.stream.isRepairingNamespaces has not been set to true
		 */
		@Override
		public void writeAttribute(final String namespaceURI, final String localName, final String value)
				throws XMLStreamException {
			this.xmlStreamWriter.writeAttribute(namespaceURI, localName, value);
		}

		/**
		 * Writes a namespace to the output stream
		 * If the prefix argument to this method is the empty string,
		 * "xmlns" or null this method will delegate to writeDefaultNamespace
		 *
		 * @param prefix       the prefix to bind this namespace to
		 * @param namespaceURI the uri to bind the prefix to
		 * @throws IllegalStateException if the current state does not allow Namespace writing
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
			this.xmlStreamWriter.writeNamespace(prefix, namespaceURI);
		}

		/**
		 * Writes the default namespace to the stream
		 *
		 * @param namespaceURI the uri to bind the default namespace to
		 * @throws IllegalStateException if the current state does not allow Namespace writing
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
			this.xmlStreamWriter.writeDefaultNamespace(namespaceURI);
		}

		/**
		 * Writes a xml comment with the data enclosed
		 *
		 * @param data the data contained in the comment, may be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeComment(final String data) throws XMLStreamException {
			this.xmlStreamWriter.writeComment(data);
		}

		/**
		 * Writes a processing instruction
		 *
		 * @param target the target of the processing instruction may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeProcessingInstruction(final String target) throws XMLStreamException {
			this.xmlStreamWriter.writeProcessingInstruction(target);
		}

		/**
		 * Writes a processing instruction
		 *
		 * @param target the target of the processing instruction may not be null
		 * @param data   the data contained in the processing instruction, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
			this.xmlStreamWriter.writeProcessingInstruction(target, data);
		}

		/**
		 * Writes a CData section
		 *
		 * @param data the data contained in the CData Section, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeCData(final String data) throws XMLStreamException {
			this.xmlStreamWriter.writeCData(data);
		}

		/**
		 * Write a DTD section.  This string represents the entire doc type decl production
		 * from the XML 1.0 specification.
		 *
		 * @param dtd the DTD to be written
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeDTD(final String dtd) throws XMLStreamException {
			this.xmlStreamWriter.writeDTD(dtd);
		}

		/**
		 * Writes an entity reference
		 *
		 * @param name the name of the entity
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEntityRef(final String name) throws XMLStreamException {
			this.xmlStreamWriter.writeEntityRef(name);
		}

		/**
		 * Write the XML Declaration. Defaults the XML version to 1.0, and the encoding to utf-8
		 *
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeStartDocument() throws XMLStreamException {
			this.xmlStreamWriter.writeStartDocument();
		}

		/**
		 * Write the XML Declaration. Defaults the XML version to 1.0
		 *
		 * @param version version of the xml document
		 * @throws XMLStreamException If given encoding does not match encoding
		 *                            of the underlying stream
		 */
		@Override
		public void writeStartDocument(final String version) throws XMLStreamException {
			this.xmlStreamWriter.writeStartDocument(version);
		}

		/**
		 * Write the XML Declaration.  Note that the encoding parameter does
		 * not set the actual encoding of the underlying output.  That must
		 * be set when the instance of the XMLStreamWriter is created using the
		 * XMLOutputFactory
		 *
		 * @param encoding encoding of the xml declaration
		 * @param version  version of the xml document
		 * @throws XMLStreamException If given encoding does not match encoding
		 *                            of the underlying stream
		 */
		@Override
		public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
			this.xmlStreamWriter.writeStartDocument(encoding, version);
		}

		/**
		 * Write text to the output
		 *
		 * @param text the value to write
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeCharacters(final String text) throws XMLStreamException {
			if (text.startsWith(CDataAdapter.CDATA_BEGIN) && text.endsWith(CDataAdapter.CDATA_END)) {
				this.writeCData(text.substring(CDataAdapter.CDATA_BEGIN.length(),
						text.length() - CDataAdapter.CDATA_END.length()));
			} else {
				this.xmlStreamWriter.writeCharacters(text);
			}
		}

		/**
		 * Write text to the output
		 *
		 * @param text  the value to write
		 * @param start the starting position in the array
		 * @param len   the number of characters to write
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
			this.writeCharacters(new String(text, start, len));
		}

		/**
		 * Gets the prefix the uri is bound to
		 *
		 * @param uri    the uri to bind to the prefix
		 * @return the prefix or null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public String getPrefix(final String uri) throws XMLStreamException {
			return this.xmlStreamWriter.getPrefix(uri);
		}

		/**
		 * Sets the prefix the uri is bound to.  This prefix is bound
		 * in the scope of the current START_ELEMENT / END_ELEMENT pair.
		 * If this method is called before a START_ELEMENT has been written,
		 * the prefix is bound in the root scope.
		 *
		 * @param prefix the prefix to bind to the uri, may not be null
		 * @param uri    the uri to bind to the prefix, may be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
			this.xmlStreamWriter.setPrefix(prefix, uri);
		}

		/**
		 * Binds a URI to the default namespace
		 * This URI is bound
		 * in the scope of the current START_ELEMENT / END_ELEMENT pair.
		 * If this method is called before a START_ELEMENT has been written,
		 * the uri is bound in the root scope.
		 *
		 * @param uri the uri to bind to the default namespace, may be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void setDefaultNamespace(final String uri) throws XMLStreamException {
			this.xmlStreamWriter.setDefaultNamespace(uri);
		}

		/**
		 * Sets the current namespace context for prefix and uri bindings.
		 * This context becomes the root namespace context for writing and
		 * will replace the current root namespace context.  Subsequent calls
		 * to setPrefix and setDefaultNamespace will bind namespaces using
		 * the context passed to the method as the root context for resolving
		 * namespaces.  This method may only be called once at the start of
		 * the document.  It does not cause the namespaces to be declared.
		 * If a namespace URI to prefix mapping is found in the namespace
		 * context, it is treated as declared and the prefix may be used
		 * by the StreamWriter.
		 *
		 * @param context the namespace context to use for this writer, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
			this.xmlStreamWriter.setNamespaceContext(context);
		}

		/**
		 * Returns the current namespace context.
		 *
		 * @return the current NamespaceContext
		 */
		@Override
		public NamespaceContext getNamespaceContext() {
			return this.xmlStreamWriter.getNamespaceContext();
		}

		/**
		 * Get the value of a feature/property from the underlying implementation
		 *
		 * @param name The name of the property may not be null
		 * @return The value of the property
		 * @throws IllegalArgumentException if the property is not supported
		 * @throws NullPointerException     if the name is null
		 */
		@Override
		public Object getProperty(final String name) throws IllegalArgumentException {
			return this.xmlStreamWriter.getProperty(name);
		}
	}
}
