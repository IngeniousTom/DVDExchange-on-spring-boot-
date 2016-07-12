package com.dvdexchange.service;


import com.dvdexchange.model.MyDisk;
import com.dvdexchange.model.User;

import java.util.List;

public interface UserService {

    void addUser(String name, String lastname, String patronym, String birthdate, String email, String password);

    User getUserById(int id);

    User getUserByEmail(String email);

    int getUserIdByEmail(String email);

    List<User> getAllUsers();

    void changeUserPassword(String newPassword);

    void deleteUserByEmail(String email);

    List<MyDisk> getUserDisksWithStatus(int userId);

    List<MyDisk> getUserDisksWithStatus(String userEmail);
}
