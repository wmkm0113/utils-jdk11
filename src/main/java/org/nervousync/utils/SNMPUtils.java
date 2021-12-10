/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import org.nervousync.beans.snmp.SNMPData;
import org.nervousync.beans.snmp.TargetHost;
import org.nervousync.commons.snmp.SNMPDataOperator;
import org.nervousync.enumerations.net.IPProtocol;
import org.nervousync.enumerations.snmp.SNMPVersion;
import org.nervousync.enumerations.snmp.auth.SNMPAuthProtocol;
import org.nervousync.exceptions.snmp.ProcessorConfigException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 25, 2017 8:50:34 PM $
 */
public final class SNMPUtils {

	private static volatile SNMPUtils INSTANCE = null;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SNMPUtils.class);
	
	private static final String PROTOCOL_UDP = "udp:";
	private static final String PROTOCOL_TCP = "tcp:";
	
	private static final OctetString NO_AUTH_NOPRIV = new OctetString("noAuthUser");
	private static final OctetString AUTH_NOPRIV = new OctetString("authUser");
	private static final OctetString AUTH_PRIV = new OctetString("privUser");
	
	private final IPProtocol protocol;
	private int period = 5;
	private final List<TargetHost> existsHosts;
	private final ScheduledExecutorService scheduledExecutorService;
	private Snmp snmp = null;
	
	private SNMPUtils(IPProtocol protocol, int serverCount) throws IOException {
		this.protocol = protocol;
		this.existsHosts = new ArrayList<>(serverCount);
		this.scheduledExecutorService = Executors.newScheduledThreadPool(serverCount);
		switch (this.protocol) {
		case TCP:
			this.snmp = new Snmp(new DefaultTcpTransportMapping());
			break;
		case UDP:
			this.snmp = new Snmp(new DefaultUdpTransportMapping());
			break;
			default:
				return;
		}
		
		this.snmp.listen();
	}
	
	public static boolean initialize(int serverCount) {
		return SNMPUtils.initialize(IPProtocol.UDP, serverCount);
	}
	
	public static boolean initialize(IPProtocol protocol, int serverCount) {
		if (SNMPUtils.INSTANCE != null) {
			return true;
		}
		try {
			synchronized (SNMPUtils.class) {
				if (SNMPUtils.INSTANCE == null) {
					SNMPUtils.INSTANCE = new SNMPUtils(protocol, serverCount);
				}
			}
			return true;
		} catch (IOException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Initialize instance error! ", e);
			}
			return Boolean.FALSE;
		}
	}
	
	public static SNMPUtils getInstance() {
		return SNMPUtils.INSTANCE;
	}
	
	public boolean addMonitor(String identifiedKey, TargetHost targetHost, 
			List<PDU> pduList, SNMPDataOperator snmpDataOperator) {
		if (this.existsHosts.contains(targetHost)) {
			return true;
		}
		
		try {
			this.existsHosts.add(targetHost);
			this.scheduledExecutorService.scheduleAtFixedRate(
					new SNMPProcessor(identifiedKey, targetHost, pduList, snmpDataOperator), 
					0L, this.period, TimeUnit.SECONDS);
			return true;
		} catch (ProcessorConfigException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Add monitor target host error! ", e);
			}
			return Boolean.FALSE;
		}
	}
	
	/**
	 * @return the period
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public void destroy() throws IOException {
		this.scheduledExecutorService.shutdownNow();
		this.snmp.close();
	}

	private List<VariableBinding> retrieveData(Target<Address> target, PDU pdu) {
		if (this.snmp != null) {
			try {
				ResponseEvent<Address> responseEvent = this.snmp.send(pdu, target);
				if (responseEvent != null && responseEvent.getResponse() != null) {
					PDU response = responseEvent.getResponse();
					if (response.getErrorIndex() == PDU.noError 
							&& response.getErrorStatus() == PDU.noError) {
						return response.getBindingList(new OID());
					}
				}
			} catch (IOException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Retrieve snmp data error! ", e);
				}
			}
		}
		return new ArrayList<>();
	}
	
	private static final class SNMPProcessor implements Runnable {

		private final String identifiedKey;
		private final Target<Address> target;
		private final List<PDU> pduList;
		private final SNMPDataOperator snmpDataOperator;
		
		public SNMPProcessor(String identifiedKey, TargetHost targetHost, List<PDU> pduList, 
				SNMPDataOperator snmpDataOperator) throws ProcessorConfigException {
			if (identifiedKey == null || targetHost == null || pduList == null 
					|| pduList.isEmpty() || snmpDataOperator == null) {
				throw new ProcessorConfigException("Argument invalid");
			}
			this.identifiedKey = identifiedKey;
			this.target = SNMPUtils.getInstance().generateTarget(targetHost);
			this.pduList = pduList;
			this.snmpDataOperator = snmpDataOperator;
		}
		
		@Override
		public void run() {
			SNMPData snmpData = new SNMPData();
			
			snmpData.setIdentifiedKey(this.identifiedKey);
			for (PDU pdu : this.pduList) {
				List<VariableBinding> returnList =
						SNMPUtils.getInstance().retrieveData(this.target, pdu);
				for (VariableBinding variableBinding : returnList) {
					snmpData.addData(variableBinding.getOid().toString(),
							variableBinding.getVariable().toString());
				}
			}
			
			this.snmpDataOperator.operateData(snmpData);
		}
	}

	private OID retrieveAuthProtocol(SNMPAuthProtocol snmpAuthProtocol) {
		switch (snmpAuthProtocol) {
			case MD5:
				return AuthMD5.ID;
			case SHA:
				return AuthSHA.ID;
		}
		return null;
	}
	
	private Target<Address> generateTarget(TargetHost targetHost) {
		if (targetHost == null) {
			return null;
		}

		String address = null;

		switch (this.protocol) {
		case TCP:
			address = PROTOCOL_TCP + targetHost.getIpAddress() + "/" + targetHost.getPort();
			break;
		case UDP:
			address = PROTOCOL_UDP + targetHost.getIpAddress() + "/" + targetHost.getPort();
			break;
		}
		Target<Address> target;
		
		if (SNMPVersion.VERSION3.equals(targetHost.getVersion())) {
			USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
			SecurityModels.getInstance().addSecurityModel(usm);

			target = new UserTarget<>();

			OctetString securityName = null;
			OID authProtocol = null;
			OctetString authPassword = null;
			OID privProtocol = null;
			OctetString privPassword = null;

			switch (targetHost.getAuth()) {
				case NOAUTH_NOPRIV:
					target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
					securityName = NO_AUTH_NOPRIV;
					break;
				case AUTH_NOPRIV:
					target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
					securityName = AUTH_NOPRIV;
					authProtocol = retrieveAuthProtocol(targetHost.getAuthProtocol());
					authPassword = new OctetString(targetHost.getAuthPassword());
					break;
				case AUTH_PRIV:
					target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
					securityName = AUTH_PRIV;
					authProtocol = retrieveAuthProtocol(targetHost.getAuthProtocol());
					authPassword = new OctetString(targetHost.getAuthPassword());
					switch (targetHost.getPrivProtocol()) {
						case PrivDES:
							privProtocol = PrivDES.ID;
							break;
						case Priv3DES:
							privProtocol = Priv3DES.ID;
							break;
					}
					privPassword = new OctetString(targetHost.getPrivPassword());
					break;
			}

			if (securityName != null) {
				target.setSecurityName(securityName);

				UsmUser usmUser = new UsmUser(securityName, authProtocol, authPassword, privProtocol, privPassword);
				this.snmp.getUSM().addUser(securityName, usmUser);
			}
		} else {
			target = new CommunityTarget<>();
			((CommunityTarget<Address>)target).setCommunity(new OctetString(targetHost.getCommunity()));
		}

		target.setAddress(GenericAddress.parse(address));
		target.setRetries(targetHost.getRetries());
		target.setTimeout(targetHost.getTimeOut());
		
		switch (targetHost.getVersion()) {
		case VERSION1:
			target.setVersion(SnmpConstants.version1);
			break;
		case VERSION2C:
			target.setVersion(SnmpConstants.version2c);
			break;
		case VERSION3:
			target.setVersion(SnmpConstants.version3);
			break;
		}
		return target;
	}
}
