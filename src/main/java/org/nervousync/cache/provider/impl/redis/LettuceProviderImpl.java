package org.nervousync.cache.provider.impl.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.nervousync.cache.annotation.CacheProviderImpl;
import org.nervousync.cache.provider.impl.AbstractCacheProvider;
import org.nervousync.commons.beans.xml.cache.CacheServer;
import org.nervousync.commons.core.Globals;

import java.util.List;
import java.util.Vector;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 8/25/2020 4:07 PM $
 */
@CacheProviderImpl(name = "LettuceProvider", defaultPort = 6379)
public final class LettuceProviderImpl extends AbstractCacheProvider {

	/**
	 * Is single server mode
	 */
	private boolean singleMode = Globals.DEFAULT_VALUE_BOOLEAN;

	private StatefulRedisClusterConnection<String, String> clusterConnection = null;
	private RedisAdvancedClusterCommands<String, String> clusterCommands = null;

	private StatefulRedisConnection<String, String> singleConnection = null;
	private RedisCommands<String, String> singleCommands = null;

	@Override
	protected void initializeConnection(List<CacheServer> serverConfigList) {
		if (serverConfigList.isEmpty()) {
			return;
		}
		this.singleMode = (serverConfigList.size() == 1);
		if (serverConfigList.size() > 1) {
			Vector<RedisURI> vector = new Vector<>(serverConfigList.size());
			serverConfigList.forEach(cacheServer ->
					vector.add(RedisURI.builder()
							.withHost(cacheServer.getServerAddress())
							.withPort(cacheServer.getServerPort())
							.withPassword(cacheServer.getServerPassword()).build()));
			RedisClusterClient clusterClient = RedisClusterClient.create(vector);
			this.clusterConnection = clusterClient.connect();
			this.clusterCommands = this.clusterConnection.sync();
		} else {
			CacheServer cacheServer = serverConfigList.get(0);
			RedisClient redisClient =
					RedisClient.create(RedisURI.builder().withHost(cacheServer.getServerAddress())
							.withPort(cacheServer.getServerPort())
							.withPassword(cacheServer.getServerPassword()).build());
			this.singleConnection = redisClient.connect();
			this.singleCommands = this.singleConnection.sync();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#set(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void set(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#add(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void add(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#replace(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void replace(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	@Override
	public void expire(String key, int expire) {
		if (this.singleMode) {
			this.singleCommands.expire(key, expire);
		} else {
			System.out.println(this.clusterCommands.ttl(key));
			this.clusterCommands.expire(key, expire);
		}
	}

	@Override
	public void touch(String... keys) {
		if (this.singleMode) {
			this.singleCommands.touch(keys);
		} else {
			this.clusterCommands.touch(keys);
		}
	}

	@Override
	public void delete(String key) {
		if (this.singleMode) {
			this.singleCommands.del(key);
		} else {
			this.clusterCommands.del(key);
		}
	}

	@Override
	public String get(String key) {
		return this.singleMode ? this.singleCommands.get(key) : this.clusterCommands.get(key);
	}

	@Override
	public long incr(String key, long step) {
		long result;
		if (this.singleMode) {
			result = this.singleCommands.incrby(key, step);
		} else {
			result = this.clusterCommands.incrby(key, step);
		}
		return result;
	}

	@Override
	public long decr(String key, long step) {
		long result;
		if (this.singleMode) {
			result = this.singleCommands.decrby(key, step);
		} else {
			result = this.clusterCommands.decrby(key, step);
		}
		return result;
	}

	@Override
	public void destroy() {
		if (this.singleMode) {
			this.clusterConnection.close();
		} else {
			this.singleConnection.close();
		}
	}

	private void process(String key, String value, int expiry) {
		if (this.singleMode) {
			this.singleCommands.setex(key, expiry, value);
		} else {
			this.clusterCommands.setex(key, expiry, value);
		}
	}
}
