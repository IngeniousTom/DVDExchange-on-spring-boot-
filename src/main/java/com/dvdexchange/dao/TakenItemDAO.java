package com.dvdexchange.dao;


import com.dvdexchange.model.Takenitem;

import java.util.List;

public interface TakenItemDAO {

    boolean addBorrowing(int iduser, int iddisk);

    int getUserIdByDiskId(int iddisk);

    public List<Takenitem> getAllDisksGivenByLoggedUser(int idUser);

    boolean deleteBorrowing(int iddisk);
}
