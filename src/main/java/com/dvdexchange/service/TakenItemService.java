package com.dvdexchange.service;


import com.dvdexchange.model.Disk;
import com.dvdexchange.model.MyDisk;
import com.dvdexchange.model.Takenitem;

import java.util.List;

public interface TakenItemService {

    byte addBorrowing(String currentUserEmail, int diskID);

    int getUserIdByDiskId(int iddisk);

    public List<Takenitem> getAllDisksGivenByLoggedUser(int idUser);

    public List<Takenitem> getAllDisksGivenByLoggedUser(String userEmail);

    int deleteBorrowing(List<Integer> disksIds);

}
