package com.example.quiz_fut_draft;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ImageProcessor {
    private Context context;

    public ImageProcessor(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        this.context = context;
    }

    public Uri compressImage(Uri uri) {
        try(InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream); // 80 is the quality percentage

            byte[] compressedImageBytes = byteArrayOutputStream.toByteArray();

            return saveImageInMemory(compressedImageBytes);
        } catch (Exception ignored) {return null;}
    }

    private Uri saveImageInMemory(byte[] imageBytes) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "compressed_image_" + System.currentTimeMillis() + ".jpg");
        Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if(imageUri == null) return null;

        try(OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
            if (outputStream == null) return null;
            outputStream.write(imageBytes);
        } catch (Exception ignored) {}

        return imageUri;
    }

    public CompletableFuture<Uri> removeBackground(Uri uri) {
        return CompletableFuture.supplyAsync(() -> {
            Uri image = removeBackgroundSync(uri);
            if (image == null) {
                return uri;
            }
            return image;
        });
    }

    private Uri removeBackgroundSync(Uri uri) {
        HttpURLConnection connection = null;
        try {
            // Create connection
            URL url = new URL("https://rembg.fadisarwat.dev/process");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer Qpy7Tap6v3UVIbWZ1kVFvM0Ml3nRBoO2");
            connection.setDoOutput(true);

            String boundary = UUID.randomUUID().toString();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Open the output stream
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            writeImageToRequest(uri, wr, boundary);

            wr.writeBytes("--" + boundary + "--\r\n");
            wr.flush();
            wr.close();

            connection.setConnectTimeout(60000);

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > -1) {
                    baos.write(buffer, 0, len);
                }
                baos.flush();

                // Save the image from response to a temporary file and get its Uri
                byte[] imageBytes = baos.toByteArray();

                inputStream.close();

                return saveImageInMemory(imageBytes);
            } else {
                System.out.println("Response code: " + connection.getResponseCode());
                System.out.println("Response message: " + connection.getResponseMessage());
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void writeImageToRequest(Uri uri, DataOutputStream wr, String boundary) throws IOException {
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        // Set up the file part
        wr.writeBytes(twoHyphens + boundary + lineEnd);
        wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpeg\"" + lineEnd);
        wr.writeBytes("Content-Type: image/jpeg" + lineEnd);
        wr.writeBytes(lineEnd);

        // Read the image from the Uri and write it to the output stream
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            wr.write(buffer, 0, bytesRead);
        }
        inputStream.close();

        wr.writeBytes(lineEnd);
    }
}
