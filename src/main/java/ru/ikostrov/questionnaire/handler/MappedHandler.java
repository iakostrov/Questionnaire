package ru.ikostrov.questionnaire.handler;

import io.undertow.server.HttpHandler;

public interface MappedHandler extends HttpHandler {
    String path();

    String method();
}
