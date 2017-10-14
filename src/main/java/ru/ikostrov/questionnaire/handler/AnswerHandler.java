package ru.ikostrov.questionnaire.handler;

import freemarker.template.Template;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;
import ru.ikostrov.questionnaire.Question;
import ru.ikostrov.questionnaire.UndertowServer;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 14.10.2017.
 */
public class AnswerHandler implements HttpHandler {
    private final List<Question> questions;
    private final Template template;

    EagerFormParsingHandler inner = null;

    public AnswerHandler(Template template,List<Question> questions) {
        this.template = template;
        this.questions = questions;
        FormParserFactory.Builder formPFBuilder = FormParserFactory.builder();
        formPFBuilder.setDefaultCharset(StandardCharsets.UTF_8.name());
        inner = new EagerFormParsingHandler(formPFBuilder.build());
        inner.setNext(this::handle);
    }

    void handle(HttpServerExchange exchange) throws Exception {
        FormData form = exchange.getAttachment(FormDataParser.FORM_DATA);

        FormData.FormValue qfw = form.getFirst("q");
        int q = Integer.parseInt(qfw.getValue());

        FormData.FormValue answerFw = form.getFirst("answer");
        String answer = answerFw.getValue();
        //String answer = new String(answerFw.getValue().getBytes(StandardCharsets.ISO_8859_1.name()),StandardCharsets.UTF_8.name());

        System.out.println(answer);

        Question question = questions.get(q);
        int rightAnswers = 0;

        SessionManager sm = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
        SessionConfig sessionConfig = exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
        Session session = sm.getSession(exchange, sessionConfig);
        if (session == null)
            session = sm.createSession(exchange, sessionConfig);

        Integer rightAnswers2 = (Integer) session.getAttribute("rightAnswers");

        if (rightAnswers2 == null)
            rightAnswers2 = 0;

        Cookie cookieRightAnswers = exchange.getRequestCookies().get("rightAnswers");
        if (cookieRightAnswers != null)
            rightAnswers = Integer.parseInt(cookieRightAnswers.getValue());

        else
            cookieRightAnswers = new CookieImpl("rightAnswers", "0");

        boolean right = question.isRight(answer);
        if (right) {
            rightAnswers += 1;
            rightAnswers2 += 1;
        }
        Map<String, Cookie> responseCookies = exchange.getResponseCookies();
        System.out.println("rightAnswers " + rightAnswers);
        if (q < questions.size() - 1) {
            session.setAttribute("rightAnswers", rightAnswers2);
            responseCookies.put("rightAnswers", cookieRightAnswers.setValue(String.valueOf(rightAnswers)));
            Handlers.redirect("question?q=" + (q + 1)).handleRequest(exchange);
        } else {
            final StringWriter responseWriter = new StringWriter();
            final HashMap<String, Object> map = new HashMap<>();
            map.put("total", questions.size());
            map.put("rightAnswers", rightAnswers);

            template.process(map, responseWriter);
            responseCookies.put("rightAnswers", cookieRightAnswers.setMaxAge(0));
            session.invalidate(exchange);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=utf-8");
            exchange.getResponseSender().send(responseWriter.toString());
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        inner.handleRequest(exchange);
    }
}
