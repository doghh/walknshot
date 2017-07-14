package cn.edu.sjtu.se.walknshot.apimessages;

import java.util.regex.Pattern;

public class Util {
    public static boolean validUsername(String username) {
        if (username == null)
            return false;
        return Pattern.matches("([a-z][a-z0-9_]{3,30})", username);
    }

    public static boolean validPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean validLatLng(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude < 180;
    }
}
