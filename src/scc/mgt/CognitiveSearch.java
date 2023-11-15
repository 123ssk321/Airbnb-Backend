package scc.mgt;
import java.util.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;
import com.azure.search.documents.util.SearchPagedResponse;
import scc.data.dto.*;

import java.util.Map;

public class CognitiveSearch {

    private final SearchClient searchClient;

    public CognitiveSearch(){
         searchClient = new SearchClientBuilder()
                .credential(new AzureKeyCredential(System.getenv(AzureProperties.SEARCH_SERVICE_QUERY_KEY)))
                .endpoint(System.getenv(AzureProperties.SEARCH_SERVICE_URL))
                .indexName(System.getenv(AzureProperties.SEARCH_SERVICE_INDEX_NAME))
                .buildClient();

    }

    private List<HouseSearch> search(String queryText, SearchOptions options) {
        SearchPagedIterable searchPagedIterable = searchClient.search(queryText, options, null);

        List<HouseSearch> results = new ArrayList<>();

        for (SearchPagedResponse resultResponse : searchPagedIterable.iterableByPage()) {
            resultResponse.getValue().forEach(searchResult -> {
                var map = searchResult.getDocument(SearchDocument.class);
                String id = map.get("id").toString();
                String name = map.get("name").toString();
                String description = map.get("description").toString();
                String photoIds = map.get("photoIds").toString();
                String ownerId = map.get("ownerId").toString();

                results.add(new HouseSearch(id,name,ownerId,description, photoIds));
            });
        }
        return results;
    }

    public List<HouseSearch> searchHouses(String queryText, String userId, Boolean searchName, Boolean searchDesc, int start, int length){

        SearchOptions options = new SearchOptions().setIncludeTotalCount(true)  //Include total number of hits in the index
                .setSelect("id", "name", "ownerId", "description", "photoIds")  //Fields to return
                .setOrderBy("ownerId desc")                                     //Ordered by owner
                .setSkip(start)                                                 //Number of results to skip
                .setTop(length);                                                //Number of results returned

        if(userId != null){
            options.setFilter("ownerId eq '"+ userId +"'");                       //Return houses of an owner
        }

        if( searchDesc != null && searchName != null) {
            if (searchDesc && searchName)                                           //Default
                options.setSearchFields("name", "description");
            if (searchDesc && !searchName)
                options.setSearchFields("description");                             //Search for description only
            if (!searchDesc && searchName)
                options.setSearchFields("name");                                    //Search for name only
        }

        return search(queryText,options);
    }
}







