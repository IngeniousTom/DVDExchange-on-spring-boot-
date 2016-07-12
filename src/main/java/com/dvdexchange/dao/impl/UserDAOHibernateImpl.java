package com.dvdexchange.dao.impl;


import com.dvdexchange.model.User;
import com.dvdexchange.dao.UserDAO;
import com.dvdexchange.utils.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class UserDAOHibernateImpl implements UserDAO {

    public void addUser(String name, String lastname, String patronym, String birthdateString, String email, String password) {
        /*Позволяет добавить пользователя удобным образом -  передавая дату рождения пользователя в виде строки.
        Метод сам берет на себя все преобразования*/
        Session session = HibernateUtil.getSessionFactory().openSession();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            java.util.Date parsed = null;
            try {
                parsed = format.parse("30/03/1991");
            } catch (ParseException pe) {
                System.out.println("Неверный формат даты");
                pe.printStackTrace();
            }
            Date birthdate = new Date(parsed.getTime());
            User user = new User(name, lastname, patronym, birthdate, email, password);
            session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public User getUserById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        User resultUser = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String hql = "FROM User U " +
                    "WHERE U.id=:user_id";
            Query query = session.createQuery(hql);
            query.setParameter("user_id", id);
            resultUser = (User) query.uniqueResult();
            Hibernate.initialize(resultUser.getDisks());
            Hibernate.initialize(resultUser.getTakenDisks());
            //нужно проинициализировать еще все поля всех Takendisks
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return resultUser;
    }

    public User getUserByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        User resultUser = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String hql = "FROM User U " +
                    "WHERE U.email=:user_email";
            Query query = session.createQuery(hql);
            query.setParameter("user_email", email);
            resultUser = (User) query.uniqueResult();
            Hibernate.initialize(resultUser.getDisks());
            Hibernate.initialize(resultUser.getTakenDisks());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return resultUser;
    }

    public List<User> getAllUsers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<User> allUsers = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            allUsers = session.createCriteria(User.class).list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return allUsers;
    }

    public void changeUserPassword(String newPassword) {
        //Позволяет текущему залогининому пользователю поменять свой пароль. user_id = 1 - это затычка
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String hql = "UPDATE User U SET U.password = :new_password " +
                    "WHERE U.id=:user_id";
            Query query = session.createQuery(hql);
            query.setParameter("new_password", newPassword);
            query.setParameter("user_id", 1);
            query.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteUserByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(User.class);
            cr.add(Restrictions.eq("email", email));
            User user = (User) cr.uniqueResult();
            session.delete(user);
            tx.commit();
        } catch (HibernateException e) {
            System.out.println("Объект не найден");
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

}
