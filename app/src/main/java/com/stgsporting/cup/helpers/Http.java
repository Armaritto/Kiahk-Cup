package com.stgsporting.cup.helpers;

import android.net.Uri;
import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Http {
    private HttpURLConnection connection;

    public static final String URL = "https://cup.stgsporting.com/api";

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
        addHeader("Accept", "application/json; charset=utf-8");
        addHeader("Content-Type", "application/json; charset=utf-8");
        return this;
    }

    public Http addHeader(String key, String value) {
        connection.setRequestProperty(key, value);
        return this;
    }

//    private Object encodeData(Object data) {
//        if(data instanceof String) {
//            try {
//                return URLEncoder.encode((String) data, "utf-8");
//            } catch (UnsupportedEncodingException e) {
//                return data;
//            }
//        }
//
//        if (data instanceof JSONObject) {
//            JSONObject dataObj = (JSONObject) data;
//            dataObj.keys().forEachRemaining((key) -> {
//                try {
//                    Object value = dataObj.get(key);
//                    dataObj.put(key, encodeData(value));
//                } catch (JSONException ignored) {}
//            });
//
//            return dataObj;
//        }
//
//        if(data instanceof JSONArray) {
//            JSONArray array = (JSONArray) data;
//            for (int i = 0; i < array.length(); i++) {
//                try {
//                    array.put(i, encodeData(array.get(i)));
//                } catch (JSONException ignored) {}
//            }
//
//            return array;
//        }
//
//        return data;
//    }

    public Http addData(JSONObject data) {

        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));

            wr.write(data.toString());
            wr.flush();
            wr.close();
        } catch (Exception ignored) {}
        return this;
    }

    public void addImage(String name, InputStream inputStream) throws IOException {
        String boundary = UUID.randomUUID().toString(); // Unique boundary for multipart
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        // Set request properties for multipart form data
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setDoOutput(true); // Ensure the connection allows output

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {

            // Set up the file part
            wr.writeBytes(twoHyphens + boundary + lineEnd);
            wr.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"image.jpeg\"" + lineEnd);
            wr.writeBytes("Content-Type: image/jpeg" + lineEnd);
            wr.writeBytes(lineEnd);

            // Write the image bytes from InputStream to the DataOutputStream
            byte[] buffer = new byte[4096]; // Use a larger buffer for efficiency
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                wr.write(buffer, 0, bytesRead);
            }

            inputStream.close(); // Close input stream after reading

            wr.writeBytes(lineEnd); // End of file part

            // Final boundary
            wr.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Ensure everything is flushed out
            wr.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to upload the image", e);
        }
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
