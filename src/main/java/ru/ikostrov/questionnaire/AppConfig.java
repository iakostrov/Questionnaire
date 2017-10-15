package ru.ikostrov.questionnaire;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class AppConfig {

    @Bean
    QuestionnaireDAO questionnaireDao() {
        return QuestionnaireDAO.init(JdbcConnectionPool.create("jdbc:h2:~/questionnairedb", "mylogin", "mypassword"));
    }

    @Bean
    Configuration freemarkerConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassLoaderForTemplateLoading(UndertowServer.class.getClassLoader(), "html");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_26));
        return configuration;
    }
}
