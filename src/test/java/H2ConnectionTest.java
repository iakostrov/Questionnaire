import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ikostrov.questionnaire.Question;
import ru.ikostrov.questionnaire.QuestionnaireDAO;
import ru.ikostrov.questionnaire.QuestionsHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by User on 01.10.2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@DirtiesContext
public class H2ConnectionTest {

    @Autowired
    QuestionnaireDAO questionnaireDAO;

    @Autowired
    QuestionsHolder questionsHolder;


    @AfterClass
    public static void dropTableAndCloseConnection() throws SQLException {
    }

    @Test
    public void readTable() {
        Question question = questionsHolder.getQuestions().get(0);
        Question resQuestion = null;
        try (
                ResultSet resultSet = questionnaireDAO.getStatement().executeQuery("SELECT * FROM Questions")
        ) {
            System.out.println(resultSet.toString());
            while (resultSet.next()) {
                final List<String> answers = Arrays.stream(((Object[]) resultSet.getObject("answers"))).map(i -> i.toString()).collect(Collectors.toList());
                resQuestion = new Question(resultSet.getInt("num"), resultSet.getString("text"), answers, resultSet.getInt("right"));
                System.out.println(
                        question.toString()
                );
                System.out.println(
                        resQuestion.toString()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
