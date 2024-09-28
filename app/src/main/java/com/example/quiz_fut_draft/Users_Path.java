package com.example.quiz_fut_draft;

public class Users_Path {
    public static String getPath(String grade){
        switch (grade) {
            case "1":
                return "/elmilad25/Users/J1";
            case "2":
                return "/elmilad25/Users/J2";
            case "3":
                return "/elmilad25/Users/J3";
            case "4":
                return "/elmilad25/Users/J4";
            case "5":
                return "/elmilad25/Users/J5";
            case "6":
                return "/elmilad25/Users/J6";
            default:
                return "/elmilad25/Users/";
        }
    }
}
