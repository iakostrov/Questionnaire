package ru.ikostrov.questionnaire;

import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikostrov.questionnaire.handler.MappedHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.routing;

/**
 * Created by User on 17.09.2017.
 */
@Component
public class UndertowServer {

    @Autowired
    List<MappedHandler> mappedHandlerList = null;

    private Undertow server = null;

    private RoutingHandler addRoutingHandlers() {
        RoutingHandler routing = routing();
        for (MappedHandler mappedHandler : mappedHandlerList) {
            routing().add(mappedHandler.method(), mappedHandler.path(), mappedHandler);
        }
        routing.setFallbackHandler(exchange -> exchange.getResponseSender().send("no page"));
        return routing;
    }

    @PostConstruct
    void startServer() throws IOException, SQLException, ServletException {
        server = Undertow.builder()
                .addHttpListener(8080, "localhost",
                        new SessionAttachmentHandler(new InMemorySessionManager("SESSION_MANAGER"), new SessionCookieConfig())
                                .setNext(path()
                                        .addPrefixPath("/h2", QuestionnaireDAO.getDatabaseManagmentConsoleHandler())
                                        .addPrefixPath("/", addRoutingHandlers()
                                        )
                                ))
                .build();
        server.start();
    }

    @PreDestroy
    void stopServer() {
        server.stop();
    }
}