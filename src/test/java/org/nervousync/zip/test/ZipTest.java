package org.nervousync.zip.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.zip.ZipOptions;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;
import org.nervousync.zip.ZipFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ZipTest extends BaseTest {

	private static final String BASE_PATH;
	private static String COMPRESS_FOLDER;
	private static String EXTRACT_FOLDER;
	private static String ZIP_FILE_PATH;
	private static final String ZIP_PASSWORD = "Nervousync";

	static {
		BASE_PATH = System.getProperty("java.io.tmpdir");
	}

	@BeforeClass
	public static void createTmpFiles() {
		COMPRESS_FOLDER = BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "TestZip";
		FileUtils.makeDir(COMPRESS_FOLDER);
		EXTRACT_FOLDER = BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "TestUnzip";
		FileUtils.makeDir(EXTRACT_FOLDER);
		ZIP_FILE_PATH = BASE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "ZipFiles";
		FileUtils.makeDir(ZIP_FILE_PATH);

		Random random = new Random();
		for (int i = 0 ; i < 5 ; i++) {
			int fileSize = random.nextInt(1048576);
			if (i % 2 == 1) {
				fileSize += 1048576;
			}

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			for (int j = 0 ; j < fileSize ; j++) {
				byteArrayOutputStream.write(random.nextInt(256));
			}
			String randFilePath;
			if (i == 0) {
				randFilePath = COMPRESS_FOLDER + Globals.DEFAULT_PAGE_SEPARATOR + "中文路径";
			} else {
				randFilePath = COMPRESS_FOLDER + Globals.DEFAULT_PAGE_SEPARATOR + StringUtils.randomString(8);
			}
			FileUtils.saveFile(byteArrayOutputStream.toByteArray(), randFilePath);
		}
	}

	@AfterClass
	public static void clearTmpFiles() {
		FileUtils.removeDir(COMPRESS_FOLDER);
		FileUtils.removeDir(EXTRACT_FOLDER);
		FileUtils.removeDir(ZIP_FILE_PATH);
	}

	@Test
	public void test000CreateZipFromList() throws ZipException, FileNotFoundException {
		ZipFile.createZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoFile.zip",
				ZipOptions.newOptions(), FileUtils.listFiles(COMPRESS_FOLDER).toArray(new String[0]));
		this.logger.info("Create Zip File Success! ");
	}

	@Test
	public void test005CreateZipFromFolder() throws ZipException {
		ZipFile.createZipFileFromFolder(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoFolder.zip",
				ZipOptions.newOptions(), COMPRESS_FOLDER);
		this.logger.info("Create Zip File Success! ");
	}

	@Test
	public void test010CreateEncFile() {
		ZipFile.createZipFileFromFolder(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoEnc.zip",
				ZipOptions.standardEncryptOptions(ZIP_PASSWORD), COMPRESS_FOLDER);
		this.logger.info("Create Zip File Success! ");
	}

	@Test
	public void test020CreateAes256File() {
		ZipFile.createZipFileFromFolder(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoAes256.zip",
				ZipOptions.aesEncryptOptions(ZIP_PASSWORD, 256), COMPRESS_FOLDER);
		this.logger.info("Create Zip File Success! ");
	}

	@Test
	public void test030CreateSplitFile() {
		ZipFile.createZipFileFromFolder(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoSplit.zip",
				ZipOptions.newOptions(), Boolean.TRUE, 1024 * 1024L, COMPRESS_FOLDER);
		this.logger.info("Create Zip File Success! ");
	}

	@Test
	public void test040CreateCommentFile() {
		ZipOptions zipOptions = ZipOptions.newOptions();
		//  Setting character encoding "GBK" if comment content contains CJK character for compatible the compress software (ex: 7-Zip)
		zipOptions.setCharsetEncoding("GBK");
		ZipFile.createZipFileFromFolder(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoComment.zip",
						zipOptions, Boolean.FALSE, Globals.DEFAULT_VALUE_LONG, COMPRESS_FOLDER)
				.setComment("Comment 中文测试");
		this.logger.info("Create Comment Zip File Success! ");
	}

	@Test
	public void test045ZipEntryTest() {
		ZipFile zipFile = ZipFile.openZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoFile.zip");
		List<String> entryList = zipFile.entryList();
		for (int i = 0 ; i < entryList.size() ; i++) {
			String entryPath = entryList.get(i);
			this.logger.info("Entry path: {}", entryPath);
			if (i % 2 == 0) {
				byte[] dataBytes = zipFile.readEntry(entryPath);
				this.logger.info("Entry size: {}", dataBytes.length);
			}
		}
	}

	@Test
	public void test050ExtractFile() {
		ZipFile.openZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoFolder.zip")
				.extractAll(EXTRACT_FOLDER + Globals.DEFAULT_PAGE_SEPARATOR + "demo");
	}

	@Test
	public void test060ExtractEncFile() {
		ZipFile.openZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoEnc.zip")
				.setPassword(ZIP_PASSWORD)
				.extractAll(EXTRACT_FOLDER + Globals.DEFAULT_PAGE_SEPARATOR + "demoEnc");
	}

	@Test
	public void test070ExtractAes256File() {
		ZipFile.openZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoAes256.zip")
				.setPassword(ZIP_PASSWORD)
				.extractAll(EXTRACT_FOLDER + Globals.DEFAULT_PAGE_SEPARATOR + "demoAes256");
	}

	@Test
	public void test080ExtractSplitFile() {
		ZipFile.openZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoSplit.zip")
				.extractAll(EXTRACT_FOLDER + Globals.DEFAULT_PAGE_SEPARATOR + "demoSplit");
	}

	@Test
	public void test090MergeSplitFile() {
		ZipFile.openZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoSplit.zip")
				.mergeSplitFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoMerge.zip");
	}

	@Test
	public void test100ReadComment() {
		this.logger.info("Read comment: {} ",
				ZipFile.openZipFile(ZIP_FILE_PATH + Globals.DEFAULT_PAGE_SEPARATOR + "demoComment.zip").getComment("GBK"));
	}

	/**
	 * Test for support the split zip file which created by 7-zip
	 */
	@Test
	public void test110ExtractSoftwareZippedFile() {
		Optional.ofNullable(ZipTest.class.getResource("/"))
				.ifPresent(url -> {
					String basePath = StringUtils.replace(url.getPath(), "/", Globals.DEFAULT_PAGE_SEPARATOR);
					basePath = basePath.substring(1, basePath.length() - 1);
					ZipFile.openZipFile(basePath + Globals.DEFAULT_PAGE_SEPARATOR + "TestZip.zip.001")
							.extractAll(EXTRACT_FOLDER + Globals.DEFAULT_PAGE_SEPARATOR + "TestZip");
				});
	}
}
