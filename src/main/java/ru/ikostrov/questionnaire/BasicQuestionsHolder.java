package ru.ikostrov.questionnaire;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicQuestionsHolder implements QuestionsHolder {
    @Override
    public List<Question> getQuestions() {
     return Arrays.asList(
                new Question(0, " 2 + 2", Arrays.asList("2", "5", "4"), 2),
                new Question(1, " 3 + 3", Arrays.asList("6", "8", "9"), 0),
                new Question(2, " 1 - 1", Arrays.asList("-1", "0", "1"), 1),
                new Question(3, " Кто самый умный", Arrays.asList("Киса", "Мур", "Рома"), 1)
        );
    }
}
