package com.stgsporting.cup.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.*;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ImageProcessor {
    private final Context context;

    public ImageProcessor(Context context) {
        this.context = context;
    }

    public Uri compressImage(Uri uri) {
        try(InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            int size = 800;
            int newWidth = (int) (bitmap.getWidth() * ((double) size / bitmap.getHeight()));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, size, true);
            // Crop image with width > 800
            if (newWidth > size) {
                int x = (newWidth - size) / 2;
                resizedBitmap = Bitmap.createBitmap(resizedBitmap, x, 0, size, size);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 85, byteArrayOutputStream);

            byte[] compressedImageBytes = byteArrayOutputStream.toByteArray();

            return saveImageInMemory(compressedImageBytes, "png");
        } catch (Exception ignored) {return null;}
    }

    public void deleteImage(Uri uri) {
        context.getContentResolver().delete(uri, null, null);
    }

    public Uri saveImageInMemory(byte[] imageBytes, String type) {
        // Determine the MIME type based on the file extension (e.g., jpg, png)
        String mimeType = getMimeType(type.toLowerCase(Locale.ROOT));

        if (mimeType == null) {
            return null; // Invalid file type
        }

        // Set up content values for the image file
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "compressed_image_" + System.currentTimeMillis() + "." + type);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/KiahkCupImages");
        }

        // Insert the image to MediaStore
        Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri == null) return null;

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
            if (outputStream == null) return null;

            outputStream.write(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return imageUri;
    }

    private String getMimeType(String type) {
        switch (type) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            default:
                return null; // Unsupported type
        }
    }

    public CompletableFuture<Uri> removeBackground(Uri imageUri) {
        return CompletableFuture.supplyAsync(() -> {
            Uri image = removeBackgroundSync(imageUri);

            return image == null ? imageUri : image;
        });
    }

    private Uri removeBackgroundSync(Uri uri) {
        Http request = Http.post(Uri.parse("https://rembg.fadisarwat.dev/process"))
                .addHeader("Authorization", "Bearer Qpy7Tap6v3UVIbWZ1kVFvM0Ml3nRBoO2")
                .setConnectTimeout(60000);
        try {
            request.addImage("file", context.getContentResolver().openInputStream(uri));
        } catch (IOException e) {
            return null;
        }

        Http.Response response = request.send();

        if (response.getCode() == 200) {
            InputStream inputStream = response.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = inputStream.read(buffer)) > -1) {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
                byte[] imageBytes = baos.toByteArray();
                inputStream.close();

                deleteImage(uri);
                return saveImageInMemory(imageBytes, "png");
            } catch (IOException ignored) {}
            finally {
                request.disconnect();
            }
        }

        request.disconnect();

        return null;
    }

}
