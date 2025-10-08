package com.chriscarr.infra;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class LoggingBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            java.util.logging.LogManager.getLogManager().reset();
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
