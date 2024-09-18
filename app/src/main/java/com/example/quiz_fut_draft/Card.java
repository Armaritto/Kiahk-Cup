package com.example.quiz_fut_draft;

public class Card {

    private int ID;
    private int price;
    private String image;
    private String owner;
    private String rating;
    private String position;

    public Card(int ID, int price, String name, String owner, String rating, String position) {
        this.ID = ID;
        this.price = price;
        this.image = name;
        this.owner = owner;
        this.rating = rating;
        this.position = position;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
