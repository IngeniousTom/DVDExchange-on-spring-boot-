package com.dvdexchange.dao.impl;

import com.dvdexchange.model.Disk;
import com.dvdexchange.model.Takenitem;
import com.dvdexchange.model.User;
import com.dvdexchange.dao.TakenItemDAO;
import com.dvdexchange.utils.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TakenItemDAOHibernateImpl implements TakenItemDAO {

    public boolean addBorrowing(int iduser, int iddisk) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean isSuccessful = true;

        try {
            tx = session.beginTransaction();
            Criteria crTempUser = session.createCriteria(User.class);
            crTempUser.add(Restrictions.eq("id", iduser));
            User tempDiskUser = (User) crTempUser.uniqueResult();

            Criteria crBorrowedDisk = session.createCriteria(Disk.class);
            crBorrowedDisk.add(Restrictions.eq("id", iddisk));
            crBorrowedDisk.add(Restrictions.not(Restrictions.eq("host.id", iduser)));
            Disk borrowedDisk = (Disk) crBorrowedDisk.uniqueResult();

            if (borrowedDisk != null) {
                Takenitem takenitem = new Takenitem(iddisk, iduser, tempDiskUser, borrowedDisk);
                session.save(takenitem);
                tx.commit();
            } else {
                return false;
            }
        } catch (HibernateException e) {
            isSuccessful = false;
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return isSuccessful;
    }

    public int getUserIdByDiskId(int iddisk) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        int result = 0;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Takenitem.class);
            cr.add(Restrictions.eq("iddisk", iddisk));
            Takenitem takenitem = (Takenitem) cr.uniqueResult();
            result = takenitem.getIduser();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    public List<Takenitem> getAllDisksGivenByLoggedUser(int idUser) {
        /* Метод получает целевой всех дисков которые взяли у текущего пользователя, фильтруя множество всех взятых
         дисков через список id тех дисков, что принадлежат только текущему пользователю. Вся информация о дисках и кто
         их взял доступна, т.к метод вернет список объектов Takenitem */
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Takenitem> givenDisks = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            //Получаем список id всех дисков текущего пользователя
            Criteria crOwnDisks = session.createCriteria(Disk.class);
            crOwnDisks.add(Restrictions.eq("host.id", idUser));
            crOwnDisks.setProjection(Projections.property("id"));
            List<Integer> ownDisks = crOwnDisks.list();
            //Целевой список через полученный список
            Criteria crGivenDisks = session.createCriteria(Takenitem.class);
            crGivenDisks.add(Restrictions.in("iddisk", ownDisks));
            givenDisks = crGivenDisks.list();
            for (Takenitem givenDisk : givenDisks) {
                Hibernate.initialize(givenDisk.getDisk());
                Hibernate.initialize(givenDisk.getDisk().getHost());
                Hibernate.initialize(givenDisk.getTempUser());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return givenDisks;
    }

    public boolean deleteBorrowing(int iddisk) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean isSuccessful = true;
        try {
            tx = session.beginTransaction();
            String hql = "FROM Takenitem " +
                    "WHERE iddisk = :id_disk";
            Query query = session.createQuery(hql);
            query.setParameter("id_disk", iddisk);
            Takenitem takenitem = (Takenitem) query.uniqueResult();
            System.out.println(takenitem);
            session.delete(takenitem);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            isSuccessful = false;
        } finally {
            session.close();
        }
        return isSuccessful;
    }
}
