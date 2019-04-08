package com.limelight.limelight.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.limelight.limelight.R;
import com.limelight.limelight.activities.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ClassificationDialog extends Dialog{
    public Context ctx;
    public Dialog d;
    public String url;
    PieChart clickBaitChart;
    PieChart sentimentChart;
    PieChart writingChart;
    PieChart factCheckChart;
    Button classificationClose;
    String token;
    SharedPreferences sharedPref;

    public ClassificationDialog(Context ctx, String url) {
        super(ctx);
        this.ctx = ctx;
        this.url = url;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_classification);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

        classificationClose = findViewById(R.id.classification_close);
        clickBaitChart = findViewById(R.id.clickBaitChart);
        sentimentChart = findViewById(R.id.sentimentChart);
        writingChart = findViewById(R.id.writingChart);
        factCheckChart = findViewById(R.id.factCheckChart);

        sharedPref = ctx.getSharedPreferences("limelight", Context.MODE_PRIVATE);
        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
        } else {
            //go to login activity
            logout();
        }

        classificationClose.setOnClickListener((v)->{
            this.dismiss();
        });

        // clickbait

        List<PieEntry> entriesClick = new ArrayList<>();
        entriesClick.add(new PieEntry(20f, "Clickbait"));
        entriesClick.add(new PieEntry(80f, "Real"));

        PieDataSet setClick = new PieDataSet(entriesClick, "Article clickbait results");
        setClick.setColors(Color.RED, Color.BLUE);
        setClick.setValueTextColor(Color.WHITE);
        setClick.setValueTextSize(20);
        PieData dataClick = new PieData(setClick);
        clickBaitChart.setData(dataClick);
        clickBaitChart.getDescription().setEnabled(false);
        clickBaitChart.getLegend().setEnabled(false);
        clickBaitChart.invalidate(); // refresh

        // sentiment

        List<PieEntry> entriesSenti = new ArrayList<>();
        entriesSenti.add(new PieEntry(33f, "Positive"));
        entriesSenti.add(new PieEntry(33f, "Negative"));
        entriesSenti.add(new PieEntry(33f, "Neutral"));

        PieDataSet setSenti = new PieDataSet(entriesSenti, "Article Sentiment results");
        setSenti.setValueTextColor(Color.WHITE);
        setSenti.setValueTextSize(20);
        setSenti.setColors(Color.RED, Color.BLUE, Color.GREEN);
        PieData dataSenti = new PieData(setSenti);
        sentimentChart.setData(dataSenti);
        sentimentChart.getDescription().setEnabled(false);
        sentimentChart.getLegend().setEnabled(false);
        sentimentChart.invalidate(); // refresh

        // TODO CREATE CONSOLIDATED ENDPOINT
        // TODO CREATE PROGRESS BAR WHEN LOADING FROM SERVER
        // TODO FETCH DATA FROM SERVER
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(ctx, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.dismiss();
        ctx.startActivity(i);
    }

}
