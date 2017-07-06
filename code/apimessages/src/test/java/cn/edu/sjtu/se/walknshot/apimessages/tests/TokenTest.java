package cn.edu.sjtu.se.walknshot.apimessages.tests;

import cn.edu.sjtu.se.walknshot.apimessages.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TokenTest {
    public static void main(String[] args) {
        Token token = new Token(42, "passphrase");
        ObjectMapper mapper = new ObjectMapper();
        String s = null;
        try {
            s = mapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(s);
        Token token2 = null;
        try {
            token2 = mapper.readValue(s, Token.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(token2.getUserId());
        System.out.println(token2.getPassphrase());
    }
}
