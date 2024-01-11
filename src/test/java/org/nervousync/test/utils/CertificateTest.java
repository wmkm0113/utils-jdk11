package org.nervousync.test.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.commons.Globals;
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
        this.logger.info("Certificate_Sign", ((RSAPrivateKey) SIGN_KEY).getPrivateExponent().toString(), ((RSAPrivateKey) SIGN_KEY).getModulus().toString());
        KeyPair keyPair = SecurityUtils.RSAKeyPair();
        X509Certificate x509Certificate = generateCertificate(keyPair);
        byte[] certBytes = x509Certificate.getEncoded();
        this.logger.info("Certificate_Base64", StringUtils.base64Encode(certBytes));
        X509Certificate readCertificate = CertificateUtils.x509(certBytes, VERIFY_KEY);
        this.logger.info("Certificate_Read",
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
            this.logger.error("Certificate_Read_Private_PKCS12_Error");
            return;
        }
        this.logger.info("Certificate_Read_Private", StringUtils.base64Encode(privateKey.getEncoded()));
        X509Certificate x509Certificate =
                CertificateUtils.x509(BASE_PATH + STORE_PATH, "CERT", "changeit");
        if (x509Certificate == null) {
            this.logger.error("Certificate_Read_x509_PKCS12_Error");
            return;
        }
        this.logger.info("Certificate_Read", StringUtils.base64Encode(x509Certificate.getEncoded()));
        PublicKey publicKey = CertificateUtils.publicKey("RSA", VERIFY_KEY.getEncoded());
        this.logger.info("Certificate_Verify", CertificateUtils.verify(x509Certificate, publicKey));
        PrivateKey readKey =
                CertificateUtils.privateKey(BASE_PATH + STORE_PATH, "CERT", "changeit");
        if (readKey == null) {
            this.logger.error("Certificate_Read_Private_File_Error");
            return;
        }
        this.logger.info("Certificate_Read_Private", StringUtils.base64Encode(readKey.getEncoded()));
    }

    @Test
    @Order(20)
    public void readPrivateKeyFromPem() {
        PrivateKey privateKey = CertificateUtils.privateKey("src/test/resources/private.pem");
        if (privateKey == null) {
            this.logger.error("Certificate_Read_PEM_Private_Error");
            return;
        }
        this.logger.info("Certificate_Read_PEM_Private", StringUtils.base64Encode(privateKey.getEncoded()));
    }

    @Test
    @Order(30)
    public void readPublicKeyFromPem() throws CertificateEncodingException {
        X509Certificate x509Certificate =
                CertificateUtils.x509("src/test/resources/public.pem", "nervousync.com");
        if (x509Certificate == null) {
            this.logger.error("Certificate_Read_PEM_Certificate_Error");
            return;
        }
        this.logger.info("Certificate_Read_PEM_Certificate", StringUtils.base64Encode(x509Certificate.getEncoded()));
    }

    private static X509Certificate generateCertificate(final KeyPair keyPair) {
        long currentTime = DateTimeUtils.currentUTCTimeMillis();
        return CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(),
                new Date(currentTime), new Date(currentTime + 365 * 24 * 60 * 60 * 1000L), "TestCert",
                (SIGN_KEY == null) ? keyPair.getPrivate() : SIGN_KEY, "SHA1withRSA");
    }
}
