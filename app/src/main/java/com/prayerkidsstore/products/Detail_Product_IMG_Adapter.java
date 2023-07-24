package com.prayerkidsstore.products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.prayerkidsstore.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Detail_Product_IMG_Adapter extends PagerAdapter {
    Context context;
    String[] images;
    LayoutInflater mlayoutInflater;
    public Detail_Product_IMG_Adapter(Context context, String[] images) {
        this.context = context;
        this.images = images;
        mlayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mlayoutInflater.inflate(R.layout.item_image_detail_product, container, false);

        // referencing the image view from the item.xml file
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView imageView = (ImageView) itemView.findViewById(R.id.bannerimage_detail);

        // setting the image in the imageView
        Log.d("Image_Link",images[position]);
        Picasso.get()
                .load(images[position])
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(imageView);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
