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
package com.nervousync.commons.beans.xml;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Objects;

import com.nervousync.commons.adapter.xml.CDataAdapter;
import com.nervousync.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.xml.XmlException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Base element define, all xml object define must extends this class
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Sep 23, 2010, 2010 1:22:51 PM $
 */
public class BaseElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6377799204139380357L;
	
	private static transient final String FRAGMENT = "<?xml version=\"1.0\" encoding=\"{}\"?>";
	
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Parse xml string and setting fields data to this object
	 * @param <T>           Object
	 * @param xmlObj	    XML string or XML file location will be parsed
	 * @param entityClass   Entity class
	 *
	 * @return              Convert object
	 */
	public static <T> T parseXml(String xmlObj, Class<T> entityClass) {
		return BaseElement.parseXml(xmlObj, Globals.DEFAULT_ENCODING, entityClass);
	}

	/**
	 * Parse xml string and setting fields data to this object
	 * @param <T>           Object
	 * @param xmlObj	    XML string or XML file location will be parsed
	 * @param encoding      Character encoding
	 * @param entityClass   Entity class
	 *
	 * @return              Convert object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parseXml(String xmlObj, String encoding, Class<T> entityClass) {
		InputStream inputStream = null;
		
		try {
			if (encoding == null) {
				encoding = Globals.DEFAULT_ENCODING;
			}
			if (xmlObj.startsWith("<")) {
				inputStream = new ByteArrayInputStream(xmlObj.getBytes(encoding));
			} else {
				inputStream = FileUtils.loadFile(xmlObj);
			}
			
			JAXBContext jaxbContext = JAXBContext.newInstance(entityClass);
			return (T)jaxbContext.createUnmarshaller().unmarshal(inputStream);
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeStream(inputStream);
		}
	}

	/**
	 * Convert Object to XML String
	 * @return XML String
	 */
	public String toString() throws XmlException {
		return this.toString(true);
	}

	/**
	 * Convert Object to XML String
	 * Explain all empty element
	 *
	 * @param formattedOutput 		Formatted output
	 * @return XML String
	 */
	public String toString(boolean formattedOutput) throws XmlException {
		return this.toString(formattedOutput, Globals.DEFAULT_ENCODING);
	}
	
	/**
	 * Convert Object to XML String
	 * Explain all empty element
	 * 
	 * @param formattedOutput 		Formatted output
	 * @param encoding				Charset encoding
	 * @return XML String
	 */
	public String toString(boolean formattedOutput, String encoding) throws XmlException {
		StringWriter stringWriter = null;
		
		try{
			if (encoding == null) {
				encoding = Globals.DEFAULT_ENCODING;
			}
			stringWriter = new StringWriter();
			XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
			CDataStreamWriter streamWriter = new CDataStreamWriter(xmlWriter);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formattedOutput);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			
			marshaller.marshal(this, streamWriter);
			streamWriter.flush();
			streamWriter.close();
			
			if (formattedOutput) {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				
				String xml = stringWriter.toString();
				stringWriter = new StringWriter();
				
				transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(stringWriter));
			}
			if (formattedOutput) {
				return StringUtils.replace(FRAGMENT, "{}", encoding) + "\n" + stringWriter.toString();
			} else {
				return StringUtils.replace(FRAGMENT, "{}", encoding) + stringWriter.toString();
			}
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Error stack message: ", e);
			}
			return null;
		} finally {
			IOUtils.closeStream(stringWriter);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		if (this == o) {
			return true;
		}
		
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		
		Field[] fields = this.getClass().getDeclaredFields();
		
		try {
			for (Field field : fields) {
				Object origValue = ReflectionUtils.getFieldValue(field, this);
				Object destValue = ReflectionUtils.getFieldValue(field, o);

				if (!Objects.equals(origValue, destValue)) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		Field[] fields = this.getClass().getDeclaredFields();
		
		int result = Globals.INITIAL_HASH;

		try {
			for (Field field : fields) {
				Object origValue = ReflectionUtils.getFieldValue(field, this);
				result = Globals.MULTIPLIER * result + (origValue != null ? origValue.hashCode() : 0);
			}
		} catch (Exception e) {
			result = Globals.INITIAL_HASH;
		}
		
		return result;
	}
	
	private static final class CDataStreamWriter extends DelegatingXMLStreamWriter {
		
		CDataStreamWriter(XMLStreamWriter xmlStreamWriter) {
			super(xmlStreamWriter);
		}
		
		@Override
		public void writeCharacters(String text) throws XMLStreamException {
			if (text.startsWith(CDataAdapter.CDATA_BEGIN) && text.endsWith(CDataAdapter.CDATA_END)) {
				super.writeCData(text.substring(CDataAdapter.CDATA_BEGIN.length(), text.length() - CDataAdapter.CDATA_END.length()));
			} else {
				super.writeCharacters(text);
			}
		}
	}
	
	private static class DelegatingXMLStreamWriter implements XMLStreamWriter {
	
		private final XMLStreamWriter xmlStreamWriter;
		
		DelegatingXMLStreamWriter(XMLStreamWriter xmlStreamWriter) {
			this.xmlStreamWriter = xmlStreamWriter;
		}
		
		/**
		 * Writes a start tag to the output.  All writeStartElement methods
		 * open a new scope in the internal namespace context.  Writing the
		 * corresponding EndElement causes the scope to be closed.
		 *
		 * @param localName local name of the tag, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeStartElement(String localName) throws XMLStreamException {
			this.xmlStreamWriter.writeStartElement(localName);
		}
		
		/**
		 * Writes a start tag to the output
		 *
		 * @param namespaceURI the namespaceURI of the prefix to use, may not be null
		 * @param localName    local name of the tag, may not be null
		 * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
		 *                            javax.xml.stream.isRepairingNamespaces has not been set to true
		 */
		@Override
		public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
			this.xmlStreamWriter.writeStartElement(namespaceURI, localName);
		}
		
		/**
		 * Writes a start tag to the output
		 *
		 * @param prefix       the prefix of the tag, may not be null
		 * @param localName    local name of the tag, may not be null
		 * @param namespaceURI the uri to bind the prefix to, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
			this.xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
		}
		
		/**
		 * Writes an empty element tag to the output
		 *
		 * @param namespaceURI the uri to bind the tag to, may not be null
		 * @param localName    local name of the tag, may not be null
		 * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
		 *                            javax.xml.stream.isRepairingNamespaces has not been set to true
		 */
		@Override
		public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
			this.xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
		}
		
		/**
		 * Writes an empty element tag to the output
		 *
		 * @param prefix       the prefix of the tag, may not be null
		 * @param localName    local name of the tag, may not be null
		 * @param namespaceURI the uri to bind the tag to, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
			this.xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
		}
		
		/**
		 * Writes an empty element tag to the output
		 *
		 * @param localName local name of the tag, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEmptyElement(String localName) throws XMLStreamException {
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
		public void writeAttribute(String localName, String value) throws XMLStreamException {
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
		public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
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
		public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
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
		public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
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
		public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
			this.xmlStreamWriter.writeDefaultNamespace(namespaceURI);
		}
		
		/**
		 * Writes an xml comment with the data enclosed
		 *
		 * @param data the data contained in the comment, may be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeComment(String data) throws XMLStreamException {
			this.xmlStreamWriter.writeComment(data);
		}
		
		/**
		 * Writes a processing instruction
		 *
		 * @param target the target of the processing instruction, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeProcessingInstruction(String target) throws XMLStreamException {
			this.xmlStreamWriter.writeProcessingInstruction(target);
		}
		
		/**
		 * Writes a processing instruction
		 *
		 * @param target the target of the processing instruction, may not be null
		 * @param data   the data contained in the processing instruction, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
			this.xmlStreamWriter.writeProcessingInstruction(target, data);
		}
		
		/**
		 * Writes a CData section
		 *
		 * @param data the data contained in the CData Section, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeCData(String data) throws XMLStreamException {
			this.xmlStreamWriter.writeCData(data);
		}
		
		/**
		 * Write a DTD section.  This string represents the entire doctypedecl production
		 * from the XML 1.0 specification.
		 *
		 * @param dtd the DTD to be written
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeDTD(String dtd) throws XMLStreamException {
			this.xmlStreamWriter.writeDTD(dtd);
		}
		
		/**
		 * Writes an entity reference
		 *
		 * @param name the name of the entity
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeEntityRef(String name) throws XMLStreamException {
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
		public void writeStartDocument(String version) throws XMLStreamException {
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
		public void writeStartDocument(String encoding, String version) throws XMLStreamException {
			this.xmlStreamWriter.writeStartDocument(encoding, version);
		}
		
		/**
		 * Write text to the output
		 *
		 * @param text the value to write
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void writeCharacters(String text) throws XMLStreamException {
			this.xmlStreamWriter.writeCharacters(text);
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
		public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
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
		public String getPrefix(String uri) throws XMLStreamException {
			return this.xmlStreamWriter.getPrefix(uri);
		}
		
		/**
		 * Sets the prefix the uri is bound to.  This prefix is bound
		 * in the scope of the current START_ELEMENT / END_ELEMENT pair.
		 * If this method is called before a START_ELEMENT has been written
		 * the prefix is bound in the root scope.
		 *
		 * @param prefix the prefix to bind to the uri, may not be null
		 * @param uri    the uri to bind to the prefix, may be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void setPrefix(String prefix, String uri) throws XMLStreamException {
			this.xmlStreamWriter.setPrefix(prefix, uri);
		}
		
		/**
		 * Binds a URI to the default namespace
		 * This URI is bound
		 * in the scope of the current START_ELEMENT / END_ELEMENT pair.
		 * If this method is called before a START_ELEMENT has been written
		 * the uri is bound in the root scope.
		 *
		 * @param uri the uri to bind to the default namespace, may be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void setDefaultNamespace(String uri) throws XMLStreamException {
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
		 * context it is treated as declared and the prefix may be used
		 * by the StreamWriter.
		 *
		 * @param context the namespace context to use for this writer, may not be null
		 * @throws XMLStreamException   XMLStreamException
		 */
		@Override
		public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
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
		 * @param name The name of the property, may not be null
		 * @return The value of the property
		 * @throws IllegalArgumentException if the property is not supported
		 * @throws NullPointerException     if the name is null
		 */
		@Override
		public Object getProperty(String name) throws IllegalArgumentException {
			return this.xmlStreamWriter.getProperty(name);
		}
	}
}
