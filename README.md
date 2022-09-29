# GeneUtils
Nervousync® Java Utilities Package

[![Build Status](https://app.travis-ci.com/wmkm0113/Gene.svg?branch=mainline)](https://app.travis-ci.com/wmkm0113/Gene)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/utils-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/utils-jdk11/)

## Usage
```
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>utils-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```
## BeanObject
**Package**: org.nervousync.bean.core  
Convert java bean to or parse from XML/JSON/YAML

## ZipFile
**Package**: org.nervousync.zip  
Create or extract zip file, support standard and aes crypto.

Usage: See org.nervousync.test.zip.ZipTest

## Utilities List
### BeanUtils
**Package**: org.nervousync.utils  
Read and parse file content to Java bean (List), support format: XML, JSON, YAML.  
Copy properties value from orig bean to dest bean, copy rule was same property name or convert mapping

### CertificateUtils
**Package**: org.nervousync.utils  
Generate key pair, X509 certificate, PKCS12 certificate and parse certificate from data bytes

### CookieUtils
**Package**: org.nervousync.utils  
Dependency required:
```
<dependency>
    <groupId>jakarta.servlet</groupId>
	<artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0 or higher</version>
</dependency>
```

### IDUtils
**Package**: org.nervousync.utils  
ID generator utils, register generator implements class by Java SPI. embedded generator: UUID version 1 to version 5, Snowflake and NanoID.  
Extend generator:  
Generator class must implement interface org.nervousync.generator.IGenerator
and create file named: org.nervousync.generator.IGenerator save to META-INF/services

### ImageUtils
**Package**: org.nervousync.utils  
Read image width (px), height (px), ratio (width/height). Image operator: cut, resize and add watermark,
calculate hamming (dHash/pHash), calculate signature (dHash/pHash)

### LocationUtils
**Package**: org.nervousync.utils  
Calculate distance by given GeoPoint, convert GeoPoint by GPS(WGS-84)/GCJ-02/DB-09

### OTPUtils
**Package**: org.nervousync.utils  
Generate TOTP/HOTP random key, calculate fixed time, generate and validate HOTP/TOTP code

### RawUtils
**Package**: org.nervousync.utils  
Read and write boolean/short/int/long/String from/to byte array by Little Endian or Big Endian

### RequestUtils
**Package**: org.nervousync.utils
#### IP address convert:
IP version 4 address to compatible IP version 6 address, IP address to byte array or BigInteger,
byte array or BigInteger to IP address, expand compressed IP version 6 address, convert data between netmask string and cidr,
calculate IP address range by given IP address and cidr
#### DNS:
Resolve domain name to IP address
#### Server Certificate:
Read SSL certificate from given url address
#### Request and Response:
Send request and parse response content. Support using proxy server, parse XML/JSON/YAML response content to Java bean (List),
parse request parameters and convert query string to Map, create query string from given data map, parse client IP address.

### SecurityUtils
**Package**: org.nervousync.utils  
CRC polynomial:  CRC-16/ISO-IEC-14443-3-A,CRC-32/JAMCRC,CRC-4/INTERLAKEN,CRC-16/TELEDISK,CRC-32/MPEG-2,CRC-16/GSM,CRC-6/GSM,CRC-7/UMTS,CRC-32/BZIP2,CRC-8/I-CODE,CRC-16/IBM-SDLC,CRC-16/LJ1200,CRC-10/ATM,CRC-8/NRSC-5,CRC-5/USB,CRC-7/ROHC,CRC-12/UMTS,CRC-8/BLUETOOTH,CRC-14/GSM,CRC-8/SMBUS,CRC-8/TECH-3250,CRC-5/G-704,CRC-16/MODBUS,CRC-12/DECT,CRC-7/MMC,CRC-16/CMS,CRC-24/FLEXRAY-A,CRC-24/FLEXRAY-B,CRC-32/ISO-HDLC,CRC-21/CAN-FD,CRC-8/LTE,CRC-15/CAN,CRC-24/LTE-A,CRC-30/CDMA,CRC-3/GSM,CRC-24/LTE-B,CRC-24/OPENPGP,CRC-12/CDMA2000,CRC-16/MAXIM-DOW,CRC-16/XMODEM,CRC-6/G-704,CRC-24/OS-9,CRC-16/DNP,CRC-32/AIXM,CRC-10/CDMA2000,CRC-6/CDMA2000-A,CRC-6/CDMA2000-B,CRC-16/TMS37157,CRC-16/UMTS,CRC-32/XFER,CRC-8/ROHC,CRC-16/DECT-R,CRC-8/WCDMA,CRC-8/DVB-S2,CRC-15/MPT1327,CRC-16/DECT-X,CRC-6/DARC,CRC-16/DDS-110,CRC-32/ISCSI,CRC-16/USB,CRC-8/MIFARE-MAD,CRC-8/AUTOSAR,CRC-16/KERMIT,CRC-16/IBM-3740,CRC-4/G-704,CRC-16/RIELLO,CRC-16/EN-13757,CRC-16/NRSC-5,CRC-14/DARC,CRC-31/PHILIPS,CRC-5/EPC-C1G2,CRC-32/BASE91-D,CRC-16/ARC,CRC-16/MCRF4XX,CRC-16/T10-DIF,CRC-24/INTERLAKEN,CRC-3/ROHC,CRC-13/BBC,CRC-11/UMTS,CRC-16/SPI-FUJITSU,CRC-10/GSM,CRC-8/DARC,CRC-8/OPENSAFETY,CRC-12/GSM,CRC-32/CKSUM,CRC-16/PROFIBUS,CRC-8/GSM-B,CRC-8/GSM-A,CRC-8/SAE-J1850,CRC-8/CDMA2000,CRC-8/MAXIM-DOW,CRC-16/GENIBUS,CRC-8/I-432-1,CRC-17/CAN-FD,CRC-16/OPENSAFETY-B,CRC-32/CD-ROM-EDC,CRC-16/OPENSAFETY-A,CRC-32/AUTOSAR,CRC-16/CDMA2000,CRC-11/FLEXRAY,CRC-24/BLE  
Digest provider: MD5/HmacMD5/SHA1/HmacSHA1/SHA2/HmacSHA2/SHA3/HmacSHA3/SHAKE128/SHAKE256/SM3/HmacSM3  
Symmetric provider: DES/TripleDES/SM4/AES  
Asymmetric provider: RSA/SM2

### StringUtils
**Package**: org.nervousync.utils  
Base32/Base64 encode and decode, convert string to huffman tree object, check given string is empty, blank, contains white space or emoji,
trim white space, validate given string by China ID Code, China Social Credit Code, Luhn Algorithm
