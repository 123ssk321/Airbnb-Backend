package scc.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scc.data.dao.HouseDAO;
import scc.data.dao.UserDAO;

public class RedisCache {
	private static final String REDISH_HOST_NAME = "REDIS_URL";
	private static final String REDIS_KEY = "REDIS_KEY";
	
	private static JedisPool instance;
	public RedisCache(){
		getCachePool();
	}
	public static synchronized JedisPool getCachePool() {
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
		instance = new JedisPool(poolConfig, System.getenv(REDISH_HOST_NAME), 6380, 1000,
				System.getenv(REDIS_KEY), true);
		return instance;
	}

	public void putUser(UserDAO user){
		ObjectMapper mapper = new ObjectMapper();
		try(Jedis jedis = instance.getResource()) {
			jedis.set("user:"+user.getId(), mapper.writeValueAsString(user));
		} catch (Exception e) {
			e.printStackTrace();
        }
    }

	public UserDAO getUser(String userId) {
		ObjectMapper mapper = new ObjectMapper();
		try(Jedis jedis = instance.getResource()) {
			String str = jedis.get("user:"+userId);
			if(str ==  null) { return null; }
			return mapper.readValue(str, UserDAO.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }

	public void deleteUser(String userID){
		try(Jedis jedis = instance.getResource()) {
			jedis.del("user:"+userID);
		}
	}

	public void putHouse(HouseDAO house){
		ObjectMapper mapper = new ObjectMapper();
		try(Jedis jedis = instance.getResource()) {
			jedis.set("house:"+house.getId(), mapper.writeValueAsString(house));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HouseDAO getHouse(String houseId) {
		ObjectMapper mapper = new ObjectMapper();
		try(Jedis jedis = instance.getResource()) {
			String str = jedis.get("house:"+houseId);
			return mapper.readValue(str, HouseDAO.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void deleteHouse(String houseId){
		try(Jedis jedis = instance.getResource()) {
			jedis.del("house:"+houseId);
		}
	}

}
