[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/utils-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/utils-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/utils-jdk11.svg)](https://github.com/wmkm0113/utils-jdk11/blob/mainline/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

# Nervousync® Java 工具包
## 项目简介:

本项目旨在为Java程序开发人员提供便捷的工具和实用功能。无论是初学者还是经验丰富的开发者，都可以从该工具包中受益。

工具包包含了许多常用的功能模块和工具类，涵盖了广泛的应用场景。它提供了一套简洁而强大的API，帮助开发人员更高效地编写Java代码。无论是处理日期和时间、字符串操作、文件操作还是网络请求，该工具包都提供了丰富的功能和方法。

此外，该工具包还注重代码质量和性能优化。代码经过精心设计和优化，以确保在各种场景下都能提供高效的执行速度和可靠性。

如果您对Java开发中常用的功能模块和工具类感兴趣，这个工具包绝对值得一试。 无论是加快开发速度、提高代码质量，还是简化常见任务，此工具包将为您的Java项目带来便利和效率。欢迎您贡献您的想法、建议和代码，让这个工具包更加完善。祝您在Java开发中取得成功！
欢迎您贡献您的想法、建议和代码，以进一步增强此工具包。

## JDK版本：
编译：OpenJDK 11

## 生命周期:

**此日期后不再添加新功能:** 2026年12月31日

**此日期后不再提供安全更新:** 2029年12月31日

## 使用方法
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

## 国际化支持：
四步添加国际化支持：
### 1. 创建描述文件和资源文件
在 META-INF 中创建文件 nervous.i18n，格式如下
```
{
    “groupId”: "{您的组织识别代码}",
    "bundle": "{您的项目识别代码}",
    "errors": [
        {
            "code": "{错误代码 二进制请以0d开头，八进制请以0o开头，16进制请以0x开头}",
            "key": "{错误代码对应的资源信息键值}"
        },
        ...
    ],
    "languages": [
        {
            "code": "{语言代码（例如：en-US）}",
            "name": "{语言名称（例如：English）}",
            "messages": [
                {
                    "key": "{资源信息键值}",
                    "content": "{资源信息英文内容}"
                },
                ...
            ]
        }，
        {
            "code": "{语言代码（例如：zh-CN）}",
            "name": "{语言名称（例如：简体中文）}",
            "messages": [
                {
                    "key": "{资源信息键值}",
                    "content": "{资源信息中文内容}"
                },
                ...
            ]
        }
    ]
}
```

### 2. 在需要的地方添加国际化支持：
在需要进行国际化的位置，使用 MultilingualUtils.findMessage(messageKey, collections) 读取国际化信息并输出

### 3. 向自定义异常添加国际化支持：（可选操作）
将所有自定义异常均继承自org.nervousync.exceptions.AbstractException，
在构造方法中将错误代码传递给org.nervousync.exceptions.AbstractException，
系统会自动读取资源文件中的错误信息，实现异常提示信息的国际化。

**Example:** org.nervousync.exceptions.AbstractException

### 4. 向日志中添加国际化支持：（可选操作）
使用 LoggerUtils.Logger 代替原有的日志对象，日志对象兼容slf4j的Logger对象，自动替换原有输出内容为多语言内容。

**Example:** BeanUtils, CertificateUtils 等的LoggerUtils.Logger实例

### 5. 打包时合并资源文件：（可选操作）
在多模块开发过程中，需要打包合并国际化资源文件时，需要使用到maven的shade插件，
添加transformer配置使用org.apache.maven.plugins.shade.resource.I18nResourceTransformer
并传入参数”groupId“和”bundle“，资源转换器会自动合并国际化资源文件，并输出到合并打包后的文件中

## BeanObject
**所在包**: org.nervousync.bean.core  
任何继承 BeanObject 的 JavaBean 类都可以更轻松地在对象实例和 JSON/XML/YAML 字符串之间进行转换。

## ConfigureManager
**所在包**: org.nervousync.configs
提供了一个统一管理的配置信息管理器，使用者可以通过ConfigureManager.initialize(String)静态方法初始化配置信息管理器，
配置文件的存储路径由参数传入。

如果传入的存储路径为空字符串，则默认在系统当前用户的工作目录中创建名为"configs"的文件夹，作为配置文件的存储路径。

ConfigureManager使用单例模式运行，需要读取配置文件时，使用ConfigureManager.getInstance().readConfigure方法进行读取，
读取时需要传入配置文件的实体类做为参数，如果需要读取同一实体类的不同配置，还可以传入后缀字符串作为区分。
需要保存配置文件时，使用ConfigureManager.getInstance().saveConfigure方法进行保存，需要传入配置文件的对象做为参数。

***注意：*** 配置文件的实体类需要继承BeanObject，默认使用XML格式进行数据保存，如果需要使用其他格式（JSON/YAML），
需要在配置文件的实体类上添加OutputConfig注解，并指定数据类型。

## ZipFile
**所在包**: org.nervousync.zip  
开发人员可以使用 ZipFile 创建 zip 文件、将文件添加到 zip 或从 zip 中提取文件。
支持分割存档文件、中日韩注释和入口路径、标准和AES加密/解密数据。

更多使用方法：参见 org.nervousync.test.zip.ZipTest

## Utilities List
### BeanUtils
**所在包**: org.nervousync.utils  
* 根据属性名称从源数据对象复制数据到目标数据对象
* 根据BeanProperty注解从源数据对象数组复制数据到目标数据对象
* 根据BeanProperty注解从源数据对象复制数据到目标数据对象数组

### CertificateUtils
**所在包**: org.nervousync.utils  
* 生成密钥对
* 签发X.509证书
* 从证书文件、PKCS12文件或二进制数据中读取X.509证书
* 验证 X.509 证书的有效期、数字签名
* 从PKCS12文件或二进制数据中读取公钥和私钥
* 生成PKCS12文件

### CollectionUtils
**所在包**: org.nervousync.utils
* 检查集合是否为空
* 检查集合是否包含目标对象
* 检查两个集合是否包含同一元素
* 检查集合是否有唯一元素
* 转换对象为列表
* 合并数组到列表中
* 合并属性信息实例到哈希表中
* 从集合中寻找第一个符合要求的元素

### ConvertUtils
**所在包**: org.nervousync.utils
* 转换字节数组为十六进制字符串
* 转换字节数组为字符串
* 转换字节数组为实例对象
* 转换任意实例对象为字节数组
* 转换属性信息为数据映射表

### CookieUtils
**所在包**: org.nervousync.utils  
需要添加依赖：
```
<dependency>
    <groupId>jakarta.servlet</groupId>
	<artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0 or higher</version>
</dependency>
```

### IDUtils
**所在包**: org.nervousync.utils  
* ID生成工具，使用Java SPI注册生成器实现类。
* 已集成的生成器：UUID 版本1到版本5, 雪花算法和NanoID.  
如何扩展生成器：
生成器实现类必须实现接口org.nervousync.generator.IGenerator，
并在META-INF/services文件夹下创建文件名为org.nervousync.generator.IGenerator的文件，文件中写明实现类的完整名称（包名+类名）

### ImageUtils
**所在包**: org.nervousync.utils  
* 读取图片的宽度、高度、宽高比
* 图片操作：剪切、缩放、添加水印、计算汉明距离（dHash/pHash）、计算数字签名（dHash/pHash）

### IPUtils
**所在包**: org.nervousync.utils  
* 根据给定的地址和CIDR，计算IP地址范围
* 在子网掩码和CIDR之间转换数据
* 在IPv4和IPv6之间转换数据
* 在IP地址和BigInteger之间转换数据
* 将压缩显示的IPv6地址展开

### LocationUtils
**所在包**: org.nervousync.utils  
* 在不同坐标系间转换数据，支持的坐标系：WGS84(GPS)/GCJ02/BD09
* 计算两个物理坐标之间的距离，单位：公里

### LoggerUtils
**所在包**: org.nervousync.utils  
* 使用编程方式初始化Log4j
* 使用编程方式初始化Log4j并设置目标包名为不同的日志等级
* 支持国际化的日志输出

### MailUtils
**所在包**: org.nervousync.utils  
* 发送接收电子邮件
* 获取文件夹中的电子邮件数量
* 列出所有文件夹名称
* 自动下载电子邮件中包含的附件
* 验证电子邮件签名
* 添加电子签名到邮件

### OTPUtils
**所在包**: org.nervousync.utils  
* 计算一次性密码算法的修正时间值
* 生成随机密钥
* 生成基于HMAC算法加密的一次性密码/基于时间戳算法的一次性密码值
* 验证基于HMAC算法加密的一次性密码/基于时间戳算法的一次性密码值

### PropertiesUtils
**所在包**: org.nervousync.utils  
* 从字符串/本地文件/网络文件/输入流中读取属性文件
* 修改属性文件
* 将属性文件保存到目标地址

### RawUtils
**所在包**: org.nervousync.utils  
* 从二进制数组中读取boolean/short/int/long/String类型的数据
* 向二进制数组中写入boolean/short/int/long/String类型的数据
* 转换字节数组为二进制数组
* 转换位数组为字节

### RequestUtils
**所在包**: org.nervousync.utils
* 解析HTTP方法字符串为HttpMethodOption
* 解析域名信息为IP地址
* 读取和验证服务器的SSL证书
* 发送请求并解析响应数据为字符串或指定的JavaBean
* 自由转换查询字符串和参数映射表
* 检查用户的角色信息，使用<code>request.isUserInRole</code>实现

### SecurityUtils
**所在包**: org.nervousync.utils  
* CRC多项式:  CRC-16/ISO-IEC-14443-3-A,CRC-32/JAMCRC,CRC-4/INTERLAKEN,CRC-16/TELEDISK,CRC-32/MPEG-2,CRC-16/GSM,CRC-6/GSM,CRC-7/UMTS,CRC-32/BZIP2,CRC-8/I-CODE,CRC-16/IBM-SDLC,CRC-16/LJ1200,CRC-10/ATM,CRC-8/NRSC-5,CRC-5/USB,CRC-7/ROHC,CRC-12/UMTS,CRC-8/BLUETOOTH,CRC-14/GSM,CRC-8/SMBUS,CRC-8/TECH-3250,CRC-5/G-704,CRC-16/MODBUS,CRC-12/DECT,CRC-7/MMC,CRC-16/CMS,CRC-24/FLEXRAY-A,CRC-24/FLEXRAY-B,CRC-32/ISO-HDLC,CRC-21/CAN-FD,CRC-8/LTE,CRC-15/CAN,CRC-24/LTE-A,CRC-30/CDMA,CRC-3/GSM,CRC-24/LTE-B,CRC-24/OPENPGP,CRC-12/CDMA2000,CRC-16/MAXIM-DOW,CRC-16/XMODEM,CRC-6/G-704,CRC-24/OS-9,CRC-16/DNP,CRC-32/AIXM,CRC-10/CDMA2000,CRC-6/CDMA2000-A,CRC-6/CDMA2000-B,CRC-16/TMS37157,CRC-16/UMTS,CRC-32/XFER,CRC-8/ROHC,CRC-16/DECT-R,CRC-8/WCDMA,CRC-8/DVB-S2,CRC-15/MPT1327,CRC-16/DECT-X,CRC-6/DARC,CRC-16/DDS-110,CRC-32/ISCSI,CRC-16/USB,CRC-8/MIFARE-MAD,CRC-8/AUTOSAR,CRC-16/KERMIT,CRC-16/IBM-3740,CRC-4/G-704,CRC-16/RIELLO,CRC-16/EN-13757,CRC-16/NRSC-5,CRC-14/DARC,CRC-31/PHILIPS,CRC-5/EPC-C1G2,CRC-32/BASE91-D,CRC-16/ARC,CRC-16/MCRF4XX,CRC-16/T10-DIF,CRC-24/INTERLAKEN,CRC-3/ROHC,CRC-13/BBC,CRC-11/UMTS,CRC-16/SPI-FUJITSU,CRC-10/GSM,CRC-8/DARC,CRC-8/OPENSAFETY,CRC-12/GSM,CRC-32/CKSUM,CRC-16/PROFIBUS,CRC-8/GSM-B,CRC-8/GSM-A,CRC-8/SAE-J1850,CRC-8/CDMA2000,CRC-8/MAXIM-DOW,CRC-16/GENIBUS,CRC-8/I-432-1,CRC-17/CAN-FD,CRC-16/OPENSAFETY-B,CRC-32/CD-ROM-EDC,CRC-16/OPENSAFETY-A,CRC-32/AUTOSAR,CRC-16/CDMA2000,CRC-11/FLEXRAY,CRC-24/BLE  
* 摘要算法: MD5/HmacMD5/SHA1/HmacSHA1/SHA2/HmacSHA2/SHA3/HmacSHA3/SHAKE128/SHAKE256/SM3/HmacSM3  
* 对称加密算法: Blowfish/DES/TripleDES/SM4/AES/RC2/RC4  
* 非对称加密算法: RSA/SM2

### ServiceUtils
**所在包**: org.nervousync.utils  
* 生成SOAP请求客户端
* 生成Restful请求客户端并处理请求

### SNMPUtils
**所在包**: org.nervousync.utils  
* 定时调度读取监控主机的相关信息

### StringUtils
**所在包**: org.nervousync.utils  
* 使用Base32/Base64编码给定的二进制字节数组
* 将给定的Base32/Base64编码字符串解码为二进制字节数组
* 将给定的字符串编码为霍夫曼树结果实例对象
* 去除字符串中的空格
* 检查给定的字符串是否为MD5值/UUID/电话号码/电子邮件地址等
* 检查给定的字符串是否为空/非空/包含字符串等
* 使用给定的分隔符分割字符串
* 根据规则截取字符串
* 验证给定的字符串是否符合代码类型

# 特别感谢
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>非常感谢 <a href="https://www.jetbrains.com/">JetBrains</a> 通过许可证赞助我们的开源项目。</span>
</span>