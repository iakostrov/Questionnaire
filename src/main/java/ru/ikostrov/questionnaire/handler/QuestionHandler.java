package ru.ikostrov.questionnaire.handler;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikostrov.questionnaire.Question;
import ru.ikostrov.questionnaire.QuestionnaireDAO;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by User on 14.10.2017.
 */
@Component
public class QuestionHandler implements MappedHandler {
    private final Template template;
    private final List<Question> questions;

    @Autowired
    public QuestionHandler( Configuration configuration, QuestionnaireDAO dao) throws IOException {
        this.template = configuration.getTemplate("questionnaireTemplate.ftl");
        this.questions = dao.loadAllQuestions();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        int q = 0;
        String qstring = exchange.getQueryParameters().getOrDefault("q", new ArrayDeque<>()).peek();
        if (qstring != null)
            q = Integer.parseInt(qstring);

        StringWriter stringWriter = new StringWriter();

        template.process(questions.get(q), stringWriter);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=utf-8");
        exchange.getResponseSender().send(stringWriter.toString());
    }

    @Override
    public String path() {
        return "questions";
    }

    @Override
    public String method() {
        return "get";
    }
}
