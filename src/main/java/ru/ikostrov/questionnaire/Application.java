package ru.ikostrov.questionnaire;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("ru.ikostrov.questionnaire");
        context.registerShutdownHook();
    }
}