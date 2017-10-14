package ru.ikostrov.questionnaire;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.*;
import ru.ikostrov.questionnaire.handler.AnswerHandler;
import ru.ikostrov.questionnaire.handler.QuestionHandler;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.routing;

/**
 * Created by User on 17.09.2017.
 */
public class UndertowServer {

    public static void main(String[] args) throws IOException, SQLException, ServletException {
        QuestionnaireDAO.init("jdbc:h2:~/questionnairedb", "mylogin", "mypassword");
        Undertow.builder().addHttpListener(8080, "localhost", getHandler()).build().start();
    }

    private static HttpHandler getHandler() throws IOException, SQLException, ServletException {
        List<Question> questions = QuestionnaireDAO.loadAllQuestions();

        return new SessionAttachmentHandler(new InMemorySessionManager("SESSION_MANAGER"), new SessionCookieConfig())
                .setNext(path()
                        .addPrefixPath("/h2", QuestionnaireDAO.getDatabaseManagmentConsoleHandler())
                        .addPrefixPath("/", routing()
                                .get("question", new QuestionHandler(getTemplate("questionnaireTemplate.ftl"), questions))
                                .post("answer", new AnswerHandler(getTemplate("resultTemplate.ftl"), questions))
                                .setFallbackHandler(
                                        httpServerExchange -> httpServerExchange.getResponseSender().send("no page"))
                        )
                );
    }

    private static Template getTemplate(String templateName) throws IOException {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassLoaderForTemplateLoading(UndertowServer.class.getClassLoader(), "html");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_26));
        return configuration.getTemplate(templateName);
    }


}