package org.nervousync.cache.provider.impl.memcached;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.nervousync.cache.annotation.CacheProviderImpl;
import org.nervousync.cache.provider.impl.AbstractCacheProvider;
import org.nervousync.commons.beans.xml.cache.CacheServer;
import org.nervousync.commons.core.Globals;

import java.io.IOException;
import java.util.List;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 8/11/2020 11:37 AM $
 */
@CacheProviderImpl(name = "XMemcachedProvider", defaultPort = 11211)
public final class MemcachedProviderImpl extends AbstractCacheProvider {

	/**
	 * Memcached client object
	 */
	private MemcachedClient memcachedClient = null;

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#initializeConnection(java.util.List)
	 */
	@Override
	protected void initializeConnection(List<CacheServer> serverConfigList) {
		int[] serverWeightList = new int[serverConfigList.size()];

		int index = 0;

		StringBuilder serverAddresses = new StringBuilder();
		for (CacheServer memcachedServer : serverConfigList) {
			serverAddresses.append(" ").append(memcachedServer.getServerAddress()).append(":").append(memcachedServer.getServerPort());
			serverWeightList[index] = memcachedServer.getServerWeight();
		}

		MemcachedClientBuilder clientBuilder =
				new XMemcachedClientBuilder(AddrUtil.getAddresses(serverAddresses.toString().trim()), serverWeightList);
		clientBuilder.setCommandFactory(new BinaryCommandFactory());

		if (serverConfigList.size() > 1) {
			clientBuilder.setSessionLocator(new KetamaMemcachedSessionLocator());
		}

		for (CacheServer memcachedServer : serverConfigList) {
			if (memcachedServer.getServerUserName() != null && memcachedServer.getServerUserName().length() > 0
					&& memcachedServer.getServerPassword() != null && memcachedServer.getServerPassword().length() > 0) {
				clientBuilder.addAuthInfo(AddrUtil.getOneAddress(memcachedServer.getServerAddress() + ":" + memcachedServer.getServerPort()),
						AuthInfo.typical(memcachedServer.getServerUserName(), memcachedServer.getServerPassword()));
			}
		}
		try {
			this.memcachedClient = clientBuilder.build();
		} catch (IOException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Initialize memcached client error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#set(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void set(String key, String value, int expiry) {
		try {
			this.memcachedClient.set(key, expiry, value);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Set data error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#add(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void add(String key, String value, int expiry) {
		try {
			this.memcachedClient.add(key, expiry, value);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Set data error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#replace(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void replace(String key, String value, int expiry) {
		try {
			this.memcachedClient.replace(key, expiry, value);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Set data error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#expire(java.lang.String, int)
	 */
	@Override
	public void expire(String key, int expiry) {
		try {
			this.memcachedClient.touch(key, expiry);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Set data error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#touch(java.lang.String)
	 */
	@Override
	public void touch(String... keys) {
		try {
			for (String key : keys) {
				this.memcachedClient.touch(key, this.getExpireTime());
			}
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Set data error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#delete(java.lang.String)
	 */
	@Override
	public void delete(String key) {
		try {
			this.memcachedClient.delete(key);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Set data error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		try {
			return this.memcachedClient.get(key);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Get data error! ", e);
			}
		}
		return null;
	}

	@Override
	public long incr(String key, long step) {
		try {
			return this.memcachedClient.incr(key, step);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Get data error! ", e);
			}
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	@Override
	public long decr(String key, long step) {
		try {
			return this.memcachedClient.decr(key, step);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Get data error! ", e);
			}
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#destroy()
	 */
	@Override
	public void destroy() {
		if (this.memcachedClient != null) {
			if (!this.memcachedClient.isShutdown()) {
				try {
					this.memcachedClient.shutdown();
				} catch (IOException e) {
					this.logger.error("Shutdown memcached client error! ");
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Stack message: ", e);
					}
				}
			}
		}
	}
}
