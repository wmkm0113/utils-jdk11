/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 4:26:58 PM $
 */
public final class OfficeUtils {
	
	private transient final static Logger LOGGER = LoggerFactory.getLogger(OfficeUtils.class);
	
	private final static String EXCEL_FILE_EXT_NAME_2003 = "xls";
	private final static String EXCEL_FILE_EXT_NAME_2007 = "xlsx";
	
	private OfficeUtils() {
		
	}
	
	/**
	 * Override data to excel file
	 * @param filePath				excel file path
	 * @param workbookValues		override data
	 * @return						<code>true</code> for process success, <code>false</code> for other
	 */
	public static boolean processExcel(String filePath, Map<String, List<List<Object>>> workbookValues) {
		if (workbookValues == null) {
			return false;
		}
		
		Workbook workbook = openExcel(filePath);
		
		if (workbook == null) {
			return false;
		}
		
		Iterator<Entry<String, List<List<Object>>>> iterator = workbookValues.entrySet().iterator();
		
		while (iterator.hasNext()) {
			Entry<String, List<List<Object>>> entry = iterator.next();
			appendExcel(workbook, entry.getKey(), entry.getValue());
		}
		
		return writeExcel(filePath, workbook);
	}
	
	/**
	 * Count excel rows
	 * @param filePath		excel path
	 * @return				all rows count
	 */
	public static int excelRowsCount(String filePath) {
		int rowCount = 0;
		InputStream inputStream = null;
		Workbook workbook = null;
		
		try {
			inputStream = FileUtils.getURL(filePath).openStream();
			workbook = WorkbookFactory.create(inputStream);
			
			for (int i = 0 ; i < workbook.getNumberOfSheets() ; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				rowCount += sheet.getPhysicalNumberOfRows();
			}
		} catch (Exception e) {
			if (OfficeUtils.LOGGER.isDebugEnabled()) {
				OfficeUtils.LOGGER.debug("Load excel file error! ", e);
			}
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
				
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				
			}
		}
		
