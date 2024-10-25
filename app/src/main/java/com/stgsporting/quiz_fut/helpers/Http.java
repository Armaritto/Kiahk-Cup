package com.stgsporting.quiz_fut.helpers;

import android.net.Uri;
import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Http {
    private HttpURLConnection connection;

    public static final String URL = "https://quizzes-fc.stgsporting.com/api";

    public Http(Uri uri, String method) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
        }catch (Exception ignored) {}
    }

    public static Http get(Uri uri, Map<String, String> params) {
        Uri.Builder builder = new Uri.Builder().scheme(uri.getScheme())
                .authority(uri.getAuthority())
                .path(uri.getPath());
        params.forEach(builder::appendQueryParameter);

        return request(builder.build(), "GET");
    }

    public static Http get(Uri uri) {
        return request(uri, "GET");
    }

    public static Http post(Uri uri) {
        Http http = request(uri, "POST");

        http.connection.setDoOutput(true);

        return http;
    }

    public static Http request(Uri uri, String method) {
        return new Http(uri, method);
    }

    public Http setConnectTimeout(int timeout) {
        connection.setConnectTimeout(timeout);
        return this;
    }

    public Http expectsJson() {
        addHeader("Accept", "application/json");
        addHeader("Content-Type", "application/json");
        return this;
    }

    public Http addHeader(String key, String value) {
        connection.setRequestProperty(key, value);
        return this;
    }

    public Http addData(JSONObject data) {
        try {
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            wr.writeBytes(data.toString());
            wr.flush();
            wr.close();
        } catch (Exception ignored) {}
        return this;
    }

    public void addImage(String name, InputStream inputStream) throws IOException {
        String boundary = UUID.randomUUID().toString();
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        // Set up the file part
        wr.writeBytes(twoHyphens + boundary + lineEnd);
        wr.writeBytes("Content-Disposition: form-data; name=\"" + name +"\"; filename=\"image.jpeg\"" + lineEnd);
        wr.writeBytes("Content-Type: image/jpeg" + lineEnd);
        wr.writeBytes(lineEnd);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            wr.write(buffer, 0, bytesRead);
        }
        inputStream.close();

        wr.writeBytes(lineEnd);

        wr.writeBytes("--" + boundary + "--\r\n");
        wr.flush();
        wr.close();
    }

    public CompletableFuture<Response> sendAsync() {
        return CompletableFuture.supplyAsync(this::send);
    }

    public Response send() {
        try {
            InputStream inputStream = connection.getInputStream();

            return new Response(connection.getResponseCode(), connection.getResponseMessage(), inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(-2, e.getMessage(), null);
        } finally {
            connection.disconnect();
        }
    }

    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public static class Response {
        private final int code;
        private final String message;
        private final InputStream inputStream;

        public Response(int code, String message, InputStream inputStream) {
            this.code = code;
            this.message = message;
            this.inputStream = inputStream;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public JSONObject getJson() {
            InputStream inputStream = getInputStream();

            if (inputStream == null) {
                return new JSONObject();
            }

            byte[] buffer = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            try {
                while ((len = inputStream.read(buffer)) > -1) {
                    sb.append(new String(buffer, 0, len));
                }
                inputStream.close();
            } catch (IOException ignored) {}

            try {
                return new JSONObject(sb.toString());
            }catch (JSONException e) {
                return new JSONObject();
            }
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }
}
