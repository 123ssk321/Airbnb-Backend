package scc.functions;

import com.microsoft.azure.functions.annotation.*;

import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with Timer Trigger.
 */
public class CosmosDBFunction {
    @FunctionName("recentAddedHouses")
    public void updateMostRecentUsers(@CosmosDBTrigger(name = "recentAddedHouses",
    										databaseName = "scc24db57449",
    										collectionName = "houses",
    										preferredLocations="North Europe",
    										createLeaseCollectionIfNotExists = true,
    										connectionStringSetting = "AzureCosmosDBConnection") 
        							String[] houses,
        							final ExecutionContext context ) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.incr("cnt:cosmos");
			for( String h : houses) {
				jedis.lpush("az::functions::houses", h);
			}
			jedis.ltrim("az::functions::houses", 0, 9);
		}
    }

}
