package com.dvdexchange.dao.impl;

import com.dvdexchange.model.Disk;
import com.dvdexchange.model.Takenitem;
import com.dvdexchange.model.User;
import com.dvdexchange.dao.DiskDAO;
import com.dvdexchange.utils.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;

@Repository
public class DiskDAOHibernateImpl implements DiskDAO {

    public boolean addDisk(String name, int idHost) {
        //Метод служит для того, чтобы текущий пользователь мог добавить новый диск
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean isSuccessful = true;
        try {
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(User.class);
            cr.add(Restrictions.eq("id", idHost));
            User host = (User) cr.uniqueResult();
            Disk disk = new Disk(name, host);
            session.save(disk);
            tx.commit();
        } catch (HibernateException e) {
            isSuccessful = false;
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return isSuccessful;
    }

    public List<Disk> getAllFreeDisks() {
        //Получение списка всех свободных дисков всех пользователей
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Disk> diskList = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            //получаем список id всех позаимствованных дисков (т.е. тех что есть в таблице "takenitem")
            Criteria crTakenItems = session.createCriteria(Takenitem.class).setProjection(Projections.property("iddisk"));
            List<Integer> takenDisksIds = crTakenItems.list();
            //Формируем с помощью полученного списка целевой список всех свободных дисков
            Criteria crDisks = session.createCriteria(Disk.class);
            crDisks.add(Restrictions.not(Restrictions.in("id", takenDisksIds)));
            diskList = crDisks.list();
            for (Disk disk : diskList) {
                Hibernate.initialize(disk.getHost());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return diskList;
    }

    public List<Disk> getAllFreeDisksForCurrentUser(int currentUserId) {
        //Получение списка всех свободных дисков всех пользователей
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Disk> diskList = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            //получаем список id всех позаимствованных дисков (т.е. тех что есть в таблице "takenitem")
            Criteria crTakenItems = session.createCriteria(Takenitem.class).setProjection(Projections.property("iddisk"));
            List<Integer> takenDisksIds = crTakenItems.list();
            //Формируем с помощью полученного списка целевой список всех свободных дисков
            Criteria crDisks = session.createCriteria(Disk.class);
            crDisks.add(Restrictions.not(Restrictions.in("id", takenDisksIds)));
            crDisks.add(Restrictions.not(Restrictions.eq("host.id", currentUserId)));     //текущий пользователь не может позаимствовать свои собственные диски,
            // поэтому их и предлагать не следует
            diskList = crDisks.list();
            for (Disk disk : diskList) {
                Hibernate.initialize(disk.getHost());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return diskList;
    }

    public List<Disk> getAllDisksTakenByLoggedUser(int currentUserId) {
        //Метод получает список всех дисков, которые взял текущий пользователь, с информацией о хозяине диска
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Disk> diskList = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            //получаем список id всех дисков, что позаимстовал текущий пользователь из "takenitem"
            Criteria crTakenItems = session.createCriteria(Takenitem.class);
            crTakenItems.add(Restrictions.eq("iduser", currentUserId)).setProjection(Projections.property("iddisk"));
            List<Integer> takenDisksIds = crTakenItems.list();
            //Формируем с помощью полученного списка целевой список
            Criteria crDisks = session.createCriteria(Disk.class);
            crDisks.add(Restrictions.in("id", takenDisksIds));
            diskList = crDisks.list();
            for (Disk disk : diskList) {
                Hibernate.initialize(disk.getHost());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return diskList;
    }

    public boolean editDiskInformationById(int id, String newDiskInfo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean isSuccessful = true;
        try {
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Disk.class);
            cr.add(Restrictions.eq("id", id));
            Disk disk = (Disk) cr.uniqueResult();
            if (disk != null) {
                disk.setName(newDiskInfo);
                session.save(disk);
                tx.commit();
            } else {
                isSuccessful = false;
            }
            /*String hql = "UPDATE Disk D set D.name = :new_info " +
                    "WHERE D.id = :id_disk";
            Query query = session.createQuery(hql);
            query.setParameter("new_info", newDiskInfo);
            query.setParameter("id_disk", id);
            query.executeUpdate();*/
            /*tx.commit();*/
        } catch (HibernateException e) {
            isSuccessful = false;
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return isSuccessful;
    }

    public boolean deleteDiskById(int id) {
        //Метод для удаления пользователем своего диска из БД
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean isSuccessful = true;
        try {
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Disk.class);
            cr.add(Restrictions.eq("id", id));
            Disk disk = (Disk) cr.uniqueResult();
            session.delete(disk);
            tx.commit();
        } catch (HibernateException e) {
            isSuccessful = false;
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return isSuccessful;
    }
}
