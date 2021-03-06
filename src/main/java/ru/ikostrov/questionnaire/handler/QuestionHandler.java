package ru.ikostrov.questionnaire.handler;

import freemarker.template.Template;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ru.ikostrov.questionnaire.Question;

import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by User on 14.10.2017.
 */
public class QuestionHandler implements HttpHandler {
    private final Template template;
    private final List<Question> questions;

    public QuestionHandler(Template template, List<Question> questions) {
        this.template = template;
        this.questions = questions;
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
}
