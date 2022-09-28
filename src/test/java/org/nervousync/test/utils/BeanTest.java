package org.nervousync.test.utils;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.annotations.beans.BeanMapping;
import org.nervousync.annotations.beans.FieldMapping;
import org.nervousync.beans.converter.impl.basic.NumberDataConverter;
import org.nervousync.beans.converter.impl.blob.Base64DataConverter;
import org.nervousync.beans.converter.impl.json.JsonDataConverter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.BeanUtils;
import org.nervousync.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class BeanTest extends BaseTest {

	private static final OrigBeanOne BEAN_ONE;
	private static final OrigBeanTwo BEAN_TWO;

	static {
		BEAN_ONE = new OrigBeanOne();
		BEAN_ONE.setBinaryField("Test string".getBytes());
		BEAN_ONE.setStringField("One string");
		BEAN_ONE.setIntegerField(2);
		OrigBeanObject origBeanObject = new OrigBeanObject();
		origBeanObject.setBeanBoolean(Boolean.TRUE);
		origBeanObject.setBeanString("Bean string");
		BEAN_TWO = new OrigBeanTwo();
		BEAN_TWO.setBeanObject(origBeanObject);
	}

	@Test
	public void test000BeanCopyTest() {
		DestBean destBean = new DestBean();
		BeanUtils.copyProperties(destBean, BEAN_ONE, BEAN_TWO);
		this.logger.info("Copied result: {}",
				StringUtils.objectToString(destBean, StringUtils.StringType.JSON, Boolean.TRUE));
	}

	@Test
	public void test010BeanConverterCopyTest() {
		Map<String, String> converterMap = new HashMap<>();
		converterMap.put("stringField", "destStringField");
		converterMap.put("integerField", "destIntField");
		converterMap.put("binaryField", "destBinaryField");
		converterMap.put("beanObject", "destBeanObject");
		DestBean destBean = new DestBean();
		BeanUtils.copyProperties(destBean, converterMap, BEAN_ONE, BEAN_TWO);
		this.logger.info("Copied result: {}",
				StringUtils.objectToString(destBean, StringUtils.StringType.JSON, Boolean.TRUE));
	}

	@Test
	public void test020MapCopyTest() {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("destStringField", "One map string");
		dataMap.put("destIntField", "3.3");
		dataMap.put("destBinaryField", StringUtils.base64Encode("Test map string".getBytes()));
		dataMap.put("destBeanObject", "{\"beanString\":\"Bean string\",\"beanBoolean\":true}");
		DestBean destBean = new DestBean();
		BeanUtils.copyProperties(dataMap, destBean);
		this.logger.info("Copied result: {}",
				StringUtils.objectToString(destBean, StringUtils.StringType.JSON, Boolean.TRUE));
	}

	@Test
	public void test030RemoveConfig() {
		BeanUtils.removeBeanConfig(OrigBeanOne.class, OrigBeanTwo.class, DestBean.class);
	}

	public static final class OrigBeanOne {

		private String stringField;
		@FieldMapping(NumberDataConverter.class)
		private Integer integerField;
		@FieldMapping(Base64DataConverter.class)
		private byte[] binaryField;

		public OrigBeanOne() {
		}

		public String getStringField() {
			return stringField;
		}

		public void setStringField(String stringField) {
			this.stringField = stringField;
		}

		public Integer getIntegerField() {
			return integerField;
		}

		public void setIntegerField(Integer integerField) {
			this.integerField = integerField;
		}

		public byte[] getBinaryField() {
			return binaryField;
		}

		public void setBinaryField(byte[] binaryField) {
			this.binaryField = binaryField;
		}
	}

	public static final class OrigBeanTwo {

		@FieldMapping(JsonDataConverter.class)
		private OrigBeanObject beanObject;

		public OrigBeanTwo() {
		}

		public OrigBeanObject getBeanObject() {
			return beanObject;
		}

		public void setBeanObject(OrigBeanObject beanObject) {
			this.beanObject = beanObject;
		}
	}

	public static final class OrigBeanObject extends BeanObject {

		private String beanString;
		private boolean beanBoolean;

		public OrigBeanObject() {
		}

		public String getBeanString() {
			return beanString;
		}

		public void setBeanString(String beanString) {
			this.beanString = beanString;
		}

		public boolean isBeanBoolean() {
			return beanBoolean;
		}

		public void setBeanBoolean(boolean beanBoolean) {
			this.beanBoolean = beanBoolean;
		}
	}

	public static final class DestBean {

		@BeanMapping(beanClass = OrigBeanOne.class, fieldName = "stringField")
		private String destStringField;
		@FieldMapping(NumberDataConverter.class)
		@BeanMapping(beanClass = OrigBeanOne.class, fieldName = "integerField")
		private Integer destIntField;
		@FieldMapping(Base64DataConverter.class)
		@BeanMapping(beanClass = OrigBeanOne.class, fieldName = "binaryField")
		private byte[] destBinaryField;
		@FieldMapping(JsonDataConverter.class)
		@BeanMapping(beanClass = OrigBeanTwo.class, fieldName = "beanObject")
		private OrigBeanObject destBeanObject;

		public DestBean() {
		}

		public String getDestStringField() {
			return destStringField;
		}

		public void setDestStringField(String destStringField) {
			this.destStringField = destStringField;
		}

		public Integer getDestIntField() {
			return destIntField;
		}

		public void setDestIntField(Integer destIntField) {
			this.destIntField = destIntField;
		}

		public byte[] getDestBinaryField() {
			return destBinaryField;
		}

		public void setDestBinaryField(byte[] destBinaryField) {
			this.destBinaryField = destBinaryField;
		}

		public OrigBeanObject getDestBeanObject() {
			return destBeanObject;
		}

		public void setDestBeanObject(OrigBeanObject destBeanObject) {
			this.destBeanObject = destBeanObject;
		}
	}
}
