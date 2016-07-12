package com.dvdexchange.model;


import com.fasterxml.jackson.annotation.JsonView;

public class Disk {
    @JsonView(Views.Disk.class)
    private int id;
    @JsonView(Views.Disk.class)
    private String name;
    @JsonView(Views.Disk.class)
    private User host;

    public Disk() {
    }

    public Disk(String name, User host) {
        this.name = name;
        this.host = host;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Disk disk = (Disk) o;

        if (id != disk.id) return false;
        if (name != null ? !name.equals(disk.name) : disk.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "Disk{" +
                " id=" + id + "\t" +
                ", name='" + name + "\t" +
                ", host=" + host
                + '}';
    }
}
