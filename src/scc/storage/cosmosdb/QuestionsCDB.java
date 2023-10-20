package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.data.dao.HouseDAO;
import scc.data.dao.QuestionDAO;

import java.util.logging.Logger;

public class QuestionsCDB {
    private final CosmosContainer container;

    private static final Logger Log = Logger.getLogger(QuestionsCDB.class.getName());

    public QuestionsCDB(CosmosContainer container) {
        this.container = container;
    }

    public CosmosItemResponse<QuestionDAO> putQuestion(QuestionDAO question) {
        return container.createItem(question);
    }

    public boolean hasQuestion(String questionId) {
        return this.getQuestion(questionId) != null;
    }

    public void addReply(String questionId, CosmosPatchOperations updateOps) {
        PartitionKey key = new PartitionKey(questionId);
        container.patchItem(questionId, key, updateOps, QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getHouseQuestions(String houseId) {
        return container.queryItems(
                "SELECT * FROM questions WHERE questions.houseId=\"" + houseId + "\"",
                new CosmosQueryRequestOptions(),
                QuestionDAO.class);
    }

    public QuestionDAO getQuestion(String questionId){
        try {
            var questions = container.queryItems(
                    "SELECT * FROM questions WHERE questions.id=\"" + questionId + "\"",
                    new CosmosQueryRequestOptions(),
                    QuestionDAO.class);
            var questionIt = questions.iterator();
            return questionIt.hasNext()? questionIt.next() : null;
        } catch (Exception e){
            Log.info("Exception caught:" + e);
            return null;
        }

    }
}
