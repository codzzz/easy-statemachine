package com.codzzz.lang.easy.statemachine.container;

import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;

public interface ApplicationRunListener extends ApplicationListener<ApplicationContextEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    default void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent && event.getApplicationContext() instanceof ConfigurableApplicationContext) {
            this.started((ConfigurableApplicationContext) event.getApplicationContext());
        }
    }

    /**
     * The context has been refreshed and the application has started.
     *
     * @param applicationContext the application context.
     */
    void started(ConfigurableApplicationContext applicationContext);
}
