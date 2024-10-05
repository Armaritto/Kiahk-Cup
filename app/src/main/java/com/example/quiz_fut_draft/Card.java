package com.example.quiz_fut_draft;

public class Card {

    private String ID;
    private int price;
    private String imageName;
    private String rating;
    private String position;
    private boolean owned;
    private String storageURL;

    public Card(String ID, int price, String imageName, String rating, String position, String storageURL) {
        this.ID = ID;
        this.price = price;
        this.imageName = imageName;
        this.rating = rating;
        this.position = position;
        this.storageURL = storageURL;
    }

    public Card(String storageURL) {
        this.storageURL = storageURL;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

    public String getImageLink() {
        return storageURL + imageName + "?alt=media";
    }
}
