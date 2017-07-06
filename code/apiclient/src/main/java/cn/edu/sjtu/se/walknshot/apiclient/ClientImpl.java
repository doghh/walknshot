package cn.edu.sjtu.se.walknshot.apiclient;

import cn.edu.sjtu.se.walknshot.apimessages.RegisterResponse;
import cn.edu.sjtu.se.walknshot.apimessages.Token;
import cn.edu.sjtu.se.walknshot.apimessages.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class ClientImpl implements Client {
    private Token token;
    private String baseUrl = "http://localhost:8080";

    private static ClientImpl singleton = new ClientImpl();

    public static ClientImpl getInstance() {
        return singleton;
    }

    private ClientImpl() {
        // NOP
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void register(final Callback callback, String username, String password) {
        if (!Util.validUsername(username) || !Util.validPassword(password)) {
            callback.onFailure(1);
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/register")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                RegisterResponse rr = new ObjectMapper().readValue(response.body().string(), RegisterResponse.class);
                if (rr.getSuccess()) {
                    callback.onSuccess(rr.getUserId());
                } else {
                    callback.onFailure(2);
                }
            }
        });
    }

    @Override
    public void login(final Callback callback, String username, String password) {
        if (!Util.validUsername(username) || !Util.validPassword(password)) {
            callback.onFailure(1);
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/login")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Token token = new ObjectMapper().readValue(response.body().string(), Token.class);
                if (token.getUserId() == 0) {
                    callback.onFailure(1);
                    return;
                }
                setToken(token);
                callback.onSuccess(token.getUserId());
            }
        });
    }

    @Override
    public void isLoginValid(final Callback callback) {
        if (!isLoggedIn()) {
            callback.onSuccess(false);
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("token", getToken().toString())
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/validate/token")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Boolean r = new ObjectMapper().readValue(response.body().string(), Boolean.class);
                callback.onSuccess(r == null ? false : r);
            }
        });
    }

    @Override
    public void logout(Callback callback) {
        setToken(null);
        callback.onSuccess(true);
    }

    @Override
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    @Override
    public Integer getUserId() {
        return 0;
    }
}
