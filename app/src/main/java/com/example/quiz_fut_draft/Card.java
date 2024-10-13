package com.example.quiz_fut_draft;

public class Card {

    private String ID;
    private int price;
    private String imageLink;
    private String rating;
    private String position;
    private boolean owned;
    private String imagePath;

    public Card(String ID, int price, String imageLink, String rating, String position) {
        this.ID = ID;
        this.price = price;
        this.imageLink = imageLink;
        this.rating = rating;
        this.position = position;
    }

    public Card() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
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

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
