package org.nervousync.test.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.commons.core.Globals;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

public final class CertificateTest extends BaseTest {

    private static final String STORE_PATH = Globals.DEFAULT_PAGE_SEPARATOR + "store";

	private static final String BASE_PATH;
    private static final PublicKey VERIFY_KEY;
    private static final PrivateKey SIGN_KEY;

    static {
		BASE_PATH = System.getProperty("java.io.tmpdir");
        KeyPair keyPair = SecurityUtils.RSAKeyPair();
        VERIFY_KEY = keyPair.getPublic();
        SIGN_KEY = keyPair.getPrivate();
    }

    @AfterAll
    public static void clearFile() {
        FileUtils.removeFile(BASE_PATH + STORE_PATH);
    }

    @Test
    @Order(0)
    public void generateCertificate() throws CertificateEncodingException {
        this.logger.info("Signature Private exponent: {}", ((RSAPrivateKey) SIGN_KEY).getPrivateExponent().toString());
        this.logger.info("Signature Modulus: {}", ((RSAPrivateKey) SIGN_KEY).getModulus().toString());
        KeyPair keyPair = SecurityUtils.RSAKeyPair();
        X509Certificate x509Certificate = generateCertificate(keyPair);
        byte[] certBytes = x509Certificate.getEncoded();
        this.logger.info("Generate certificate base64 encoding: {}", StringUtils.base64Encode(certBytes));
        X509Certificate readCertificate = CertificateUtils.x509(certBytes, VERIFY_KEY);
        this.logger.info("Read certificate: {}",
                (readCertificate == null)
                        ? Globals.DEFAULT_VALUE_STRING
                        : StringUtils.base64Encode(readCertificate.getEncoded()));
        long currentTime = DateTimeUtils.currentUTCTimeMillis();
        byte[] pkcs12Bytes = CertificateUtils.PKCS12(keyPair, IDUtils.snowflake(),
                new Date(currentTime), new Date(currentTime + 30 * 24 * 60 * 60 * 1000L),
                "CERT", "CERT", "changeit", SIGN_KEY, "SHA256withRSA");
        FileUtils.saveFile(pkcs12Bytes, BASE_PATH + STORE_PATH);
    }

    @Test
    @Order(10)
    public void readKeyFromFile() throws CertificateEncodingException, IOException {
        PrivateKey privateKey =
                CertificateUtils.privateKey(FileUtils.readFileBytes(BASE_PATH + STORE_PATH),
                        "CERT", "changeit");
        if (privateKey == null) {
            this.logger.error("Read private key from PKCS12 bytes error! ");
            return;
        }
        this.logger.info("Read private key: {}", StringUtils.base64Encode(privateKey.getEncoded()));
        X509Certificate x509Certificate =
                CertificateUtils.x509(BASE_PATH + STORE_PATH, "CERT", "changeit");
        if (x509Certificate == null) {
            this.logger.error("Read certificate from PKCS12 error! ");
            return;
        }
        this.logger.info("Read certificate: {}", StringUtils.base64Encode(x509Certificate.getEncoded()));
        PublicKey publicKey = CertificateUtils.publicKey("RSA", VERIFY_KEY.getEncoded());
        this.logger.info("Certificate verify result: {}", CertificateUtils.verify(x509Certificate, publicKey));
        PrivateKey readKey =
                CertificateUtils.privateKey(BASE_PATH + STORE_PATH, "CERT", "changeit");
        if (readKey == null) {
            this.logger.error("Read private key from file error! ");
            return;
        }
        this.logger.info("Read private key: {}", StringUtils.base64Encode(readKey.getEncoded()));
    }

    private static X509Certificate generateCertificate(final KeyPair keyPair) {
        long currentTime = DateTimeUtils.currentUTCTimeMillis();
        return CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(),
                new Date(currentTime), new Date(currentTime + 365 * 24 * 60 * 60 * 1000L), "TestCert",
                (SIGN_KEY == null) ? keyPair.getPrivate() : SIGN_KEY, "SHA1withRSA");
    }
}
