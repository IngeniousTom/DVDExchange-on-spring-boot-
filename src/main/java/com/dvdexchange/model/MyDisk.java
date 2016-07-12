package com.dvdexchange.model;


import com.fasterxml.jackson.annotation.JsonView;

public class MyDisk {
    @JsonView(Views.Disk.class)
    private int id;

    @JsonView(Views.Disk.class)
    private String name;

    @JsonView(Views.Disk.class)
    private boolean given;

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

    public boolean isGiven() {
        return given;
    }

    public void setGiven(boolean given) {
        this.given = given;
    }

    public MyDisk(int id, String name, boolean given) {
        this.id = id;
        this.name = name;
        this.given = given;
    }

    public MyDisk() {
    }
}
