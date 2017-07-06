package cn.edu.sjtu.se.walknshot.apimessages;

import cn.edu.sjtu.se.walknshot.apimessages.serializers.TokenDeserializer;
import cn.edu.sjtu.se.walknshot.apimessages.serializers.TokenSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TokenSerializer.class)
@JsonDeserialize(using = TokenDeserializer.class)
public class Token {
    private int userId;
    private String passphrase;

    public Token() {
        userId = 0;
        passphrase = null;
    }

    public Token(int userId, String passphrase) {
        this.userId = userId;
        this.passphrase = passphrase;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    @Override
    public String toString() {
        return getUserId() + ":" + getPassphrase();
    }

    public static Token fromString(String str) {
        String[] parts = str.split(":");
        if (parts.length != 2)
            return null;
        try {
            return new Token(Integer.parseInt(parts[0]), parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
