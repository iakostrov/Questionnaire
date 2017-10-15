import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ikostrov.questionnaire.Question;
import ru.ikostrov.questionnaire.QuestionnaireDAO;
import ru.ikostrov.questionnaire.QuestionsHolder;

import java.util.Arrays;

@Configuration
public class TestConfig {
    @Bean
    @Autowired
    QuestionnaireDAO prodQuestionnaireDao(QuestionsHolder holder) {
        return QuestionnaireDAO.init(JdbcConnectionPool.create("jdbc:h2:~/testdb", "mylogin", "mypassword"), holder);
    }

    @Bean
    QuestionsHolder testQuestionHolder() {
        return () ->
                Arrays.asList(
                        new Question(3, " Кто самый умный", Arrays.asList("Киса", "Мур", "Рома"), 1)
                );
    }
}

