package com.dvdexchange.service.impl;

import com.dvdexchange.dao.DiskDAO;
import com.dvdexchange.dao.UserDAO;
import com.dvdexchange.dao.impl.DiskDAOHibernateImpl;
import com.dvdexchange.dao.impl.UserDAOHibernateImpl;
import com.dvdexchange.model.Disk;
import com.dvdexchange.model.Takenitem;
import com.dvdexchange.service.TakenItemService;
import com.dvdexchange.dao.TakenItemDAO;
import com.dvdexchange.dao.impl.TakenItemDAOHibernateImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TakenItemServiceImpl implements TakenItemService {

    private UserDAO userDAO = new UserDAOHibernateImpl();
    private DiskDAO diskDAO = new DiskDAOHibernateImpl();
    private TakenItemDAO takenItemDAO = new TakenItemDAOHibernateImpl();
    public static final int maxBorrowedDiskCount = 5;

    public byte addBorrowing(String currentUserEmail, int diskID) {
        int currentUserId = userDAO.getUserByEmail(currentUserEmail).getId();
        List<Disk> borrowedDisks = diskDAO.getAllDisksTakenByLoggedUser(currentUserId);
        int borrowedCount = (borrowedDisks != null) ? borrowedDisks.size() : 0;

        if (borrowedCount < maxBorrowedDiskCount) {
            if (takenItemDAO.addBorrowing(currentUserId, diskID)) {
                return 0;       //Диск успешно взят.
            } else {
                return 1;   //Диск не взят, т.к. уже занят или произошел сбой
            }
        }
        return 2;         //Отказ, т.к. превышен лимит
    }

    public int getUserIdByDiskId(int iddisk) {
        return takenItemDAO.getUserIdByDiskId(iddisk);
    }

    public List<Takenitem> getAllDisksGivenByLoggedUser(int idUser) {
        return takenItemDAO.getAllDisksGivenByLoggedUser(idUser);
    }

    public List<Takenitem> getAllDisksGivenByLoggedUser(String userEmail) {
        int currentUserId = userDAO.getUserByEmail(userEmail).getId();
        return getAllDisksGivenByLoggedUser(currentUserId);
    }

    public int deleteBorrowing(List<Integer> disksIds) {
        int failCount = 0;
        for (Integer diskId : disksIds) {
            if (takenItemDAO.deleteBorrowing(diskId) == false) {
                failCount++;
            }
        }
        return failCount;
    }

}
