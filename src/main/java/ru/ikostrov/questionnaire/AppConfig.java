package ru.ikostrov.questionnaire;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@org.springframework.context.annotation.Configuration
@ComponentScan
@PropertySource("classpath:settings.properties")
@SuppressWarnings("unused")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    @Autowired
    QuestionnaireDAO questionnaireDao(QuestionsHolder holder) {
        return QuestionnaireDAO.init(
                JdbcConnectionPool.create(
                        env.getProperty("database.url"),
                        env.getProperty("database.username"),
                        env.getProperty("database.password")),
                holder);
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
