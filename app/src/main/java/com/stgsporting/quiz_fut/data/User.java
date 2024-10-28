package com.stgsporting.quiz_fut.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class User {

    private String passcode;
    private String name;
    private String imageLink;
    private int points = 0;
    private int stars;

    private Card card;
    private String cardIcon;
    private final List<Card> ownedCards;

    private Boolean current = false;


    public User() {
        ownedCards = new ArrayList<>();
    }

    public List<Card> getOwnedCards() {
        return ownedCards;
    }

    public String getDisplayName() {
        return Arrays.stream(name.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public String getFirstName() {
        return getDisplayName().split(" ")[0];
    }

    public Boolean isCurrent() {
        return current;
    }

    public void addOwnedCard(Card card) {
        this.ownedCards.add(card);
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Boolean hasCardIcon() {
        return cardIcon != null;
    }

    public String getCardIcon() {
        return cardIcon;
    }

    public void setCardIcon(String cardIcon) {
        this.cardIcon = cardIcon;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "passcode='" + passcode + '\'' +
                ", name='" + name + '\'' +
                ", cardIcon='" + cardIcon + '\'' +
                ", current=" + current +
                ", points=" + points +
                ", stars=" + stars +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}