		return rowCount;
	}
	
	/**
	 * Count sheet rows 
	 * @param filePath		excel file path
	 * @param sheetName		sheet name
	 * @return
	 */
	public static int excelRowsCount(String filePath, String sheetName) {
		InputStream inputStream = null;
		Workbook workbook = null;
		
		try {
			inputStream = FileUtils.loadFile(filePath);
			workbook = WorkbookFactory.create(inputStream);

			Sheet sheet = workbook.getSheet(sheetName);
			return sheet.getPhysicalNumberOfRows();
		} catch (Exception e) {
			if (OfficeUtils.LOGGER.isDebugEnabled()) {
				OfficeUtils.LOGGER.debug("Load excel file error! ", e);
			}
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
				
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				
			}
		}
		return Globals.INITIALIZE_INT_VALUE;
	}
	
	/**
	 * Check sheet exists
	 * @param filePath		excel file path
	 * @param sheetList		check sheet name list
	 * @return
	 */
	public static boolean checkSheetExists(String filePath, String... sheetList) {
		if (FileUtils.isExists(filePath)) {
			InputStream inputStream = null;
			Workbook workbook = null;
			
			try {
				inputStream = FileUtils.loadFile(filePath);
				workbook = WorkbookFactory.create(inputStream);
				
				for (String sheetName : sheetList) {
					if (workbook.getSheet(sheetName) == null) {
						return Globals.DEFAULT_VALUE_BOOLEAN;
					}
				}
			} catch (Exception e) {
				if (OfficeUtils.LOGGER.isDebugEnabled()) {
					OfficeUtils.LOGGER.debug("Load excel file error! ", e);
				}
			} finally {
				try {
					if (workbook != null) {
						workbook.close();
					}
					
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException ex) {
					
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Read excel datas to map
	 * @param filePath		excel file path
	 * @return				data map(key for sheet name, values for sheet datas)
	 */
	public static Map<String, List<List<String>>> readExcel(String filePath) {
		String fileExtName = StringUtils.getFilenameExtension(filePath);
		
		if (EXCEL_FILE_EXT_NAME_2007.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithEventModel(filePath, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
		} else if (EXCEL_FILE_EXT_NAME_2003.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithUserModel(filePath, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * Read excel datas to map
	 * @param filePath		excel file path
	 * @param beginRow
	 * @param endRow
	 * @return				data map(key for sheet name, values for sheet datas)
	 */
	public static Map<String, List<List<String>>> readExcel(String filePath, int beginRow, int endRow) {
		String fileExtName = StringUtils.getFilenameExtension(filePath);
		
		if (EXCEL_FILE_EXT_NAME_2007.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithEventModel(filePath, beginRow, endRow);
		} else if (EXCEL_FILE_EXT_NAME_2003.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithUserModel(filePath, beginRow, endRow);
		} else {
			return null;
		}
	}
	
	/**
	 * Read sheet datas to list
	 * @param filePath		excel file path
	 * @param sheetName		read sheet name
	 * @return				sheet datas
	 */
	public static List<List<String>> readExcel(String filePath, String sheetName) {
		String fileExtName = StringUtils.getFilenameExtension(filePath);
		
		if (EXCEL_FILE_EXT_NAME_2007.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithEventModel(filePath, sheetName, 
					Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
		} else if (EXCEL_FILE_EXT_NAME_2003.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithUserModel(filePath, sheetName, 
					Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
		} else {
			return null;
		}
	}
	
	/**
	 * Read sheet datas from begin to end to list
	 * @param filePath		excel file path
	 * @param sheetName		read sheet name
	 * @param beginRow
	 * @param endRow
	 * @return				sheet datas
	 */
	public static List<List<String>> readExcel(String filePath, String sheetName, int beginRow, int endRow) {
		String fileExtName = StringUtils.getFilenameExtension(filePath);
		
		if (EXCEL_FILE_EXT_NAME_2007.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithEventModel(filePath, sheetName, beginRow, endRow);
		} else if (EXCEL_FILE_EXT_NAME_2003.equalsIgnoreCase(fileExtName)) {
			return OfficeUtils.readExcelWithUserModel(filePath, sheetName, beginRow, endRow);
		} else {
			return null;
		}
	}
	
	/**
	 * Read excel and create workbook
	 * @param filePath		excel file path
	 * @return
	 */
	public static Workbook openExcel(String filePath) {
		if (!FileUtils.isExists(filePath)) {
			return null;
		}
		
		String fileExtName = StringUtils.getFilenameExtension(filePath);
		
		if (fileExtName == null) {
			return null;
		}
		
		InputStream inputStream = null;
		Workbook workbook = null;
		
		try {
			if (FileUtils.isExists(filePath)) {
				inputStream = FileUtils.getURL(filePath).openStream();
			}
			
			if (EXCEL_FILE_EXT_NAME_2003.equals(fileExtName)) {
				if (inputStream != null) {
					workbook = new HSSFWorkbook(inputStream);
				}
			} else if (EXCEL_FILE_EXT_NAME_2007.equals(fileExtName)) {
				if (inputStream != null) {
					workbook = new XSSFWorkbook(inputStream);
				}
			}
		} catch (Exception e) {
			if (OfficeUtils.LOGGER.isDebugEnabled()) {
				OfficeUtils.LOGGER.debug("Load excel file error! ", e);
			}
			workbook = null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Close input stream error! ", e);
					}
				}
			}
		}
		
		return workbook;
	}
	
	/**
	 * Create new workbook
	 * @param filePath
	 * @return
	 */
	public static Workbook createWorkbook(String filePath) {
		String fileExtName = StringUtils.getFilenameExtension(filePath);
		
		if (fileExtName == null) {
			return null;
		}

		if (EXCEL_FILE_EXT_NAME_2003.equals(fileExtName)) {
			return new HSSFWorkbook();
		} else if (EXCEL_FILE_EXT_NAME_2007.equals(fileExtName)) {
			return new SXSSFWorkbook(100);
		} else {
			return null;
		}
	}
	
	/**
	 * Write workbook to local disk
	 * @param filePath
	 * @param workbook
	 * @return
	 */
	public static boolean writeExcel(String filePath, Workbook workbook) {
		FileUtils.makeHome(filePath.substring(0, filePath.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR)));
		
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(new File(filePath));
			
			workbook.write(fileOutputStream);
			return true;
		} catch (Exception e) {
			if (OfficeUtils.LOGGER.isDebugEnabled()) {
				OfficeUtils.LOGGER.debug("Write excel file error! ", e);
			}
			return false;
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e) {
					if (OfficeUtils.LOGGER.isDebugEnabled()) {
						OfficeUtils.LOGGER.debug("Close output stream error! ", e);
					}
				}
			}
		}
	}
	
	/**
	 * Append data to excel workbook
	 * @param workbook
	 * @param sheetName
	 * @param sheetValues
	 */
	public static void appendExcel(Workbook workbook, String sheetName, List<List<Object>> sheetValues) {
		if (workbook == null || sheetName == null || sheetValues == null || sheetValues.isEmpty()) {
			return;
		}
		
		Sheet sheet = null;
		if (workbook.getSheet(sheetName) != null) {
			sheet = workbook.getSheet(sheetName);
		} else {
			sheet = workbook.createSheet(sheetName);
		}
		
		for (int j = 0 ; j < sheetValues.size() ; j++) {
			Row row = sheet.createRow(j);
			List<Object> rowValues = sheetValues.get(j);
			
			for (int k = 0 ; k < rowValues.size() ; k++) {
				Cell cell = row.createCell(k);
				Object object = rowValues.get(k);
				if (object != null) {
					if (object instanceof Date) {
						Date dateObj = (Date)object;
						cell.setCellValue(DateTimeUtils.formatDate(dateObj));
					} else {
						cell.setCellValue(object.toString());
					}
				} else {
					cell.setCellValue("");
				}
			}
		}
	}
	
	private static List<String> processRowData(Row row) {
		if (row == null) {
			return null;
		}
		
		List<String> rowList = new ArrayList<String>();
		
		for (int k = 0 ; k < row.getPhysicalNumberOfCells() ; k++) {
			Cell cell = row.getCell(k);
			if (cell == null) {
				continue;
			}
			
			CellType cellType = cell.getCellTypeEnum();
			Object value = null;
			switch (cellType) {
			case NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					value = DateTimeUtils.formatDateTime(cell.getDateCellValue());
				} else {
					double doubleValue = cell.getNumericCellValue();
					long longValue = Math.round(doubleValue);
					if (Double.parseDouble(longValue + ".0") == doubleValue) {
						value = longValue;
					} else {
						value = doubleValue;
					}
				}
				break;
			case BOOLEAN:
				value = new Boolean(cell.getBooleanCellValue());
				break;
			case FORMULA:
				try {
					double doubleValue = cell.getNumericCellValue();
					long longValue = Math.round(doubleValue);
					if (Double.parseDouble(longValue + ".0") == doubleValue) {
						value = longValue;
					} else {
						value = doubleValue;
					}
				} catch (IllegalStateException e) {
					value = cell.getRichStringCellValue();
				}
				break;
			case STRING:
				value = cell.getStringCellValue();
				break;
			case ERROR:
				value = "";
				break;
				default:
					value = cell.getStringCellValue();
			}
			rowList.add(value.toString());
		}
		
		return rowList;
	}
	
	private static Map<String, List<List<String>>> readExcelWithEventModel(String filePath, int beginRow, int endRow) {
		Map<String, List<List<String>>> returnList = new HashMap<String, List<List<String>>>();
		if (FileUtils.isExists(filePath)) {
			InputStream inputStream = null;

			OPCPackage opcPackage = null;
			try {
				opcPackage = OPCPackage.open(filePath, PackageAccess.READ);
				XSSFReader xssfReader = new XSSFReader(opcPackage);
				XMLReader xmlReader = XMLReaderFactory.createXMLReader();
				NervousyncSheetHandler sheetHandler = 
						new NervousyncSheetHandler(xssfReader.getStylesTable(), 
								xssfReader.getSharedStringsTable(), beginRow, endRow);
				xmlReader.setContentHandler(sheetHandler);
				
				Iterator<InputStream> iterator = xssfReader.getSheetsData();
				
				int sheetIndex = 0;
				while (iterator.hasNext()) {
					String sheetName = OfficeUtils.searchSheetName(filePath, sheetIndex);
					
					if (sheetName != null) {
						inputStream = iterator.next();
						
						xmlReader.parse(new InputSource(inputStream));
						returnList.put(sheetName, sheetHandler.getDataList());
						inputStream.close();
					}
					
					sheetIndex++;
					sheetHandler.resetDataList();
				}
			} catch (Exception e) {
				if (OfficeUtils.LOGGER.isDebugEnabled()) {
					OfficeUtils.LOGGER.debug("Load excel file error! ", e);
				}
				return returnList;
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException ex) {
					
				}
			}
		}
		return returnList;
	}

	private static Map<String, List<List<String>>> readExcelWithUserModel(String filePath, int beginRow, int endRow) {
		Map<String, List<List<String>>> returnList = new HashMap<String, List<List<String>>>();
		InputStream inputStream = null;
		Workbook workbook = null;
		
		try {
			inputStream = FileUtils.getURL(filePath).openStream();
			workbook = WorkbookFactory.create(inputStream);
			
			for (int i = 0 ; i < workbook.getNumberOfSheets() ; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				
				List<List<String>> sheetList = new ArrayList<List<String>>();
				if (beginRow == Globals.DEFAULT_VALUE_INT) {
					beginRow = Globals.INITIALIZE_INT_VALUE;
				}
				
				if (endRow == Globals.DEFAULT_VALUE_INT 
						|| endRow > sheet.getPhysicalNumberOfRows()) {
					endRow = sheet.getPhysicalNumberOfRows();
				}
				
				for (int j = beginRow ; j < endRow ; j++) {
					List<String> rowList = processRowData(sheet.getRow(j));

					if (rowList == null) {
						continue;
					}
					
					sheetList.add(rowList);
				}
				returnList.put(sheet.getSheetName(), sheetList);
			}
		} catch (Exception e) {
			if (OfficeUtils.LOGGER.isDebugEnabled()) {
				OfficeUtils.LOGGER.debug("Load excel file error! ", e);
			}
			return returnList;
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
				
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				
			}
		}
		
		return returnList;
	}

	private static List<List<String>> readExcelWithEventModel(String filePath, String sheetName, int beginRow, int endRow) {
		List<List<String>> dataList = null;
		
		if (FileUtils.isExists(filePath)) {
			InputStream inputStream = null;
			OPCPackage opcPackage = null;
			try {
				opcPackage = OPCPackage.open(filePath, PackageAccess.READ);
				XSSFReader xssfReader = new XSSFReader(opcPackage);
				XMLReader xmlReader = XMLReaderFactory.createXMLReader();
				NervousyncSheetHandler sheetHandler = 
						new NervousyncSheetHandler(xssfReader.getStylesTable(), 
								xssfReader.getSharedStringsTable(), beginRow, endRow);
				xmlReader.setContentHandler(sheetHandler);
				
				int sheetIndex = OfficeUtils.searchRelationId(filePath, sheetName);
				
				inputStream = xssfReader.getSheet("rId" + sheetIndex);
				xmlReader.parse(new InputSource(inputStream));
				
				dataList = sheetHandler.getDataList();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (opcPackage != null) {
						opcPackage.close();
					}
					
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Exception e) {
					
				}
			}
		}
		
		return dataList;
	}
	
	private static List<List<String>> readExcelWithUserModel(String filePath, String sheetName, int beginRow, int endRow) {
		List<List<String>> dataList = new ArrayList<List<String>>();
		InputStream inputStream = null;
		Workbook workbook = null;
		
		try {
			inputStream = FileUtils.getURL(filePath).openStream();
			workbook = WorkbookFactory.create(inputStream);
			
			Sheet sheet = workbook.getSheet(sheetName);
			
			if (endRow == Globals.DEFAULT_VALUE_INT || sheet.getPhysicalNumberOfRows() < endRow) {
				endRow = sheet.getPhysicalNumberOfRows();
			}
			
			if (beginRow == Globals.DEFAULT_VALUE_INT) {
				beginRow = Globals.INITIALIZE_INT_VALUE;
			}
			
			if (sheet.getPhysicalNumberOfRows() > beginRow) {
				for (int i = beginRow ; i < endRow ; i++) {
					List<String> rowList = processRowData(sheet.getRow(i));

					if (rowList == null) {
						continue;
					}

					dataList.add(rowList);
				}
			}
		} catch (Exception e) {
			if (OfficeUtils.LOGGER.isDebugEnabled()) {
				OfficeUtils.LOGGER.debug("Load excel file error! ", e);
			}
			return dataList;
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
				
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				
			}
		}
		
		return dataList;
	}
	
	private static class NervousyncSheetHandler extends DefaultHandler {

		private StylesTable stylesTable;
		private SharedStringsTable sharedStringsTable;
		private String lastContents;
		private CellDataType dataType;
		private List<List<String>> dataList;
		private List<String> rowDatas = new ArrayList<String>();
		private short formatIndex = Globals.DEFAULT_VALUE_SHORT;
		private String formatInfo = Globals.DEFAULT_VALUE_STRING;
		private DataFormatter dataFormatter = null;
		private int beginRow;
		private int endRow;
		private int currentCol = Globals.INITIALIZE_INT_VALUE;
		private int currentRow = Globals.INITIALIZE_INT_VALUE;
		
		private NervousyncSheetHandler(StylesTable stylesTable, 
				SharedStringsTable sharedStringsTable, int beginRow, int endRow) {
			this.stylesTable = stylesTable;
			this.sharedStringsTable = sharedStringsTable;
			this.beginRow = beginRow;
			this.endRow = endRow;
			this.dataFormatter = new DataFormatter();
			this.resetDataList();
		}
		
		public void resetDataList() {
			this.dataList = new ArrayList<List<String>>();
			this.currentCol = Globals.INITIALIZE_INT_VALUE;
			this.currentRow = Globals.INITIALIZE_INT_VALUE;
		}
		
		/**
		 * @return the dataList
		 */
		public List<List<String>> getDataList() {
			return dataList;
		}

		public void startElement(String uri, String localName, String name, Attributes attributes) {
			if ((this.beginRow != Globals.DEFAULT_VALUE_INT && this.currentRow < this.beginRow) 
					|| (this.endRow != Globals.DEFAULT_VALUE_INT && this.currentRow >= this.endRow)) {
				return;
			}
			
			if (name.equals("c")) {
				this.parseCellDataType(attributes);
				this.currentCol++;
			}
			this.lastContents = "";
		}
		
		public void endElement(String uri, String localName, String name) {
			if ((this.beginRow != Globals.DEFAULT_VALUE_INT && this.currentRow < this.beginRow) 
					|| (this.endRow != Globals.DEFAULT_VALUE_INT && this.currentRow >= this.endRow)) {
				if (name.equals("row")) {
					this.currentRow++;
					this.currentCol = Globals.INITIALIZE_INT_VALUE;
				}
				return;
			}
			
			/*
			if (this.nextIsString) {
				int index = Integer.parseInt(this.lastContents);
				this.lastContents = new XSSFRichTextString(this.sharedStringsTable.getEntryAt(index)).toString();
				this.nextIsString = false;
			}*/
			
			if (name.equals("v")) {
				XSSFRichTextString xssfRichTextString = null;
				
				switch (this.dataType) {
				case BOOLEAN:
					this.lastContents = (this.lastContents.charAt(0) == '0') ? "false" : "true";
					break;
				case ERROR:
					this.lastContents = "\"Error: " + this.lastContents + "\"";
					break;
				case INLINE_STRING:
					xssfRichTextString = new XSSFRichTextString(this.lastContents);
					this.lastContents = xssfRichTextString.toString();
					break;
				case SSTINDEX:
					try {
						int index = Integer.parseInt(this.lastContents);
						xssfRichTextString = new XSSFRichTextString(this.sharedStringsTable.getEntryAt(index));
						this.lastContents = xssfRichTextString.toString();
					} catch (NumberFormatException e) {
						
					}
					break;
				case NUMBER:
					if (this.formatInfo != null) {
						this.lastContents = 
								this.dataFormatter.formatRawCellContents(Double.parseDouble(this.lastContents), 
										this.formatIndex, this.formatInfo);
					}
					break;
					default:
						break;
				}
				xssfRichTextString = null;
				
				this.rowDatas.add(this.lastContents);
			} else if (name.equals("row")) {
				this.dataList.add(this.rowDatas);
				this.rowDatas = new ArrayList<String>();
				this.currentRow++;
				this.currentCol = Globals.INITIALIZE_INT_VALUE;
			}
			
			if (this.rowDatas.size() != this.currentCol) {
				this.rowDatas.add("");
			}
		}
		
		public void characters(char[] ch, int start, int length) {
			if ((this.beginRow != Globals.DEFAULT_VALUE_INT && this.currentRow < this.beginRow) 
					|| (this.endRow != Globals.DEFAULT_VALUE_INT && this.currentRow >= this.endRow)) {
				return;
			}
			this.lastContents += new String(ch, start, length);
		}
		
		private void parseCellDataType(Attributes attributes) {
			String cellType = attributes.getValue("t");
			if ("b".equals(cellType)) {
				this.dataType = CellDataType.BOOLEAN;
			} else if ("e".equals(cellType)) {
				this.dataType = CellDataType.ERROR;
			} else if ("inlineStr".equals(cellType)) {
				this.dataType = CellDataType.INLINE_STRING;
			} else if ("s".equals(cellType)) {
				this.dataType = CellDataType.SSTINDEX;
			} else if ("str".equals(cellType)) {
				this.dataType = CellDataType.FORMULA;
			} else {
				this.dataType = CellDataType.NUMBER;
				String cellStyle = attributes.getValue("s");
				
				if (cellStyle != null) {
					int styleIndex = Integer.parseInt(cellStyle);
					XSSFCellStyle xssfCellStyle = this.stylesTable.getStyleAt(styleIndex);
					this.formatIndex = xssfCellStyle.getDataFormat();
					this.formatInfo = xssfCellStyle.getDataFormatString();
					
					if (this.formatInfo == null) {
						this.formatInfo = BuiltinFormats.getBuiltinFormat(this.formatIndex);
					}
				}
			}
		}
		
		private enum CellDataType {
			BOOLEAN, ERROR, INLINE_STRING, SSTINDEX, FORMULA, NUMBER
		}
	}
	
	private static String searchSheetName(String filePath, int sheetIndex) {
		InputStream inputStream = null;
		Workbook workbook = null;
		try {
			inputStream = FileUtils.loadFile(filePath);
			workbook = WorkbookFactory.create(inputStream);
			return workbook.getSheetAt(sheetIndex).getSheetName();
		} catch (Exception e) {
			
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
				
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				
			}
		}
		
		return null;
	}
	
	private static int searchRelationId(String filePath, String sheetName) {
		InputStream inputStream = null;
		Workbook workbook = null;
		int index = Globals.DEFAULT_VALUE_INT;
		try {
			inputStream = FileUtils.loadFile(filePath);
			workbook = WorkbookFactory.create(inputStream);
			
			index = workbook.getSheetIndex(sheetName) + 1;
		} catch (Exception e) {
			index = Globals.DEFAULT_VALUE_INT;
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
				
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				
			}
		}
		
		return index;
	}
}
