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
package org.nervousync.cache.provider.impl.redis;

import java.util.ArrayList;
import java.util.List;

import org.nervousync.cache.annotation.CacheProviderImpl;
import org.nervousync.cache.provider.impl.AbstractCacheProvider;
import org.nervousync.commons.beans.xml.cache.CacheServer;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.ConvertUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Cache provider implement by Redis using Jedis
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 4:36:52 PM $
 */
@CacheProviderImpl(name = "JedisProvider", defaultPort = 6379)
public final class JedisProviderImpl extends AbstractCacheProvider {

	/**
	 * Is single server mode
	 */
	private boolean singleMode = Globals.DEFAULT_VALUE_BOOLEAN;
	/**
	 * Jedis pool object
	 */
	private JedisPool jedisPool = null;
	/**
	 * Write jedis pool
	 */
	private ShardedJedisPool shardedJedisPool = null;
	/**
	 * Read jedis pool
	 */
	private ShardedJedisPool readJedisPool = null;

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#initializeConnection(java.util.List)
	 */
	@Override
	protected void initializeConnection(List<CacheServer> serverConfigList) {
		if (serverConfigList.isEmpty()) {
			return;
		}
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		
		jedisPoolConfig.setMaxTotal(this.getMaximumClient());
		jedisPoolConfig.setMaxIdle(this.getClientPoolSize());
		jedisPoolConfig.setMaxWaitMillis(this.getConnectTimeout() * 1000L);
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPoolConfig.setTestWhileIdle(true);
		
		if (serverConfigList.size() == 1) {
			CacheServer cachedServer = serverConfigList.get(0);
			String serverPassword = null;
			
			if (cachedServer.getServerPassword() != null 
					&& cachedServer.getServerPassword().length() > 0) {
				serverPassword = cachedServer.getServerPassword();
			}
			
			int serverPort = cachedServer.getServerPort();
			if (serverPort == Globals.DEFAULT_VALUE_INT) {
				serverPort = this.getDefaultPort();
			}
			
			this.jedisPool = new JedisPool(jedisPoolConfig, cachedServer.getServerAddress(), serverPort, 
					this.getConnectTimeout() * 1000, serverPassword);
			this.singleMode = true;
		} else {
			List<JedisShardInfo> readShardInfos = new ArrayList<>();
			List<JedisShardInfo> jedisShardInfos = new ArrayList<>();
			
			for (CacheServer cachedServer : serverConfigList) {
				int serverPort = cachedServer.getServerPort();
				if (serverPort == Globals.DEFAULT_VALUE_INT) {
					serverPort = this.getDefaultPort();
				}
				
				JedisShardInfo jedisShardInfo = new JedisShardInfo(cachedServer.getServerAddress(), serverPort);
				
				if (cachedServer.getServerPassword() != null && cachedServer.getServerPassword().length() > 0) {
					jedisShardInfo.setPassword(cachedServer.getServerPassword());
				}
				
				if (!cachedServer.isReadOnly()) {
					jedisShardInfos.add(jedisShardInfo);
				}
				readShardInfos.add(jedisShardInfo);
			}
			
			this.readJedisPool = new ShardedJedisPool(jedisPoolConfig, readShardInfos);
			this.shardedJedisPool = new ShardedJedisPool(jedisPoolConfig, jedisShardInfos);
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

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#expire(java.lang.String, int)
	 */
	@Override
	public void expire(String key, int expire) {
		if (this.singleMode) {
			Jedis jedis = this.singleClient();
			jedis.expire(key, expire);
			jedis.close();
		} else {
			ShardedJedis shardedJedis = this.writeClient();
			shardedJedis.expire(key, expire);
			shardedJedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#touch(java.lang.String)
	 */
	@Override
	public void touch(String... keys) {
		if (this.singleMode) {
			Jedis jedis = this.singleClient();
			jedis.touch(keys);
			jedis.close();
		} else {
			ShardedJedis shardedJedis = this.writeClient();
			for (String key : keys) {
				shardedJedis.touch(key);
			}
			shardedJedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#delete(java.lang.String)
	 */
	@Override
	public void delete(String key) {
		if (this.singleMode) {
			Jedis jedis = this.singleClient();
			jedis.del(key);
			jedis.close();
		} else {
			ShardedJedis shardedJedis = this.writeClient();
			shardedJedis.del(key);
			shardedJedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		byte[] objectData;
		if (this.singleMode) {
			Jedis jedis = this.singleClient();
			objectData = jedis.get(key.getBytes());
			jedis.close();
		} else {
			ShardedJedis shardedJedis = this.readClient();
			objectData = shardedJedis.get(key.getBytes());
			shardedJedis.close();
		}
		return objectData == null ? Globals.DEFAULT_VALUE_STRING : ConvertUtils.convertToString(objectData);
	}

	@Override
	public long incr(String key, long step) {
		long result;
		if (this.singleMode) {
			Jedis jedis = this.singleClient();
			result = jedis.incrBy(key, step);
			jedis.close();
		} else {
			ShardedJedis shardedJedis = this.readClient();
			result = shardedJedis.incrBy(key, step);
			shardedJedis.close();
		}
		return result;
	}

	@Override
	public long decr(String key, long step) {
		long result;
		if (this.singleMode) {
			Jedis jedis = this.singleClient();
			result = jedis.decrBy(key, step);
			jedis.close();
		} else {
			ShardedJedis shardedJedis = this.readClient();
			result = shardedJedis.decrBy(key, step);
			shardedJedis.close();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#destroy()
	 */
	@Override
	public void destroy() {
		if (this.jedisPool != null && !this.jedisPool.isClosed()) {
			this.jedisPool.close();
		}
		
		if (this.readJedisPool != null && !this.readJedisPool.isClosed()) {
			this.readJedisPool.close();
		}
		
		if (this.shardedJedisPool != null && !this.shardedJedisPool.isClosed()) {
			this.shardedJedisPool.close();
		}
	}
	
	private Jedis singleClient() {
		return this.jedisPool.getResource();
	}
	
	private ShardedJedis writeClient() {
		return this.shardedJedisPool.getResource();
	}
	
	private ShardedJedis readClient() {
		if (this.readJedisPool == null) {
			return this.writeClient();
		}
		return this.readJedisPool.getResource();
	}

	private void process(String key, String value, int expiry) {
		if (this.singleMode) {
			Jedis jedis = this.singleClient();
			jedis.setex(key.getBytes(), expiry, ConvertUtils.convertToByteArray(value));
			jedis.close();
		} else {
			ShardedJedis shardedJedis = this.writeClient();
			shardedJedis.setex(key.getBytes(), expiry, ConvertUtils.convertToByteArray(value));
			shardedJedis.close();
		}
	}
}
