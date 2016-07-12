package com.dvdexchange.model;

import com.fasterxml.jackson.annotation.JsonView;

import java.sql.Date;
import java.util.Set;


public class User {
    @JsonView(Views.Disk.class)
    private int id;
    @JsonView(Views.Disk.class)
    private String name;
    @JsonView(Views.Disk.class)
    private String lastname;
    @JsonView(Views.Disk.class)
    private String patronym;
    private Date birthdate;
    private String email;
    private String password;
    private Set<Disk> disks;
    private Set<Takenitem> takenDisks;

    public User() {
    }

    public User(String name, String lastname, String patronym, Date birthdate, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.patronym = patronym;
        this.birthdate = birthdate;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPatronym() {
        return patronym;
    }

    public void setPatronym(String patronym) {
        this.patronym = patronym;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (lastname != null ? !lastname.equals(user.lastname) : user.lastname != null) return false;
        if (patronym != null ? !patronym.equals(user.patronym) : user.patronym != null) return false;
        if (birthdate != null ? !birthdate.equals(user.birthdate) : user.birthdate != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (patronym != null ? patronym.hashCode() : 0);
        result = 31 * result + (birthdate != null ? birthdate.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    public Set<Disk> getDisks() {
        return disks;
    }

    public void setDisks(Set<Disk> disks) {
        this.disks = disks;
    }

    public Set<Takenitem> getTakenDisks() {
        return takenDisks;
    }

    public void setTakenDisks(Set<Takenitem> takenDisks) {
        this.takenDisks = takenDisks;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + "\t" +
                ", lastname='" + lastname + "\t" +
                ", patronym='" + patronym + "\t" +
                ", birthdate=" + birthdate + "\t" +
                ", email='" + email + "\t" +
                ", password='" + password +
                '}';
    }
}
