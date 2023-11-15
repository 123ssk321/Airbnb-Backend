package scc.storage.cosmosdb;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
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

    public CosmosPagedIterable<QuestionDAO> getHouseQuestions(String houseId, Boolean answered, int start, int length) {
        String query;
        if (answered == null)
            query = "SELECT * FROM questions WHERE questions.houseId=\"" + houseId + "\" "+
                    "OFFSET " + start + " LIMIT " + length;
        else if (answered) {
            query = "SELECT * FROM questions WHERE questions.houseId=\"" + houseId + "\""
                                            + " AND NOT IS_NULL(questions.reply) "+
                    "OFFSET " + start + " LIMIT " + length;
        } else
            query = "SELECT * FROM questions WHERE questions.houseId=\"" + houseId + "\""
                    + " AND IS_NULL(questions.reply) " +
                    "OFFSET " + start + " LIMIT " + length;

        return container.queryItems(
                query,
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

    public boolean hasReply(String questionId) {
        var question = getQuestion(questionId);
        return question.getReply() != null;
    }
}
