package org.nervousync.test.utils;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.beans.network.NetworkInfo;
import org.nervousync.exceptions.beans.network.NetworkInfoException;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.SystemUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public final class SystemTest extends BaseTest {

	@Test
    @Order(0)
	public void systemInfo() {
		this.logger.info("System name: {}, version: {}, user name: {}, user home: {}, workspace: {}, identify Key: {}",
				SystemUtils.OPERATE_SYSTEM_NAME, SystemUtils.OPERATE_SYSTEM_VERSION, SystemUtils.USER_NAME,
				SystemUtils.USER_HOME, SystemUtils.USER_DIR, SystemUtils.identifiedKey());
		this.logger.info("Windows: {}, Unix: {}, Linux: {}, MacOS: {}, MacOSX: {}, OS2: {}, Solaris: {}, SunOS: {}, " +
				"MPEiX: {}, HPUX: {}, AIX: {}, OS390: {}, FreeBSD: {}, Irix: {}, DigitalUnix: {}, Netware: {}, OSF1: {}," +
				"OpenVMS: {}", SystemUtils.isWindows(), SystemUtils.isUnix(), SystemUtils.isLinux(), SystemUtils.isMacOS(),
				SystemUtils.isMacOSX(), SystemUtils.isOS2(), SystemUtils.isSolaris(), SystemUtils.isSunOS(),
				SystemUtils.isMPEiX(), SystemUtils.isHPUX(), SystemUtils.isAIX(), SystemUtils.isOS390(),
				SystemUtils.isFreeBSD(), SystemUtils.isIrix(), SystemUtils.isDigitalUnix(), SystemUtils.isNetware(),
				SystemUtils.isOSF1(), SystemUtils.isOpenVMS());
		this.logger.info("Java version: {}, major version: {}, java home: {}, tmp directory: {}",
				SystemUtils.JAVA_VERSION, SystemUtils.MAJOR_VERSION, SystemUtils.JAVA_HOME, SystemUtils.JAVA_TMP_DIR);
	}

	@Test
    @Order(10)
	public void networkInfo() throws SocketException, NetworkInfoException {
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		do {
			NetworkInfo networkInfo = new NetworkInfo(enumeration.nextElement());
			this.logger.info("Adaptor {} IPv4 addresses: ", networkInfo.getDisplayName());
			networkInfo.getIPv4AddressList().forEach(ipAddressInfo ->
					this.logger.info("IP address: {}, local: {}, loop: {}, link local: {}",
							ipAddressInfo.getIpAddress(), ipAddressInfo.isLocal(), ipAddressInfo.isLoop(),
							ipAddressInfo.isLinkLocal()));
			this.logger.info("Adaptor {} IPv6 addresses: ", networkInfo.getDisplayName());
			networkInfo.getIPv6AddressList().forEach(ipAddressInfo ->
					this.logger.info("IP address: {}, local: {}, loop: {}, link local: {}",
							ipAddressInfo.getIpAddress(), ipAddressInfo.isLocal(), ipAddressInfo.isLoop(),
							ipAddressInfo.isLinkLocal()));
			this.logger.info("Adaptor {} IP addresses: ", networkInfo.getDisplayName());
			networkInfo.getIpAddressList().forEach(ipAddressInfo ->
					this.logger.info("IP address: {}, local: {}, loop: {}, link local: {}",
							ipAddressInfo.getIpAddress(), ipAddressInfo.isLocal(), ipAddressInfo.isLoop(),
							ipAddressInfo.isLinkLocal()));
		} while (enumeration.hasMoreElements());
	}
}
