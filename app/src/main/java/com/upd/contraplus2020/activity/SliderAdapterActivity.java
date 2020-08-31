package com.upd.contraplus2020.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upd.contraplus2020.MainActivity;
import com.upd.contraplus2020.R;
import com.upd.contraplus2020.db.UserDBHandler;
import com.upd.contraplus2020.utils.SliderAdapter;

public class SliderAdapterActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout linearLayout;

    private TextView[] dots;

    private SliderAdapter sliderAdapter;

    UserDBHandler userDBHandler = new UserDBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_adapter);

        viewPager = findViewById(R.id.slideViewPager);
        linearLayout = findViewById(R.id.linearLayout);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        viewPager.addOnPageChangeListener(viewListener);

        addDotsIndicator(0);
    }

    public void addDotsIndicator(int position){
        dots = new TextView[4];
        linearLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(20);
            dots[i].setTextColor(getResources().getColor(R.color.colorSecondary));
            linearLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void goToEulaActivity(View view){
        if (userDBHandler.getKeyUniqueid().isEmpty()){
            Intent intent = new Intent(getApplicationContext(), EulaActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
