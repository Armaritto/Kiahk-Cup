package com.example.quiz_fut_draft;

public class Card {

    private int ID;
    private int price;
    private String image;
    private String rating;
    private String position;
    private boolean owned = false;

    public Card(int ID, int price, String name, String rating, String position) {
        this.ID = ID;
        this.price = price;
        this.image = name;
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

    public void own() {
        owned = true;
    }
}
