package com.upd.contraplus2020.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.upd.contraplus2020.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public String[] slide_Desc = {
            "A Community Interactions Application",
            "Enable your location-based services and bluetooth signal for community interactions",
            "No additional personal information is needed when using the ConTra + Application",
            "Let us begin your safe journey amidst this crisis along with ConTra + Application"
    };

    public String[] slide_Heading = {
            "Welcome to ConTra +",
            "Contact Tracing",
            "Privacy Protection",
            "Get Started"
    };

    public int[] slide_Images = {
            R.drawable.appicon,
            R.drawable.logo_medical_app,
            R.drawable.logo_medical_report,
            R.drawable.logo_medical_mask
    };

    @Override
    public int getCount() {
        return slide_Heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    public Object instantiateItem(ViewGroup viewGroup, int position){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_slide_layout, viewGroup, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDesc = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_Images[position]);
        slideHeading.setText(slide_Heading[position]);
        slideDesc.setText(slide_Desc[position]);

        viewGroup.addView(view);

        return view;
    }

    public void destroyItem (ViewGroup viewGroup, int position, Object object){
        viewGroup.removeView((RelativeLayout) object);
    }
}
