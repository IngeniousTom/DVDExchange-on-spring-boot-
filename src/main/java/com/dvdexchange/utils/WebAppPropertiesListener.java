package com.dvdexchange.utils;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class WebAppPropertiesListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String rootPath;
        rootPath = servletContextEvent.getServletContext().getRealPath("/");

        System.setProperty("webroot", rootPath);
        System.out.println("webroot applied: " + System.getProperty("webroot"));
        DBInitializer.initializeDB();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
