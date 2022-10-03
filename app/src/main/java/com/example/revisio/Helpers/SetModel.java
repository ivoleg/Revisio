package com.example.revisio.Helpers;
public class SetModel {

    private int id;
    private String name;
    private String imageUrl;
    private boolean isExpanded;
    private int highscore;

    public SetModel(int id, String name, String imageUrl, int highscore) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isExpanded = false;
        this.highscore = highscore;
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

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    @Override
    public String toString() {
        return "SetModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
