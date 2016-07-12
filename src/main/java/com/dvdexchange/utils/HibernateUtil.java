package com.dvdexchange.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Это вспомогательный класс, который заботится о создании фабрики сессий, чтобы получать и открывать
 * сессии (соединения с БД) в нужных местах программы. Метод получения фабрики, как и сам объект фабрики сессии
 * статические, а значит экземпляр класса не нужен.
 * Фабрика сессии создается на соснове конфигурационного файла hibernate.cfg.xml лишь один раз при старте программы
 * в болке статической инициализации, и затем ее можно получить в любое время с помошью метода getSessionFactory().
 */

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Создание SessionFactory из hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            //Вывод исключения, если оно возникнет, чтобы понять причину несиправности
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
