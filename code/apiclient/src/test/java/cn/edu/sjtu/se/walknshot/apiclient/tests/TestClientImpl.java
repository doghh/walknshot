package cn.edu.sjtu.se.walknshot.apiclient.tests;

import cn.edu.sjtu.se.walknshot.apiclient.CallbackAutoNetworkFailure;
import cn.edu.sjtu.se.walknshot.apiclient.Client;
import cn.edu.sjtu.se.walknshot.apiclient.ClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestClientImpl {
    private static Client client;
    public static void main(String[] args) {
        final ClientImpl clientImpl = ClientImpl.getInstance();
        clientImpl.setBaseUrl("http://localhost:8080");
        client = clientImpl;

        client.login(new CallbackAutoNetworkFailure() {
                @Override
                public void onFailure(Object arg) {
                    System.out.println(arg);
                }

                @Override
                public void onSuccess(Object arg) {
                    System.out.println(arg);
                    try {
                        System.out.println(new ObjectMapper().writeValueAsString(clientImpl.getToken()));
                    } catch (JsonProcessingException e) {
                        System.out.println("JSON error");
                    }
                    System.out.println(client.isLoggedIn());
                    testLoginValid();
                }
            },
            "anonymous", "secret");

        System.out.println(client.isLoggedIn());
    }

    private static void testLoginValid() {
        client.isLoginValid(new CallbackAutoNetworkFailure() {
                @Override
                public void onFailure(Object arg) {
                    // NOP
                }

                @Override
                public void onSuccess(Object arg) {
                    System.out.println("Login valid: " + arg);
                }
            });
    }
}
