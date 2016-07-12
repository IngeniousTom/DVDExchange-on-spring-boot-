package com.dvdexchange.dao;

import com.dvdexchange.model.Disk;

import java.util.List;


public interface DiskDAO {

    boolean addDisk(String name, int idHost);

    List<Disk> getAllFreeDisks();

    List<Disk> getAllFreeDisksForCurrentUser(int currentUserId);

    List<Disk> getAllDisksTakenByLoggedUser(int currentUserId);

    boolean editDiskInformationById(int id, String newDiskInfo);

    boolean deleteDiskById(int id);
}
