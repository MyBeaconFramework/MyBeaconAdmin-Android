package com.brokoli.mybeaconadmin.data;

import com.j256.ormlite.field.DatabaseField;

public class User {
    public static final String USERNAME = "username";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String username;

    @DatabaseField
    private String description;

    public User(){

    }

    public User(String username, String description){
        this.username = username;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
