package com.example.quiz_fut_draft;

public class Rating_Path {
    public static String getPath(String grade){
        switch (grade) {
            case "1":
                return "/elmilad25/RatingPrice/J1";
            case "2":
                return "/elmilad25/RatingPrice/J2";
            case "3":
                return "/elmilad25/RatingPrice/J3";
            case "4":
                return "/elmilad25/RatingPrice/J4";
            case "5":
                return "/elmilad25/RatingPrice/J5";
            case "6":
                return "/elmilad25/RatingPrice/J6";
            default:
                return "/elmilad25/RatingPrice/";
        }
    }
}
