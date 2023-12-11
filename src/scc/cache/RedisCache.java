package scc.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scc.mgt.AzureProperties;

public class RedisCache {
	private static JedisPool instance;
	private final ObjectMapper mapper;

	private static final String REDIS_PORT="REDIS_PORT";
	private static final String REDIS_SSL="REDIS_SSL";
	private static final String REDIS_SSL_TRUE="TRUE";

	public 	RedisCache(){ mapper = new ObjectMapper();}
	private synchronized JedisPool getCachePool() {
		if( instance != null)
			return instance;
		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);
		instance = new JedisPool(poolConfig, System.getenv(AzureProperties.REDISH_HOST_NAME), Integer.parseInt(System.getenv(REDIS_PORT)), 1000,
				System.getenv(AzureProperties.REDIS_KEY), System.getenv(REDIS_SSL).equals(REDIS_SSL_TRUE)); //AZURE-6380 ssl=true KUBERNETES-6379 ssl=false
		return instance;
	}

	public <T> void set(String key, T value){
		try(Jedis jedis = getCachePool().getResource()) {
			jedis.set(key, mapper.writeValueAsString(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public <T> T get(String key, Class<T> valueClass){
		try(Jedis jedis = getCachePool().getResource()) {
			String value = jedis.get(key);
			if(value ==  null) { return null; }
			return mapper.readValue(value, valueClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

	public void delete(String key){
		try(Jedis jedis = instance.getResource()) {
			jedis.del(key);
		}
	}
}
