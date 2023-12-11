package scc.storage.mongodb.collection;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonType;
import org.bson.conversions.Bson;
import scc.data.dao.QuestionDAO;
import scc.storage.QuestionsStorage;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class QuestionsCollection implements QuestionsStorage {
    private final MongoCollection<QuestionDAO> collection;

    public QuestionsCollection(MongoCollection<QuestionDAO> collection) {
        this.collection = collection;
    }

    @Override
    public QuestionDAO putQuestion(QuestionDAO question) {
        if(collection.insertOne(question).wasAcknowledged())
            return question;
        return null;
    }

    @Override
    public QuestionDAO getQuestion(String questionId) {
        return collection.find(eq("_id", questionId), QuestionDAO.class).first();
    }

    @Override
    public boolean hasQuestion(String questionId) {
        return this.getQuestion(questionId) != null;
    }

    public void addReply(String questionId, Bson update){
        collection.updateOne(eq("_id", questionId), update);
    }

    @Override
    public boolean hasReply(String questionId) {
        var question = this.getQuestion(questionId);
        return question != null && question.getReply() != null;
    }

    @Override
    public List<QuestionDAO> getHouseQuestions(String houseId, Boolean answered, int start, int length) {
        FindIterable<QuestionDAO> query;
        if(answered == null){
            query = collection.find(eq("houseId", houseId), QuestionDAO.class);
        } else if (answered) {
            query = collection.find(and(eq("houseId", houseId), ne("reply", null)), QuestionDAO.class);
        } else {
            query = collection.find(and(eq("houseId", houseId), type("reply", BsonType.NULL)), QuestionDAO.class);
        }
        return query.skip(start).limit(length).into(new ArrayList<>());
    }
}