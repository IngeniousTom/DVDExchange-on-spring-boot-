package com.dvdexchange.service;


import com.dvdexchange.model.Disk;

import java.util.List;

public interface DiskService {

    boolean addDisk(String name, int idHost);

    List<Disk> getAllFreeDisks();

    List<Disk> getAllFreeDisksForCurrentUser(String currentUserEmail);

    List<Disk> getAllDisksTakenByLoggedUser(int idUser);

    List<Disk> getAllDisksTakenByLoggedUser(String currentUserEmail);

    boolean editDiskInformationById(int id, String newDiskInfo);

    boolean deleteDiskById(int id);

    int deleteDisksByIdList(List<Integer> idList);
}
