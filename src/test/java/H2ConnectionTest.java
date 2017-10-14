import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.ikostrov.questionnaire.Question;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by User on 01.10.2017.
 */
public class H2ConnectionTest {

    private static Connection connection = null;
    private static Statement statement = null;
    private static Question question = new Question(1,"Question", Arrays.asList("1","2","3"),2);

    @BeforeClass
    public static void connectAndCreateTable() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:~/sampledb", "mylogin", "mypassword");
        System.out.println("Connected to database");
        statement = connection.createStatement();
        System.out.println("Statement created");
        statement.execute("CREATE TABLE IF NOT EXISTS Question  " +
                "(ID INT AUTO_INCREMENT PRIMARY KEY," +
                " num INT," +
                " text VARCHAR(255)," +
                " answers ARRAY," +
                " right INT" +
                ");");
        System.out.println("Table 'Question' created");
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Question (num, text, answers, right) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1,question.getNum());
            preparedStatement.setString(2,question.getText());
            preparedStatement.setObject(3,question.getAnswers().toArray());
            preparedStatement.setInt(4,question.getRight());

            preparedStatement.execute();
            System.out.println("Question inserted");
        }
    }

    @AfterClass
    public static void dropTableAndCloseConnection() throws SQLException {
      statement.execute("DROP TABLE IF EXISTS  Question");
      System.out.println("Table 'Question' deleted");
      statement.close();
        System.out.println("Statement closed");
      connection.close();
        System.out.println("Connection closed");
    }

    @Test
    public void readTable() {
        try (
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Question")
        ) {
            System.out.println(resultSet.toString());
            while (resultSet.next()) {
                final List<String> answers = Arrays.stream(((Object[]) resultSet.getObject("answers"))).map(i -> i.toString()).collect(Collectors.toList());
                Question resQuestion = new Question(resultSet.getInt("num"),resultSet.getString("text"),answers,resultSet.getInt("right"));
                System.out.println(
                        question.toString()
                );
                System.out.println(
                        resQuestion.toString()
                );
                Assert.assertEquals("not equals",question,resQuestion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
