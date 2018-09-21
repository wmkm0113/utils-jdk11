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
package com.nervousync.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.core.Globals;
import com.nervousync.enumerations.xml.DataType;
import com.nervousync.exceptions.xml.XmlException;

/**
 * @author Steven Wee <a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2009/09/28 14:32:00 $
 */
public final class XmlUtils {
	
	public static final String DEFAULT_NAME = "##default";

	//	Log Object
	private final static Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);
	
	private XmlUtils() {
		
	}
	
	public static Document loadXml(String xml, XmlNs xmlNs) throws XmlException {
		return XmlUtils.loadXml(xml, Globals.DEFAULT_ENCODING, xmlNs);
	}
	
	public static Document loadXml(String xml, String encoding, XmlNs xmlNs) throws XmlException {
		InputStream inputStream = null;
		
		try {
			byte[] xmlBytes;
			if (encoding == null) {
				xmlBytes = xml.getBytes();
			} else {
				xmlBytes = xml.getBytes(encoding);
			}
			inputStream = new ByteArrayInputStream(xmlBytes);
			SAXReader reader = new SAXReader();
			
			if (xmlNs != null) {
				Map<String, String> namespaceURIs = new HashMap<>();
				namespaceURIs.put(xmlNs.prefix(), xmlNs.namespaceURI());
				
				reader.getDocumentFactory().setXPathNamespaceURIs(namespaceURIs);
			}
			
			return reader.read(inputStream);
		} catch (Exception e) {
			throw new XmlException("Load xml file error! ", e);
		} finally {
			IOUtils.closeStream(inputStream);
		}
	}

	public static String convertToXml(Object object, String indent, String encoding, 
			boolean expandEmptyElements) throws XmlException {
		Document document = XmlUtils.convertToXmlDocument(object, !expandEmptyElements);
		return XmlUtils.convertToXmlString(document, indent, encoding, expandEmptyElements);
	}

	public static <T> T convertToObject(String xml, Class<T> classType) throws XmlException {
		return convertToObject(xml, Globals.DEFAULT_ENCODING, classType);
	}
	
	public static <T> T convertToObject(String xml, String encoding, Class<T> classType) throws XmlException {
		XmlNs xmlNs = null;
		
		if (classType.isAnnotationPresent(XmlNs.class)) {
			xmlNs = classType.getAnnotation(XmlNs.class);
		}
		
		Document document = XmlUtils.loadXml(xml, encoding, xmlNs);

		return XmlUtils.convertToObject(document, classType);
	}
	
	public static <T> T convertToObject(Object xmlObj, Class<T> classType) throws XmlException {
		T object = null;
		
		if (xmlObj == null) {
			return null;
		}
		
		XmlNs xmlNs = null;
		if (classType.isAnnotationPresent(XmlNs.class)) {
			xmlNs = classType.getAnnotation(XmlNs.class);
		}
		
		boolean annotationCheck = Globals.DEFAULT_VALUE_BOOLEAN;
		Element element = null;
		if (xmlObj instanceof Document) {
			annotationCheck = classType.isAnnotationPresent(XmlRootElement.class);
			element = ((Document)xmlObj).getRootElement();
		} else if (xmlObj instanceof Element) {
			annotationCheck = classType.isAnnotationPresent(XmlType.class);
			element = (Element)xmlObj;
		} else if (xmlObj instanceof InputStream) {
			return XmlUtils.convertToObject(IOUtils.readContent((InputStream)xmlObj), classType);
		} else if (xmlObj instanceof String) {
			if (((String)xmlObj).startsWith("<")) {
				return XmlUtils.convertToObject(XmlUtils.loadXml((String)xmlObj, xmlNs), classType);
			} else if (FileUtils.isExists((String)xmlObj)) {
				return XmlUtils.convertToObject(XmlUtils.loadXml(FileUtils.readFile((String)xmlObj), xmlNs), classType);
			}
		}

		if (element == null) {
			return null;
		}
		
		if (annotationCheck) {
			try {
				Constructor<T> constructor;
				if (classType.getName().indexOf('$') == -1) {
					constructor = classType.getDeclaredConstructor();
					constructor.setAccessible(true);
					object = constructor.newInstance();
				} else {
					if (!Modifier.isStatic(classType.getModifiers())) {
						String parameterTypeName = classType.getName();
						parameterTypeName = parameterTypeName.substring(0, parameterTypeName.indexOf('$'));
						Class<?> parameterType = ClassUtils.forName(parameterTypeName);
						constructor = classType.getDeclaredConstructor(parameterType);
						constructor.setAccessible(true);
						
						Constructor<?> paramConstructor = parameterType.getDeclaredConstructor();
						paramConstructor.setAccessible(true);
						
						Object paramObj = paramConstructor.newInstance();
						
						object = constructor.newInstance(paramObj);
					} else {
						constructor = classType.getDeclaredConstructor();
						constructor.setAccessible(true);
						object = constructor.newInstance();
					}
				}

				List<Field> fields = XmlUtils.getFields(classType);

				for (Field field : fields) {
					// 遍历所有属性
					String fieldName = field.getName();
					Object paramObj = null;
					
					if (field.isAnnotationPresent(XmlAttribute.class)) {
						// 标注为Attribute
						XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);

						String attributeName = xmlAttribute.name();
						
						if (DEFAULT_NAME.equals(attributeName)
								|| attributeName.trim().length() == 0) {
							attributeName = fieldName;
						}
	
						paramObj = StringUtils.parseSimpleData(element.attributeValue(attributeName), field.getType());
					} else if (field.isAnnotationPresent(XmlElement.class)) {
						XmlElement xmlElement = field.getAnnotation(XmlElement.class);

						String elementName = xmlElement.name();
						
						if (DEFAULT_NAME.equals(elementName)
								|| elementName.trim().length() == 0) {
							elementName = fieldName;
						}

						boolean isObject = field.getType().isAnnotationPresent(XmlType.class);
						
						if (isObject) {
							paramObj = convertToObject(element.element(elementName), field.getType());
						} else {
							String elementText = element.elementText(elementName);
							if (elementText != null) {
								paramObj = StringUtils.parseSimpleData(elementText.trim(), field.getType());
							}
						}
					} else if (field.isAnnotationPresent(XmlElementWrapper.class)) {
						XmlElementWrapper xmlElementWrapper = field.getAnnotation(XmlElementWrapper.class);
						String elemName = xmlElementWrapper.name();
						
						if (elemName.trim().length() == 0
								|| DEFAULT_NAME.equals(elemName)) {
							elemName = fieldName;
						}

						Class<?> paramClass;

						boolean isArray = field.getType().isArray();
						
						if (isArray) {
							paramClass = field.getType().getComponentType();
						} else {
							paramClass = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
						}
						
						DataType elemType = ObjectUtils.retrieveSimpleDataType(paramClass);

						String childElemName;
						if (DataType.OBJECT.equals(elemType)) {
							childElemName = readChildElemName(paramClass);
						} else {
							childElemName = elemName;
							elemName = fieldName;
						}

						if (childElemName == null) {
							continue;
						}
						
						List<?> elemChildren = null;
						
						if (isArray) {
							elemChildren = element.elements(childElemName);
						} else {
							Element childElem = element.element(elemName);
							if (childElem != null) {
								elemChildren = childElem.elements(childElemName);
							}
						}
						
						List<Object> objList;
						
						if (elemChildren != null) {
							objList = new ArrayList<>(elemChildren.size());
							
							for (Object childObj : elemChildren) {
								if (DataType.OBJECT.equals(elemType)) {
									objList.add(convertToObject((Element)childObj, paramClass));
								} else {
									String dataValue = ((Element)childObj).getText();
									objList.add(StringUtils.parseSimpleData(dataValue, paramClass));
								}
							}
						} else {
							objList = new ArrayList<>(0);
						}
						
						if (isArray) {
							paramObj = objList.toArray((Object[])Array.newInstance(paramClass, objList.size()));
						} else {
							paramObj = objList;
						}
					} else if (field.isAnnotationPresent(XmlValue.class)) {
						paramObj = StringUtils.parseSimpleData(element.elementText(field.getName()), field.getType());
					}
					
					if (paramObj != null) {
						if (!ReflectionUtils.setField(fieldName, object, paramObj)) {
							LOGGER.error("Set field value failed, field name: {}", fieldName);
						}
					}
				}
			} catch (Exception e) {
				throw new XmlException(e);
			}
		}
		
		return object;
	}
	
	public static boolean simpleDataType(Class<?> targetClass) {
		if (String.class.equals(targetClass) || int.class.equals(targetClass) || Integer.class.equals(targetClass) 
				|| double.class.equals(targetClass) || Double.class.equals(targetClass) || float.class.equals(targetClass) || Float.class.equals(targetClass)
				|| boolean.class.equals(targetClass) || Boolean.class.equals(targetClass) || short.class.equals(targetClass) || Short.class.equals(targetClass)
				|| long.class.equals(targetClass) || Long.class.equals(targetClass) || BigInteger.class.equals(targetClass)) {
			return true;
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	private static List<Field> getFields(Class<?> clazz) {
		List<Field> fieldList = new ArrayList<>();
		List<String> existsFieldName = new ArrayList<>();
		getFields(fieldList, existsFieldName, clazz);

		return fieldList;
	}
	
	private static void getFields(List<Field> fieldList,
			List<String> existsFieldName, Class<?> clazz) {
		if (fieldList == null) {
			fieldList = new ArrayList<>();
		}

		if (existsFieldName == null) {
			existsFieldName = new ArrayList<>();
		}

		if (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (!existsFieldName.contains(field.getName())) {
					fieldList.add(field);
					existsFieldName.add(field.getName());
				}
			}
			if (!clazz.equals(BaseElement.class)) {
				XmlUtils.getFields(fieldList, existsFieldName,
						clazz.getSuperclass());
			}
		}
	}
	
	private static Document convertToXmlDocument(Object object, boolean ignoreNullElement) {
		if (object == null) {
			return null;
		}
		
		Document document = DocumentHelper.createDocument();

		// 获取对象的Class
		Class<?> classType = object.getClass();
		Class<?> paramClass = null;
		Object[] arrayObjects = null;
		
		if (classType.isArray()) {
			paramClass = classType.getComponentType();
			arrayObjects = (Object[])object;
		} else if (Collection.class.isAssignableFrom(classType)) {
			arrayObjects = ((Collection<?>)object).toArray();
			if (arrayObjects.length > 0) {
				paramClass = arrayObjects[0].getClass();
			}
		}

		String rootElemName;
		if (paramClass != null) {
			if (XmlUtils.simpleDataType(paramClass)) {
				rootElemName = paramClass.getSimpleName();

				if (classType.isArray()) {
					for (Object itemObject : arrayObjects) {
						Element element = document.addElement(rootElemName);
						element.addText(itemObject.toString());
					}
				} else {
					Element rootElement = document.addElement(rootElemName + "s");
					for (Object itemObject : arrayObjects) {
						Element element = rootElement.addElement(rootElemName);
						element.addText(itemObject.toString());
					}
				}
			} else {
				XmlRootElement documentAnnotation = paramClass.getAnnotation(XmlRootElement.class);
				rootElemName = documentAnnotation.name();
				if (DEFAULT_NAME.equals(rootElemName)
						|| rootElemName.trim().length() == 0) {
					rootElemName = paramClass.getSimpleName();
				}
				
				if (classType.isArray()) {
					for (Object itemObject : arrayObjects) {
						Element element = document.addElement(rootElemName);
						XmlUtils.convertToXmlElement(paramClass, itemObject, element, ignoreNullElement);
					}
				} else {
					Element rootElement = document.addElement(rootElemName + "s");
					for (Object itemObject : arrayObjects) {
						Element element = rootElement.addElement(rootElemName);
						XmlUtils.convertToXmlElement(paramClass, itemObject, element, ignoreNullElement);
					}
				}
			}
		} else {
			XmlRootElement documentAnnotation = classType.getAnnotation(XmlRootElement.class);
			rootElemName = documentAnnotation.name();
			if (DEFAULT_NAME.equals(rootElemName)
					|| rootElemName.trim().length() == 0) {
				rootElemName = classType.getSimpleName();
			}
			Element rootElement = document.addElement(rootElemName);
			XmlUtils.convertToXmlElement(classType, object, rootElement, ignoreNullElement);
		}
		
		return document;
	}

	private static void convertToXmlElement(Class<?> objectClass, Object object, Element element, boolean ignoreNullElement) {

		if (element == null && ignoreNullElement) {
			return;
		}

		DataType dataType = ObjectUtils.retrieveSimpleDataType(objectClass);
		
		if (DataType.OBJECT.equals(dataType)) {
			List<Field> fields = XmlUtils.getFields(objectClass);
			
			for (Field field : fields) {
				if (field.getAnnotations().length == 0) {
					continue;
				}
				
				String fieldName = field.getName();

				Class<?> fieldClass = field.getType();
				
				if (field.isAnnotationPresent(XmlElementWrapper.class)) {
					if (fieldClass.isArray()) {
						fieldClass = fieldClass.getComponentType();
						//	fieldClass = (Class<?>) field.getGenericType();
					} else {
						fieldClass = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
					}
				}
				
				DataType fieldType = ObjectUtils.retrieveSimpleDataType(fieldClass);
				if (DataType.UNKNOWN.equals(fieldType)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Field type unknown!");
					}
					continue;
				}
				
				try {
					if (field.isAnnotationPresent(XmlNs.class)) {
						XmlNs xmlNs = field.getAnnotation(XmlNs.class);
						element.addNamespace(xmlNs.prefix(), xmlNs.namespaceURI());
					}
					
					Object fieldValue = ReflectionUtils.getFieldValue(fieldName, object);
					
					if (field.isAnnotationPresent(XmlValue.class)) {
						if (fieldValue != null) {
							element.setText(fieldValue.toString());
						} else {
							if (!ignoreNullElement) {
								element.setText("");
							}
						}
					} else if (field.isAnnotationPresent(XmlAttribute.class)) {
						// 标注为Attribute
						XmlAttribute xmlAttrAnnotation = field.getAnnotation(XmlAttribute.class);

						String attributeName = xmlAttrAnnotation.name();
						
						if (DEFAULT_NAME.equals(attributeName)
								|| attributeName.trim().length() == 0) {
							attributeName = fieldName;
						}
						
						if (fieldValue != null) {
							if (DataType.NUMBER.equals(fieldType) || DataType.STRING.equals(fieldType)
									|| DataType.BOOLEAN.equals(fieldType) || DataType.DATE.equals(fieldType)
									|| DataType.ENUM.equals(fieldType)) {
								String attributeValue;
								if (DataType.DATE.equals(fieldType)) {
									attributeValue = DateTimeUtils.formatDateForSiteMap((Date) fieldValue);
								} else {
									attributeValue = fieldValue.toString();
								}
								
								element.addAttribute(attributeName, attributeValue == null ? "" : attributeValue);
							} else {
								throw new XmlException("Attribute type does not support!");
							}
						} else {
							if (!ignoreNullElement) {
								element.addAttribute(attributeName, "");
							}
						}
					} else if (field.isAnnotationPresent(XmlElement.class)) {
						XmlElement xmlElemAnnotation = field.getAnnotation(XmlElement.class);

						String elemName = xmlElemAnnotation.name();
						
						if (DEFAULT_NAME.equals(elemName)
								|| elemName.trim().length() == 0) {
							elemName = field.getName();
						}
						
						if (DataType.OBJECT.equals(fieldType)) {
							Element childElement = element.addElement(elemName);
							Object childObject = ReflectionUtils.getFieldValue(field, object);
							if (childObject == null) {
								if (ignoreNullElement) {
									continue;
								}
							}
							convertToXmlElement(fieldClass, childObject, childElement, ignoreNullElement);
						} else {
							String nodeValue = convertDataToString(fieldType, fieldValue);

							if (nodeValue != null) {
								element.addElement(elemName).setText(nodeValue);
							} else {
								if (!ignoreNullElement) {
									element.addElement(elemName).setText("");
								}
							}
						}
					} else if (field.isAnnotationPresent(XmlElementWrapper.class)) {
						XmlElementWrapper xmlElementWrapper = field.getAnnotation(XmlElementWrapper.class);

						String elemName = xmlElementWrapper.name();
						if (elemName.trim().length() == 0
								|| DEFAULT_NAME.equals(elemName)) {
							elemName = fieldName;
						}

						Class<?> paramClass;
						
						if (field.getType().isArray()) {
							paramClass = field.getType().getComponentType();
						} else {
							paramClass = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
						}
						
						DataType elemType = ObjectUtils.retrieveSimpleDataType(paramClass);

						String childElemName;
						if (DataType.OBJECT.equals(elemType)) {
							childElemName = readChildElemName(paramClass);
						} else {
							childElemName = elemName;
							elemName = fieldName;
						}
						
						if (fieldValue == null) {
							if (!ignoreNullElement) {
								if (field.getType().isArray()) {
									element.addElement(childElemName).setText(Globals.DEFAULT_VALUE_STRING);
								} else {
									element.addElement(elemName).addElement(childElemName).setText(Globals.DEFAULT_VALUE_STRING);
								}
							}
							continue;
						}
						
						Element parentElement = element;
						List<?> arrayList;
						
						if (field.getType().isArray()) {
							arrayList = Arrays.asList((Object[]) fieldValue);
						} else {
							parentElement = element.addElement(elemName);
							arrayList = (List<?>) fieldValue;
						}

						int arrayCount = arrayList.size();
						
						if (arrayCount == 0 && !ignoreNullElement && field.getType().isArray()) {
							parentElement.addElement(childElemName).setText("");
							continue;
						}
						
						for (Object childObj : arrayList) {
							if (DataType.OBJECT.equals(elemType)) {
								Element childElem = parentElement.addElement(childElemName);
								convertToXmlElement(paramClass, childObj, childElem, ignoreNullElement);
							} else {
								String nodeValue = convertDataToString(fieldType, childObj);
								
								if (nodeValue != null) {
									parentElement.addElement(childElemName).setText(nodeValue);
								} else {
									if (!ignoreNullElement) {
										parentElement.addElement(childElemName).setText("");
									}
								}
							}
						}
					}
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Convert Object To XML Error! ", e);
					}
				}
			}
		} else {
			String text = null;
			
			if (object != null) {
				if (DataType.DATE.equals(dataType)) {
					text = DateTimeUtils.formatDateForSiteMap((Date) object);
				} else {
					text = object.toString();
				}
			}
			if (text == null) {
				element.setText(Globals.DEFAULT_VALUE_STRING);
			} else {
				element.setText(text);
			}
		}
	}

	private static String convertDataToString(DataType fieldType, Object object) {
		if (object == null) {
			return null;
		}

		String nodeValue;
		if (DataType.DATE.equals(fieldType)) {
			nodeValue = DateTimeUtils.formatDateForSiteMap((Date) object);
		} else if (DataType.BINARY.equals(fieldType)) {
			nodeValue = StringUtils.base64Encode(processBinaryDatas(object));
		} else {
			nodeValue = StringUtils.formatTextForXML(object.toString());
		}
		return nodeValue;
	}

	private static String readChildElemName(Class<?> paramClass) {
		String childElemName;
		XmlType childElement = paramClass.getAnnotation(XmlType.class);
		childElemName = childElement.name();

		if (childElemName.trim().length() == 0
				|| DEFAULT_NAME.equals(childElemName)) {
			childElemName = paramClass.getSimpleName();
		}
		return childElemName;
	}

	private static byte[] processBinaryDatas(Object object) {
		byte[] binary;
		if (object instanceof byte[]) {
			binary = (byte[])object;
		} else {
			ByteArrayOutputStream byteArrayOutputStream = null;
			ObjectOutputStream objectOutputStream = null;
			try {
				byteArrayOutputStream = new ByteArrayOutputStream();
				objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				objectOutputStream.writeObject(object);
				binary = byteArrayOutputStream.toByteArray();
			} catch (IOException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Encode binary data error! ", e);
				}
				binary = new byte[]{};
			} finally {
				IOUtils.closeStream(objectOutputStream);
				IOUtils.closeStream(byteArrayOutputStream);
			}
		}
		return binary;
	}
	
	private static String convertToXmlString(Document document, String indent, String encoding, boolean expandEmptyElements) throws XmlException {
		if (document == null) {
			return null;
		}
		
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		
		if (encoding == null) {
			outputFormat.setEncoding(Globals.DEFAULT_ENCODING);
		} else {
			outputFormat.setEncoding(encoding);
		}

		outputFormat.setNewLineAfterDeclaration(Globals.DEFAULT_VALUE_BOOLEAN);
		outputFormat.setTrimText(true);
		if (indent == null) {
			outputFormat.setIndent(true);
			outputFormat.setNewlines(Globals.DEFAULT_VALUE_BOOLEAN);
		} else {
			outputFormat.setIndent(indent);
			outputFormat.setNewlines(true);
		}
		
		outputFormat.setExpandEmptyElements(expandEmptyElements);
		
		StringWriter stringWriter = null;
		XMLWriter xmlWriter = null;
		
		try {
			stringWriter = new StringWriter();
			xmlWriter = new XMLWriter(stringWriter, outputFormat);
			
			xmlWriter.write(document);
			
			return stringWriter.toString();
		} catch (IOException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Catch error! ", e);
			}
		} finally {
			try {
				if (xmlWriter != null) {
					xmlWriter.close();
				}
			} catch (IOException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Catch error! ", e);
				}
			}
			IOUtils.closeStream(stringWriter);
		}
		
		return null;
	}
}
