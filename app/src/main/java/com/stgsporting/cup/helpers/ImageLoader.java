package com.stgsporting.cup.helpers;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stgsporting.cup.R;

public class ImageLoader {

    private final Activity activity;

    public ImageLoader(Activity activity) {
        this.activity = activity;
    }

    public void loadImage(String imageUrl, ImageView imageView) {
        Picasso picasso = Picasso.with(activity);
        picasso.setIndicatorsEnabled(true);
        picasso.load(imageUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.emptyuser)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso picasso1 = Picasso.with(activity);
                        picasso1.setIndicatorsEnabled(true);
                        picasso1.load(imageUrl)
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.emptyuser)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(activity, "Could not fetch image", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    public void loadImage(Uri uri, ImageView imageView) {
        Picasso picasso = Picasso.with(activity);
        picasso.setIndicatorsEnabled(true);
        picasso.load(uri)
                .placeholder(R.drawable.loading)
                .error(R.drawable.emptyuser)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso picasso1 = Picasso.with(activity);
                        picasso1.setIndicatorsEnabled(true);
                        picasso1.load(uri)
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.emptyuser)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(activity, "Could not fetch image", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

}
