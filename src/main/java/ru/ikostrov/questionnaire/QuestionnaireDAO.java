package ru.ikostrov.questionnaire;

import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.h2.server.web.WebServlet;

import javax.annotation.PreDestroy;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by User on 01.10.2017.
 */

// TODO: 14.10.2017 Need refactoring 
public class QuestionnaireDAO implements Closeable {
    private Connection connection = null;
    private Statement statement = null;
    private DataSource dataSource = null;
    private static QuestionnaireDAO instance = null;

    @PreDestroy
    void preDestroy() throws SQLException, IOException {
        this.clearQuestionsTable();
        this.dropQuestionsTable();
        this.close();
    }


    private QuestionnaireDAO(DataSource source) throws SQLException {
        dataSource = source;
        connection = dataSource.getConnection();
        System.out.println("Connected to database");
        statement = connection.createStatement();
        System.out.println("Statement was created");
        statement.execute("CREATE TABLE IF NOT EXISTS Questions  " +
                "(ID INT AUTO_INCREMENT PRIMARY KEY," +
                " num INT," +
                " text VARCHAR(255)," +
                " answers ARRAY," +
                " right INT" +
                ");");
        System.out.println("'Questions' table was created");
    }

    public static QuestionnaireDAO init(DataSource dataSource,QuestionsHolder questionsHolder) {
        if (instance == null) {
            try {
                instance = new QuestionnaireDAO(dataSource);
                instance.initializeQuestions(questionsHolder);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private void initializeQuestions(QuestionsHolder questionsHolder) {
        try {
            clearQuestionsTable();
            insertQuestions(questionsHolder.getQuestions());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static HttpHandler getDatabaseManagmentConsoleHandler() throws ServletException {
        final DeploymentInfo deploymentInfo = new DeploymentInfo()
                .setClassLoader(QuestionnaireDAO.class.getClassLoader())
                .setContextPath("/h2")
                .setDeploymentName("h2")
                .addServlet(
                        Servlets.servlet("h2console", WebServlet.class)
                                .addMapping("/*")
                );
        final DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        deploymentManager.deploy();
        return deploymentManager.start();
    }

    public void clearQuestionsTable() throws SQLException {
        getStatement().execute("DELETE FROM Questions");
        System.out.println("Questions table was cleared");
    }
    public void dropQuestionsTable() throws SQLException {
        getStatement().execute("DROP TABLE Questions");
        System.out.println("Questions table removed");
    }

    public void insertQuestions(List<Question> questions) throws SQLException {
        try (final PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO Questions (num, text, answers, right) VALUES (?, ?, ?, ?)")) {
            for (Question question : questions) {
                preparedStatement.setInt(1, question.getNum());
                preparedStatement.setString(2, question.getText());
                preparedStatement.setObject(3, question.getAnswers().toArray());
                preparedStatement.setInt(4, question.getRight());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
        System.out.println("Questions was inserted in the table");
    }

    public List<Question> loadAllQuestions() {
        List<Question> questions = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery("Select ID, num, text, answers, right from Questions")) {
            while (resultSet.next()) {
                List<String> answers = Arrays.stream((Object[]) resultSet.getObject("answers"))
                        .map(Object::toString).collect(Collectors.toList());
                questions.add(new Question(resultSet.getInt("num"),
                        new String(resultSet.getString("text").getBytes(Charset.forName("UTF-8"))),
                        answers, resultSet.getInt("right")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public Connection getConnection() {
        if (instance != null)
            return connection;
        else
            return null;
    }

    public Statement getStatement() {
        if (instance != null)
            return statement;
        else
            return null;
    }

    @Override
    public void close() throws IOException {
        try {
            if(!statement.isClosed()) {
                statement.close();
                System.out.println("statement closed");
            }
            if(!connection.isClosed()) {
                connection.close();
                System.out.println("connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
