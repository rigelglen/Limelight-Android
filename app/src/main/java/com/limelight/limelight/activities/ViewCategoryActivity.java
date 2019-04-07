package com.limelight.limelight.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.limelight.limelight.R;

public class ViewCategoryActivity extends AppCompatActivity {
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String t = extras.getString("cat");
            title=findViewById(R.id.title);
            title.setText(t);
        }





    }
}
