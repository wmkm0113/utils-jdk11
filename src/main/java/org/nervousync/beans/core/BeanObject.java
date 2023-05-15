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
import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.xml.XmlException;
import org.nervousync.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * The type Bean object.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2021/1/6 17:10 $
 */
public class BeanObject implements Serializable {

	private static final long serialVersionUID = 6900853002518080456L;

	private static final String FRAGMENT = "<?xml version=\"1.0\" encoding=\"{}\"?>";

	/**
	 * The constant LOGGER.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(BeanObject.class);

	/**
	 * To json string string.
	 *
	 * @return the string
	 */
	public final String toJson() {
		return StringUtils.objectToString(this, StringUtils.StringType.JSON, Boolean.FALSE);
	}

	/**
	 * To json string string.
	 *
	 * @return the string
	 */
	public final String toFormattedJson() {
		return StringUtils.objectToString(this, StringUtils.StringType.JSON, Boolean.TRUE);
	}

	/**
	 * To json string string.
	 *
	 * @return the string
	 */
	public final String toYaml() {
		return StringUtils.objectToString(this, StringUtils.StringType.YAML, Boolean.FALSE);
	}

	/**
	 * To json string string.
	 *
	 * @return the string
	 */
	public final String toFormattedYaml() {
		return StringUtils.objectToString(this, StringUtils.StringType.YAML, Boolean.TRUE);
	}

	/**
	 * Convert Object to XML String
	 *
	 * @return XML String
	 * @throws XmlException the xml exception
	 */
	public final String toXML() throws XmlException {
		return this.toXML(Boolean.TRUE);
	}

	/**
	 * Convert Object to XML String
	 * Explain all empty elements
	 *
	 * @param formattedOutput Formatted output
	 * @return XML String
	 * @throws XmlException the xml exception
	 */
	public final String toXML(final boolean formattedOutput) throws XmlException {
		return this.toXML(Boolean.TRUE, formattedOutput);
	}

	/**
	 * Convert Object to XML String
	 * Explain all empty elements
	 *
	 * @param formattedOutput Formatted output
	 * @param encoding        Charset encoding
	 * @return XML String
	 * @throws XmlException the xml exception
	 */
	public final String toXML(final boolean formattedOutput, final String encoding) throws XmlException {
		return this.toXML(Boolean.TRUE, formattedOutput, encoding);
	}

	/**
	 * Convert Object to XML String
	 * Explain all empty elements
	 *
	 * @param outputFragment  Output fragment
	 * @param formattedOutput Formatted output
	 * @return XML String
	 * @throws XmlException the xml exception
	 */
	public final String toXML(final boolean outputFragment, final boolean formattedOutput) throws XmlException {
		return this.toXML(outputFragment, formattedOutput, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Convert Object to XML String
	 * Explain all empty elements
	 *
	 * @param outputFragment  Output fragment
	 * @param formattedOutput Formatted output
	 * @param encoding        Charset encoding
	 * @return XML String
	 * @throws XmlException the xml exception
	 */
	public final String toXML(final boolean outputFragment, final boolean formattedOutput, final String encoding)
			throws XmlException {
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
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

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
			if (outputFragment) {
				if (formattedOutput) {
					return StringUtils.replace(FRAGMENT, "{}", characterEncoding) + FileUtils.LF + stringWriter;
				} else {
					return StringUtils.replace(FRAGMENT, "{}", characterEncoding) + stringWriter;
				}
			} else {
				return stringWriter.toString();
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Error stack message: ", e);
			}
			return null;
		} finally {
			IOUtils.closeStream(stringWriter);
		}
	}

	@Override
	public final boolean equals(final Object o) {
		if (o == null) {
			return Boolean.FALSE;
		}

		if (this == o) {
			return Boolean.TRUE;
		}

		if (!o.getClass().equals(this.getClass())) {
			return Boolean.FALSE;
		}

		Field[] fields = this.getClass().getDeclaredFields();

		try {
			for (Field field : fields) {
				if (ReflectionUtils.staticMember(field)) {
					//	Ignore static field
					continue;
				}
				Object origValue = ReflectionUtils.getFieldValue(field, this);
				Object destValue = ReflectionUtils.getFieldValue(field, o);

				if (!Objects.equals(origValue, destValue)) {
					return Boolean.FALSE;
				}
			}
		} catch (Exception e) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public final int hashCode() {
		Field[] fields = this.getClass().getDeclaredFields();

		int result = Globals.INITIAL_HASH;

		try {
			for (Field field : fields) {
				Object origValue = ReflectionUtils.getFieldValue(field, this);
				result = Globals.MULTIPLIER * result + (origValue != null ? origValue.hashCode() : 0);
			}
		} catch (Exception e) {
			result = Globals.DEFAULT_VALUE_INT;
		}

		return result;
	}

	@Override
	public final String toString() {
		return Optional.ofNullable(this.getClass().getAnnotation(OutputConfig.class))
				.map(outputConfig -> {
					switch (outputConfig.type()) {
						case XML:
							return this.toXML(outputConfig.formatted(), outputConfig.encoding());
						case JSON:
							return outputConfig.formatted() ? this.toFormattedJson() : this.toJson();
						case YAML:
							return outputConfig.formatted() ? this.toFormattedYaml() : this.toYaml();
						default:
							return this.objectToString();
					}
				})
				.orElse(this.objectToString());
	}

	public String objectToString() {
		return super.toString();
	}

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
		 * "xmlns", or null this method will delegate to writeDefaultNamespace
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
