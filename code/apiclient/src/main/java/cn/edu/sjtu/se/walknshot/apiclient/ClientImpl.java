package cn.edu.sjtu.se.walknshot.apiclient;

import cn.edu.sjtu.se.walknshot.apimessages.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ClientImpl implements Client {
    private Token token;
    private String baseUrl = "http://dorm.lvzheng.space:8080";
    private long lastSpot = 0;

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
    public void addSpot(final Callback callback, double latitude, double longitude) {
        if (!isLoggedIn() || !Util.validLatLng(latitude, longitude)) {
            callback.onFailure(null);
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("token", getToken().toString())
                .add("latitude", Double.toString(latitude))
                .add("longitude", Double.toString(longitude))
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/spot/add")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Long r = new ObjectMapper().readValue(response.body().string(), Long.class);
                if (r != null) {
                    lastSpot = r;
                    callback.onSuccess(r);
                } else {
                    callback.onFailure(null);
                }
            }
        });
    }

    @Override
    public void uploadPicture(final Callback callback, byte[] file) {
        if (!isLoggedIn() || lastSpot == 0) {
            callback.onFailure(null);
            return;
        }

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", getToken().toString())
                .addFormDataPart("spot", Long.toString(lastSpot))
                .addFormDataPart("file", "picture", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/picture/upload")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    PictureEntry r = new ObjectMapper().readValue(response.body().string(), PictureEntry.class);
                    if (r != null)
                        callback.onSuccess(r);
                    else
                        callback.onFailure(null);
                } catch (JsonMappingException e) {
                    callback.onFailure(null);
                }
            }
        });
    }

    @Override
    public void downloadPicture(final Callback callback, String storageName) {
        RequestBody body = new FormBody.Builder()
                .add("name", storageName)
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/static/picture")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] bytes = response.body().bytes();
                if (bytes.length == 0)
                    callback.onFailure(null);
                else
                    callback.onSuccess(bytes);
            }
        });
    }

    @Override
    public void uploadPGroup(final Callback callback, List<byte[]> pictures) {
        if (!isLoggedIn()) {
            callback.onFailure(null);
            return;
        }

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("token", getToken().toString());

        for (byte[] pic : pictures)
            builder.addFormDataPart("file[]", "picture", RequestBody.create(MediaType.parse("application/octet-stream"), pic));

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/pgroup/upload")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    PGroupDetails r = new ObjectMapper().readValue(response.body().string(), PGroupDetails.class);
                    if (r != null)
                        callback.onSuccess(r);
                    else
                        callback.onFailure(null);
                } catch (JsonMappingException e) {
                    callback.onFailure(null);
                }
            }
        });
    }

    @Override
    public void addComment(final Callback callback, int pgroupId, String content) {
        if (!isLoggedIn() || content == null) {
            callback.onFailure(null);
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("token", getToken().toString())
                .add("pgroupId", Integer.toString(pgroupId))
                .add("content", content)
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/comment/add")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommentEntry r = new ObjectMapper().readValue(response.body().string(), CommentEntry.class);
                if (r != null) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(null);
                }
            }
        });
    }

    @Override
    public void getPGroupDetails(final Callback callback, int pgroupId) {
        RequestBody body = new FormBody.Builder()
                .add("pgroupId", Integer.toString(pgroupId))
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/pgroup/get")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                PGroupDetails r = new ObjectMapper().readValue(response.body().string(), PGroupDetails.class);
                if (r != null) {
                    callback.onSuccess(r);
                } else {
                    callback.onFailure(null);
                }
            }
        });
    }

    @Override
    public void getPGroups(final Callback callback, boolean everyone) {
        if (!isLoggedIn()) {
            callback.onFailure(null);
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("token", getToken().toString())
                .add("everyone", Integer.toString(everyone ? 1 : 0))
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/pgroup/list")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new CallbackForward(callback) {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List r = new ObjectMapper().readValue(response.body().string(), List.class);
                if (r != null) {
                    callback.onSuccess(r);
                } else {
                    callback.onFailure(null);
                }
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
