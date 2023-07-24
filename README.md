[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/utils-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/utils-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/utils-jdk11.svg)](https://github.com/wmkm0113/utils-jdk11/blob/mainline/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

# Nervousync® Java Utilities Package
## Description:

This project aims to provide convenient tools and utility functions for Java program development. 
Whether you are a beginner or an experienced developer, you can benefit from this toolkit.

The toolkit includes various functional modules and utility classes that cover a wide range of application scenarios. 
It offers a concise and powerful API to help developers write Java code more efficiently. 
Whether it's dealing with dates and times, string operations, file handling, 
or network requests, the toolkit provides rich functionality and methods.

Furthermore, the project emphasizes code quality and performance optimization. 
The code has been carefully designed and optimized
to ensure efficient execution speed and reliability in various scenarios.

If you are interested in commonly used functional modules and utility classes in Java development,
this toolkit is definitely worth a try.
Whether you want to speed up development, improve code quality, or simplify common tasks, 
this toolkit will bring convenience and efficiency to your Java projects. 
You are welcome to contribute your ideas, suggestions, and code to further enhance this toolkit. 

## JDK Version：
Compile：OpenJDK 11

## End of Life:

**Features Freeze:** 31, Dec, 2026
**Secure Patch:** 31, Dec, 2029

## Usage
### Maven:
```
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>utils-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```
### Gradle:
```
Manual: compileOnly group: 'org.nervousync', name: 'utils-jdk11', version: '${version}'
Short: compileOnly 'org.nervousync:utils-jdk11:${version}'
```
### SBT:
```
libraryDependencies += "org.nervousync" % "utils-jdk11" % "${version}" % "provided"
```
### Ivy:
```
<dependency org="org.nervousync" name="utils-jdk11" rev="${version}"/>
```

## Internationalize Support:
4 Steps to add i18n support:
### 1. Create declaration and resource files
Create nervousync.i18n file in META-INF
```
bundle={Your bundle string}
languages={language string using ',' split}
```
Add multilingual message file in META-INF/i18n, file named: {languageCode}.xml, file format is Properties

**Example:** META-INF/nervousync.i18n, META-INF/i18n/en-US.xml, META-INF/i18n/zh-CN.xml

### 2. Add internationalize support for exceptions:
Modify all Custom exception class, 
using MultilingualUtils.findMessage(bundle, messageKey, collections) to read the error message.

**Example:** org.nervousync.exceptions.AbstractException

### 3. Add internationalize support for logger:
Using LoggerUtils.Logger instead all the logger instance, 
replace log content with MultilingualUtils.findMessage(bundle, messageKey, collections)

**Example:** LoggerUtils.Logger instance in BeanUtils, CertificateUtils etc.

### 4. Add internationalize support for anything:
Using MultilingualUtils.findMessage(bundle, messageKey, collections)
to retrieve the multilingual message which will output

## BeanObject
**Package**: org.nervousync.bean.core  
Any JavaBean class extends BeanObject can easier convert between object instance and JSON/XML/YAML string.

## ZipFile
**Package**: org.nervousync.zip  
Developers can use ZipFile to create zip file, add file to zip or extract file from zip.
Supported split archive file, Chinese/Japanese/Korean comment and entry path, standard and AES encrypt/decrypt data.

Usage: See org.nervousync.test.zip.ZipTest

## Utilities List
### BeanUtils
**Package**: org.nervousync.utils  
* Copy object fields value from the source object to the target object, based field name
* Copy object fields value from the source object array to the target object, based annotation: BeanProperty
* Copy object fields value from the source object to the target object arrays, based annotation: BeanProperty

### CertificateUtils
**Package**: org.nervousync.utils  
* Generate Keypair
* Signature and generate X.509 certificate
* Parse X.509 certificate from certificate file, PKCS12 file or binary data arrays
* Validate X.509 certificate period and signature
* Read PublicKey/PrivateKey from binary data arrays or PKCS12 file
* Signature and generate PKCS12 file

### CollectionUtils
**Package**: org.nervousync.utils
* Check collection is empty
* Check collection contains target element
* Check two collection contains the same element
* Check collection contains unique element
* Convert the object to an array list
* Merge array to list
* Merge properties to map
* Find the first match element of collections

### ConvertUtils
**Package**: org.nervousync.utils
* Convert data bytes to hex string
* Convert data bytes to string
* Convert data bytes to Object
* Convert any to data bytes
* Convert properties to data map

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
* ID generator utils, register generator implements class by Java SPI.
* Embedded generator: UUID version 1 to version 5, Snowflake and NanoID.  
Extend generator: 
Generator class must implement interface org.nervousync.generator.IGenerator
and create file named: org.nervousync.generator.IGenerator save to META-INF/services

### ImageUtils
**Package**: org.nervousync.utils  
* Read image width (px), height (px), ratio (width/height). 
* Image operator: cut, resize and add watermark, calculate hamming (dHash/pHash), calculate signature (dHash/pHash)

### IPUtils
**Package**: org.nervousync.utils  
* Calculate IP range by given address and CIDR value
* Convert between netmask address and CIDR value
* Convert between IPv4 and IPv6
* Convert between IP address and BigInteger(for support IPv6)
* Expand the combo IPv6 address

### LocationUtils
**Package**: org.nervousync.utils  
* Convert GeoPoint at WGS84(GPS)/GCJ02/BD09
* Calculate the distance of two given geography point. (Unit: Kilometers)

### LoggerUtils
**Package**: org.nervousync.utils  
* Using programming to initialize Log4j
* Using programming to initialize Log4j and configure target package with custom level
* Support output internationalize logger information

### MailUtils
**Package**: org.nervousync.utils  
* Send/Receive email
* Count email in folder
* List folder names
* Download email attachment files automatically
* Verify email signature
* Add signature to email

### OTPUtils
**Package**: org.nervousync.utils  
* Calculate OTP fixed time value
* Generate random key
* Generate TOTP/HOTP Code
* Verify TOTP/HOTP Code

### PropertiesUtils
**Package**: org.nervousync.utils  
* Read properties from string/file path/URL instance/input stream
* Modify properties file
* Storage properties instance to the target file path

### RawUtils
**Package**: org.nervousync.utils  
* Read boolean/short/int/long/String from binary data bytes
* Write boolean/short/int/long/String into binary data bytes
* Convert the char array to binary data bytes
* Convert the bit array to byte

### RequestUtils
**Package**: org.nervousync.utils
* Parse http method string to HttpMethodOption
* Resolve domain name to IP address
* Retrieve and verify SSL certificate from server
* Send request and parse response content to target JavaBean or string
* Convert data between query string and parameter map
* Check user role code using <code>request.isUserInRole</code>

### SecurityUtils
**Package**: org.nervousync.utils  
* CRC polynomial:  CRC-16/ISO-IEC-14443-3-A,CRC-32/JAMCRC,CRC-4/INTERLAKEN,CRC-16/TELEDISK,CRC-32/MPEG-2,CRC-16/GSM,CRC-6/GSM,CRC-7/UMTS,CRC-32/BZIP2,CRC-8/I-CODE,CRC-16/IBM-SDLC,CRC-16/LJ1200,CRC-10/ATM,CRC-8/NRSC-5,CRC-5/USB,CRC-7/ROHC,CRC-12/UMTS,CRC-8/BLUETOOTH,CRC-14/GSM,CRC-8/SMBUS,CRC-8/TECH-3250,CRC-5/G-704,CRC-16/MODBUS,CRC-12/DECT,CRC-7/MMC,CRC-16/CMS,CRC-24/FLEXRAY-A,CRC-24/FLEXRAY-B,CRC-32/ISO-HDLC,CRC-21/CAN-FD,CRC-8/LTE,CRC-15/CAN,CRC-24/LTE-A,CRC-30/CDMA,CRC-3/GSM,CRC-24/LTE-B,CRC-24/OPENPGP,CRC-12/CDMA2000,CRC-16/MAXIM-DOW,CRC-16/XMODEM,CRC-6/G-704,CRC-24/OS-9,CRC-16/DNP,CRC-32/AIXM,CRC-10/CDMA2000,CRC-6/CDMA2000-A,CRC-6/CDMA2000-B,CRC-16/TMS37157,CRC-16/UMTS,CRC-32/XFER,CRC-8/ROHC,CRC-16/DECT-R,CRC-8/WCDMA,CRC-8/DVB-S2,CRC-15/MPT1327,CRC-16/DECT-X,CRC-6/DARC,CRC-16/DDS-110,CRC-32/ISCSI,CRC-16/USB,CRC-8/MIFARE-MAD,CRC-8/AUTOSAR,CRC-16/KERMIT,CRC-16/IBM-3740,CRC-4/G-704,CRC-16/RIELLO,CRC-16/EN-13757,CRC-16/NRSC-5,CRC-14/DARC,CRC-31/PHILIPS,CRC-5/EPC-C1G2,CRC-32/BASE91-D,CRC-16/ARC,CRC-16/MCRF4XX,CRC-16/T10-DIF,CRC-24/INTERLAKEN,CRC-3/ROHC,CRC-13/BBC,CRC-11/UMTS,CRC-16/SPI-FUJITSU,CRC-10/GSM,CRC-8/DARC,CRC-8/OPENSAFETY,CRC-12/GSM,CRC-32/CKSUM,CRC-16/PROFIBUS,CRC-8/GSM-B,CRC-8/GSM-A,CRC-8/SAE-J1850,CRC-8/CDMA2000,CRC-8/MAXIM-DOW,CRC-16/GENIBUS,CRC-8/I-432-1,CRC-17/CAN-FD,CRC-16/OPENSAFETY-B,CRC-32/CD-ROM-EDC,CRC-16/OPENSAFETY-A,CRC-32/AUTOSAR,CRC-16/CDMA2000,CRC-11/FLEXRAY,CRC-24/BLE  
* Digest provider: MD5/HmacMD5/SHA1/HmacSHA1/SHA2/HmacSHA2/SHA3/HmacSHA3/SHAKE128/SHAKE256/SM3/HmacSM3  
* Symmetric provider: Blowfish/DES/TripleDES/SM4/AES/RC2/RC4  
* Asymmetric provider: RSA/SM2

### ServiceUtils
**Package**: org.nervousync.utils  
* Generate SOAP Client instance
* Generate Restful Client and process request

### SNMPUtils
**Package**: org.nervousync.utils  
* Schedule read data from monitor host

### StringUtils
**Package**: org.nervousync.utils  
* Encode byte arrays using Base32/Base64
* Decode Base32/Base64 string to byte arrays
* Encode string to a Huffman tree
* Trim given string
* Match given string is MD5 value/UUID/phone number/e-mail address etc.
* Check given string is empty/notNull/notEmpty/contains string etc.
* Tokenize string by given delimiters
* Substring given input string by rule
* Validate given string is match code type

# Thanks For
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>Many thanks to <a href="https://www.jetbrains.com/">JetBrains</a> for sponsoring our Open Source projects with a license.</span>
</span>