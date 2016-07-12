package com.dvdexchange.dao;


import com.dvdexchange.model.User;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import java.util.List;

public interface UserDAO {

    void addUser(String name, String lastname, String patronym, String birthdate, String email, String password);

    User getUserById(int id);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    void changeUserPassword(String newPassword);

    void deleteUserByEmail(String email);

}
