package com.stgsporting.quiz_fut.data;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

public class TextColor {
    public static void setTextColorBasedOnImage(Bitmap bitmap, TextView textView) {
        int luminance = calculateLuminance(bitmap);
        if (luminance < 110) {
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setTextColor(Color.BLACK);
        }
    }
    private static int calculateLuminance(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        long totalLuminance = 0;
        for (int pixel : pixels) {
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            totalLuminance += 0.2126 * r + 0.7152 * g + 0.0722 * b;
        }

        return (int) (totalLuminance / size);
    }
    public static void setColor(ImageView icon, TextView name, TextView position, TextView rating) {
        if (icon.getDrawable() == null) {
            return;
        }

        Bitmap b = ((BitmapDrawable) icon.getDrawable()).getBitmap();
        TextColor.setTextColorBasedOnImage(b, name);
        TextColor.setTextColorBasedOnImage(b, position);
        TextColor.setTextColorBasedOnImage(b, rating);
    }
}
