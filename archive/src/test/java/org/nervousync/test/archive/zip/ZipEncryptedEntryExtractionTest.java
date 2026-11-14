package org.nervousync.test.archive.zip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.nervousync.archive.ArchiveEntry;
import org.nervousync.archive.ArchiveFile;
import org.nervousync.archive.core.ResourceReader;
import org.nervousync.archive.data.LocalDataFile;
import org.nervousync.archive.exceptions.ArchiveException;
import org.nervousync.archive.options.ArchiveOptions;
import org.nervousync.archive.security.password.PasswordProvider;
import org.nervousync.archive.zip.ZipOptions;
import org.nervousync.zip.ZipFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ZipEncryptedEntryExtractionTest {

	private static final String PASSWORD = "correct-password";
	private static final long STANDARD_WRONG_PASSWORD = 0x0000001A002AL;
	private static final long AES_WRONG_PASSWORD = 0x0000001B000DL;
	private static final long AES_AUTHENTICATION_FAILED = 0x0000001B0019L;

	@TempDir
	Path tempDirectory;

	static Stream<Arguments> encryptedModes() throws Exception {
		return Stream.of(
				Arguments.of("standard",
						org.nervousync.zip.options.ZipOptions.standardEncryptOptions(PASSWORD),
						STANDARD_WRONG_PASSWORD),
				Arguments.of("aes256",
						org.nervousync.zip.options.ZipOptions.aesEncryptOptions(PASSWORD),
						AES_WRONG_PASSWORD));
	}

	@ParameterizedTest(name = "{0} encrypted entry is read through new ArchiveFile API")
	@MethodSource("encryptedModes")
	void readsEncryptedEntryThroughNewApi(final String mode,
	                                      final org.nervousync.zip.options.ZipOptions writerOptions,
	                                      final long ignoredErrorCode) throws Exception {
		byte[] expected = ("encrypted-" + mode).repeat(32).getBytes(StandardCharsets.UTF_8);
		Path archive = createEncryptedArchive(mode, expected, writerOptions);

		try (ArchiveFile archiveFile = open(archive, PASSWORD)) {
			ArchiveEntry entry = archiveFile.entry(mode + ".txt").orElseThrow();
			try (ResourceReader reader = archiveFile.openReader(entry).orElseThrow()) {
				assertArrayEquals(expected, reader.openStream().readAllBytes());
			}
		}
	}

	@ParameterizedTest(name = "{0} rejects wrong password with exact error code")
	@MethodSource("encryptedModes")
	void rejectsWrongPassword(final String mode,
	                          final org.nervousync.zip.options.ZipOptions writerOptions,
	                          final long expectedErrorCode) throws Exception {
		Path archive = createEncryptedArchive(mode, "secret".getBytes(StandardCharsets.UTF_8), writerOptions);

		try (ArchiveFile archiveFile = open(archive, "wrong-password")) {
			ArchiveEntry entry = archiveFile.entry(mode + ".txt").orElseThrow();
			ArchiveException exception = assertThrows(ArchiveException.class, () -> {
				try (ResourceReader reader = archiveFile.openReader(entry).orElseThrow()) {
					reader.openStream().readAllBytes();
				}
			});
			assertEquals(expectedErrorCode, exception.getErrorCode());
		}
	}

	@Test
	void rejectsTamperedAesAuthenticationCode() throws Exception {
		Path archive = createEncryptedArchive("aes-auth", "authenticated".repeat(32).getBytes(StandardCharsets.UTF_8),
				org.nervousync.zip.options.ZipOptions.aesEncryptOptions(PASSWORD));
		tamperLastByteOfFirstEntryPayload(archive);

		try (ArchiveFile archiveFile = open(archive, PASSWORD)) {
			ArchiveEntry entry = archiveFile.entry("aes-auth.txt").orElseThrow();
			ArchiveException exception = assertThrows(ArchiveException.class, () -> {
				try (ResourceReader reader = archiveFile.openReader(entry).orElseThrow()) {
					reader.openStream().readAllBytes();
				}
			});
			assertEquals(AES_AUTHENTICATION_FAILED, exception.getErrorCode());
		}
	}

	private Path createEncryptedArchive(final String name, final byte[] content,
	                                    final org.nervousync.zip.options.ZipOptions writerOptions) throws Exception {
		Path source = Files.write(tempDirectory.resolve(name + ".txt"), content);
		Path archive = tempDirectory.resolve(name + ".zip");
		ZipFile.createZipFile(archive.toString(), writerOptions, source.toString());
		return archive;
	}

	private static ArchiveFile open(final Path archive, final String password) throws Exception {
		ArchiveOptions options = ArchiveOptions.newBuilder()
				.passwordProvider(PasswordProvider.fixed(password.toCharArray()))
				.build();
		return ArchiveFile.zipFile(new LocalDataFile(archive.toString(), "r"), options,
				ZipOptions.newBuilder().build());
	}

	private static void tamperLastByteOfFirstEntryPayload(final Path archive) throws Exception {
		byte[] data = Files.readAllBytes(archive);
		int local = findSignature(data, 0x04034B50);
		int central = findSignature(data, 0x02014B50);
		long compressedSize = readUnsignedInt(data, central + 20);
		int nameLength = readUnsignedShort(data, local + 26);
		int extraLength = readUnsignedShort(data, local + 28);
		int payloadOffset = local + 30 + nameLength + extraLength;
		int lastPayloadByte = Math.toIntExact(payloadOffset + compressedSize - 1L);
		data[lastPayloadByte] ^= 0x01;
		Files.write(archive, data);
	}

	private static int findSignature(final byte[] data, final int signature) {
		for (int i = 0; i <= data.length - 4; i++) {
			if ((data[i] & 0xFF) == (signature & 0xFF)
					&& (data[i + 1] & 0xFF) == ((signature >>> 8) & 0xFF)
					&& (data[i + 2] & 0xFF) == ((signature >>> 16) & 0xFF)
					&& (data[i + 3] & 0xFF) == ((signature >>> 24) & 0xFF)) {
				return i;
			}
		}
		throw new AssertionError("ZIP signature not found: " + Integer.toHexString(signature));
	}

	private static int readUnsignedShort(final byte[] data, final int offset) {
		return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
	}

	private static long readUnsignedInt(final byte[] data, final int offset) {
		return (data[offset] & 0xFFL)
				| ((data[offset + 1] & 0xFFL) << 8)
				| ((data[offset + 2] & 0xFFL) << 16)
				| ((data[offset + 3] & 0xFFL) << 24);
	}
}
