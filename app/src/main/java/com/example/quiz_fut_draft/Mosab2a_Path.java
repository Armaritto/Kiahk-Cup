package com.example.quiz_fut_draft;

public class Mosab2a_Path {
    public static String getPath(String grade){
        switch (grade) {
            case "1":
                return "/elmilad25/Mosab2at/J1";
            case "2":
                return "/elmilad25/Mosab2at/J2";
            case "3":
                return "/elmilad25/Mosab2at/J3";
            case "4":
                return "/elmilad25/Mosab2at/J4";
            case "5":
                return "/elmilad25/Mosab2at/J5";
            case "6":
                return "/elmilad25/Mosab2at/J6";
            default:
                return "/elmilad25/Mosab2at/";
        }
    }
}
