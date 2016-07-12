package com.dvdexchange.service.impl;


import com.dvdexchange.dao.UserDAO;
import com.dvdexchange.dao.impl.UserDAOHibernateImpl;
import com.dvdexchange.model.Disk;
import com.dvdexchange.model.User;
import com.dvdexchange.service.DiskService;
import com.dvdexchange.dao.DiskDAO;
import com.dvdexchange.dao.impl.DiskDAOHibernateImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiskServiceImpl implements DiskService {

    private UserDAO userDAO = new UserDAOHibernateImpl();
    private DiskDAO diskDAO = new DiskDAOHibernateImpl();

    public boolean addDisk(String name, int idHost) {
        return diskDAO.addDisk(name, idHost);
    }

    public List<Disk> getAllFreeDisksForCurrentUser(String currentUserEmail) {
        int currentUserId = userDAO.getUserByEmail(currentUserEmail).getId();
        return diskDAO.getAllFreeDisksForCurrentUser(currentUserId);
    }

    public List<Disk> getAllFreeDisks() {
        return diskDAO.getAllFreeDisks();
    }

    public List<Disk> getAllDisksTakenByLoggedUser(int idUser) {
        return diskDAO.getAllDisksTakenByLoggedUser(idUser);
    }

    public List<Disk> getAllDisksTakenByLoggedUser(String currentUserEmail) {
        int currentUserId = userDAO.getUserByEmail(currentUserEmail).getId();
        return getAllDisksTakenByLoggedUser(currentUserId);
    }

    public boolean editDiskInformationById(int id, String newDiskInfo) {
        return diskDAO.editDiskInformationById(id, newDiskInfo);
    }

    public boolean deleteDiskById(int id) {
        return diskDAO.deleteDiskById(id);
    }

    public int deleteDisksByIdList(List<Integer> idList) {
        int failCount = 0;
        for (Integer diskId : idList) {
            if (diskDAO.deleteDiskById(diskId) == false) {
                failCount++;
            }
        }
        return failCount;
    }
}
