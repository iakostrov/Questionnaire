package ru.ikostrov.questionnaire;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.routing;

/**
 * Created by User on 17.09.2017.
 */
public class UndertowServer {

    public static void main(String[] args) throws IOException, SQLException, ServletException {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Undertow.builder().addHttpListener(8080, "localhost",
                new SessionAttachmentHandler(new InMemorySessionManager("SESSION_MANAGER"), new SessionCookieConfig())
                        .setNext(path()
                                .addPrefixPath("/h2", QuestionnaireDAO.getDatabaseManagmentConsoleHandler())
                                .addPrefixPath("/", routing()
                                        .get("question", context.getBean("questionHandler", HttpHandler.class))
                                        .post("answer", context.getBean("answerHandler", HttpHandler.class))
                                        .setFallbackHandler(
                                                httpServerExchange -> httpServerExchange.getResponseSender().send("no page"))
                                )
                        )).build().start();
    }
}