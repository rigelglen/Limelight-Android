package com.limelight.limelight.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.limelight.limelight.R;

public class ViewSearchActivity extends AppCompatActivity {
    private TextView title;
    private ImageButton backBtn;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_search);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String t = extras.getString("searchQuery");
            title=findViewById(R.id.title);
            title.setText(t);
        }


        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
