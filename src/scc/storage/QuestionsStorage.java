package scc.storage;

import scc.data.dao.QuestionDAO;

import java.util.List;

public interface QuestionsStorage {

    QuestionDAO putQuestion(QuestionDAO question);

    QuestionDAO getQuestion(String questionId);

    boolean hasQuestion(String questionId);

    boolean hasReply(String questionId);

    List<QuestionDAO> getHouseQuestions(String houseId, Boolean answered, int start, int length);

}
