package com.dvdexchange.service.impl;

import com.dvdexchange.dao.TakenItemDAO;
import com.dvdexchange.dao.impl.TakenItemDAOHibernateImpl;
import com.dvdexchange.model.*;
import com.dvdexchange.service.UserService;
import com.dvdexchange.dao.UserDAO;
import com.dvdexchange.dao.impl.UserDAOHibernateImpl;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();

    private UserDAO userDAO = new UserDAOHibernateImpl();

    private TakenItemDAO takenItemDAO = new TakenItemDAOHibernateImpl();

    public void addUser(String name, String lastname, String patronym, String birthdate, String email, String password) {
        String encodedPassword = md5PasswordEncoder.encodePassword(password, null);
        
        userDAO.addUser(name, lastname, patronym, birthdate, email, encodedPassword);
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    public int getUserIdByEmail(String email) {
        return userDAO.getUserByEmail(email).getId();
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void changeUserPassword(String newPassword) {
        userDAO.changeUserPassword(newPassword);
    }

    public void deleteUserByEmail(String email) {
        userDAO.deleteUserByEmail(email);
    }

    public List<MyDisk> getUserDisksWithStatus(int userId) {
        List<Disk> listOfUserDisks = new ArrayList<Disk>(userDAO.getUserById(userId).getDisks());
        List<Takenitem> listOfGivenUserDisks = takenItemDAO.getAllDisksGivenByLoggedUser(userId);
        Set<Integer> setOfGivenDisksIds = new HashSet<Integer>();
        for (Takenitem givenUserDisk : listOfGivenUserDisks) {
            setOfGivenDisksIds.add(givenUserDisk.getDisk().getId());
        }

        System.out.println(setOfGivenDisksIds);

        List<MyDisk> myDisks = new ArrayList<MyDisk>();

        System.out.println();
        for (Disk userDisk : listOfUserDisks) {
            MyDisk myDisk = new MyDisk();
            myDisk.setId(userDisk.getId());
            myDisk.setName(userDisk.getName());
            if (setOfGivenDisksIds.contains(userDisk.getId())) {
                System.out.println("true");
                myDisk.setGiven(true);
            } else {
                System.out.println("false");
                myDisk.setGiven(false);
            }
            myDisks.add(myDisk);
        }
        System.out.println();
        return myDisks;
    }

    public List<MyDisk> getUserDisksWithStatus(String userEmail) {
        int userId = userDAO.getUserByEmail(userEmail).getId();
        return getUserDisksWithStatus(userId);
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Set<String> defaultRolesSet = new HashSet<String>();
        defaultRolesSet.add("ROLE_USER");

        User user = getUserByEmail(username);

        List<GrantedAuthority> authorities = buildUserAuthority(defaultRolesSet);
        //для отладки
        System.out.println(username);
        System.out.println();
        System.out.println(user);
        System.out.println();

        for (GrantedAuthority grantedAuthority : authorities) {
            System.out.println(grantedAuthority);
        }
        System.out.println();

        return buildUserForAuthentication(user, authorities);
    }

    /* Преобразует com.dvdexchange.model.User user в
     org.springframework.security.core.userdetails.User*/
    private org.springframework.security.core.userdetails.User buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();

        System.out.println(md5PasswordEncoder.encodePassword("Asya1254", null));
        System.out.println(md5PasswordEncoder.encodePassword("YaNeonKo#", null));
        System.out.println(md5PasswordEncoder.encodePassword("25dk125", null));
        System.out.println(md5PasswordEncoder.encodePassword("BatyaBoss777", null));
        System.out.println(md5PasswordEncoder.encodePassword("Anarchy*95*", null));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true,
                true, true, true, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(Set<String> userRolesSet) {

        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

        // Build user's authorities
        for (String userRole : userRolesSet) {
            setAuths.add(new SimpleGrantedAuthority(userRole));
        }
        List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);

        return Result;
    }

}
