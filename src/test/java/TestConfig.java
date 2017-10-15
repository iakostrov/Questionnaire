import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ru.ikostrov.questionnaire.Question;
import ru.ikostrov.questionnaire.QuestionnaireDAO;
import ru.ikostrov.questionnaire.QuestionsHolder;

import java.util.Arrays;

@Configuration
@PropertySource("classpath:test.properties")
public class TestConfig {

    @Autowired
    Environment env;

    @Bean
    @Autowired
    QuestionnaireDAO questionnaireDao(QuestionsHolder holder) {
        return QuestionnaireDAO.init(
                JdbcConnectionPool.create(
                        env.getProperty("database.url"),
                        env.getProperty("database.username"),
                        env.getProperty("database.password")),
                holder);
    }

    @Bean
    QuestionsHolder testQuestionHolder() {
        return () ->
                Arrays.asList(
                        new Question(3, " Кто самый умный", Arrays.asList("Киса", "Мур", "Рома"), 1)
                );
    }
}

