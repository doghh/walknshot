package cn.edu.sjtu.se.walknshot.apiclient.tests;

import cn.edu.sjtu.se.walknshot.apiclient.CallbackAutoNetworkFailure;
import cn.edu.sjtu.se.walknshot.apiclient.Client;
import cn.edu.sjtu.se.walknshot.apiclient.ClientImpl;
import cn.edu.sjtu.se.walknshot.apimessages.PictureEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestClientImpl {
    private static Client client;
    private static long spotId;
    private static PictureEntry picture;

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
                    testAddSpot();
                }
            });
    }

    private static void testAddSpot() {
        client.addSpot(new CallbackAutoNetworkFailure() {
            @Override
            public void onFailure(Object arg) {
                // NOP
            }

            @Override
            public void onSuccess(Object arg) {
                spotId = (long) arg;
                System.out.println("Spot ID: " + spotId);
                testUploadPicture();
            }
        }, 66, 111);
    }

    private static void testUploadPicture() {
        client.uploadPicture(new CallbackAutoNetworkFailure() {
            @Override
            public void onFailure(Object arg) {
                // NOP
            }

            @Override
            public void onSuccess(Object arg) {
                System.out.println("Picture: " + arg);
                picture = (PictureEntry) arg;
                testDownloadPicture();
            }
        }, "Hello, world\n".getBytes());
    }

    private static void testDownloadPicture() {
        client.downloadPicture(new CallbackAutoNetworkFailure() {
            @Override
            public void onFailure(Object arg) {
                // NOP
            }

            @Override
            public void onSuccess(Object arg) {
                System.out.println(new String((byte[]) arg));
            }
        }, picture.getStorageName());
    }
}
