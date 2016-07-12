package com.dvdexchange.model;


import com.fasterxml.jackson.annotation.JsonView;

public class Takenitem {
    private int iddisk;
    private int iduser;
    @JsonView(Views.Disk.class)
    private User tempUser;
    @JsonView(Views.Disk.class)
    private Disk disk;

    public Takenitem() {
    }

    public Takenitem(int iddisk, int iduser, User tempUser, Disk disk) {
        this.iddisk = iddisk;
        this.iduser = iduser;
        this.tempUser = tempUser;
        this.disk = disk;
    }

    public int getIddisk() {
        return iddisk;
    }

    public void setIddisk(int iddisk) {
        this.iddisk = iddisk;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Takenitem takenitem = (Takenitem) o;

        if (iddisk != takenitem.iddisk) return false;
        if (iduser != takenitem.iduser) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = iddisk;
        result = 31 * result + iduser;
        return result;
    }

    public User getTempUser() {
        return tempUser;
    }

    public void setTempUser(User tempUser) {
        this.tempUser = tempUser;
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }

    @Override
    public String toString() {
        return "Takenitem{" +
                "iddisk=" + iddisk +
                ", iduser=" + iduser +
                ", tempUser=" + tempUser +
                ", disk=" + disk +
                '}';
    }
}
