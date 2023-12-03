package scc.storage.mongodb.collection;

import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import scc.data.dao.QuestionDAO;
import scc.storage.QuestionsStorage;

import java.util.List;

public class QuestionsCollection implements QuestionsStorage {
    private final MongoCollection<QuestionDAO> collection;

    public QuestionsCollection(MongoCollection<QuestionDAO> collection) {
        this.collection = collection;
    }

    @Override
    public QuestionDAO putQuestion(QuestionDAO question) {
        return null;
    }

    @Override
    public QuestionDAO getQuestion(String questionId) {
        return null;
    }

    @Override
    public boolean hasQuestion(String questionId) {
        return false;
    }

    public void addReply(String questionId, Bson update){

    }

    @Override
    public boolean hasReply(String questionId) {
        return false;
    }

    @Override
    public List<QuestionDAO> getHouseQuestions(String houseId, Boolean answered, int start, int length) {
        return null;
    }
}
