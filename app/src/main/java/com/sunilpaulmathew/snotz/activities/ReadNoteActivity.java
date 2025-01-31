package com.sunilpaulmathew.snotz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.Common;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2021
 */
public class ReadNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readnote);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageView mImage = findViewById(R.id.image);
        MaterialTextView mContents = findViewById(R.id.contents);
        NestedScrollView mScrollView = findViewById(R.id.scroll_view);

        if (Common.getReadModeText() != null) {
            mContents.setText(Common.getReadModeText());
        }

        if (Common.getReadModeImage() != null) {
            mImage.setImageBitmap(Common.getReadModeImage());
            mImage.setVisibility(View.VISIBLE);
            mImage.setOnClickListener(v -> {
                Intent imageView = new Intent(this, ImageViewActivity.class);
                startActivity(imageView);
            });
        }

        if (sUtils.isDarkTheme(this)) {
            mContents.setTextColor(sUtils.getColor(R.color.color_white, this));
            mScrollView.setBackgroundColor(sUtils.getColor(R.color.color_black, this));
        } else {
            mContents.setTextColor(sUtils.getColor(R.color.color_black, this));
            mScrollView.setBackgroundColor(sUtils.getColor(R.color.color_white, this));
        }

        mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sUtils.getInt("font_size", 18, this));
        mContents.setTypeface(null, AppSettings.getStyle(this));

        mBack.setOnClickListener(v -> finish());
    }

}