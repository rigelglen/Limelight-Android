package com.limelight.limelight.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.R;
import com.limelight.limelight.activities.LoginActivity;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.models.ClassificationReport;
import com.limelight.limelight.models.Clickbait;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.models.Sentiment;
import com.limelight.limelight.models.Writing;
import com.limelight.limelight.network.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassificationDialog extends Dialog {
    public Context ctx;
    public Dialog d;
    public String url;
    private PieChart clickBaitChart;
    private PieChart sentimentChart;
    private PieChart writingChart;
    //private PieChart factCheckChart;
    private String token;
    private SharedPreferences sharedPref;
    private List<PieEntry> entriesClickbait;
    private List<PieEntry> entriesSenti;
    private List<PieEntry> entriesWrite;
    private ProgressBar classificationProgress;
    private RelativeLayout classificationBody;
    private TextView disclaimerText, writingResultText, clickbaitResultText, sentimentResultText;

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
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Button classificationClose = findViewById(R.id.classification_close);
        clickBaitChart = findViewById(R.id.clickBaitChart);
        sentimentChart = findViewById(R.id.sentimentChart);
        writingChart = findViewById(R.id.writingChart);
        //factCheckChart = findViewById(R.id.factCheckChart);
        classificationProgress = findViewById(R.id.classification_progress);
        classificationBody = findViewById(R.id.classification_body);

        disclaimerText = findViewById(R.id.disclaimerText);
        writingResultText = findViewById(R.id.writingResultText);
        clickbaitResultText = findViewById(R.id.clickbaitResultText);
        sentimentResultText = findViewById(R.id.sentimentResultText);

        sharedPref = ctx.getSharedPreferences("limelight", Context.MODE_PRIVATE);
        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
        } else {
            //go to login activity
            logout();
        }

        classificationClose.setOnClickListener((v) -> this.dismiss());

        // clickbait

        entriesClickbait = new ArrayList<>();
        entriesClickbait.add(new PieEntry(20f, "Clickbait"));
        entriesClickbait.add(new PieEntry(80f, "Real"));

        PieDataSet setClick = new PieDataSet(entriesClickbait, "Article clickbait results");
        setClick.setColors(ctx.getColor(R.color.neg_res), ctx.getColor(R.color.pos_res));
        setClick.setValueTextColor(Color.WHITE);
        setClick.setValueTextSize(20);
        PieData dataClick = new PieData(setClick);
        clickBaitChart.setData(dataClick);
        clickBaitChart.getDescription().setEnabled(false);
        clickBaitChart.getLegend().setEnabled(false);
        clickBaitChart.invalidate(); // refresh

        // sentiment

        entriesSenti = new ArrayList<>();
        entriesSenti.add(new PieEntry(33f, "Positive"));
        entriesSenti.add(new PieEntry(33f, "Negative"));
        entriesSenti.add(new PieEntry(33f, "Neutral"));

        PieDataSet setSenti = new PieDataSet(entriesSenti, "Article Sentiment results");
        setSenti.setValueTextColor(Color.WHITE);
        setSenti.setValueTextSize(20);
        setSenti.setColors(ctx.getColor(R.color.pos_res), ctx.getColor(R.color.neg_res), ctx.getColor(R.color.neu_res));
        PieData dataSenti = new PieData(setSenti);
        sentimentChart.setData(dataSenti);
        sentimentChart.getDescription().setEnabled(false);
        sentimentChart.getLegend().setEnabled(false);
        sentimentChart.invalidate(); // refresh

        //writing
        entriesWrite = new ArrayList<>();
        entriesWrite.add(new PieEntry(20f, "Fake"));
        entriesWrite.add(new PieEntry(80f, "Real"));

        PieDataSet setWrite = new PieDataSet(entriesWrite, "Article writing style results");
        setWrite.setColors(ctx.getColor(R.color.pos_res), ctx.getColor(R.color.neg_res));
        setWrite.setValueTextColor(Color.WHITE);
        setWrite.setValueTextSize(20);
        PieData dataWrite = new PieData(setWrite);
        writingChart.setData(dataWrite);
        writingChart.getDescription().setEnabled(false);
        writingChart.getLegend().setEnabled(false);
        writingChart.invalidate(); // refresh

        getClassification();
    }

    private void getClassification() {
        Api api = RetrofitClient.getInstance().getApiService();
        Call<ClassificationReport> call = api.getClassificationReport(token, url);

        call.enqueue(new Callback<ClassificationReport>() {
            @Override
            public void onResponse(@NonNull Call<ClassificationReport> call, @NonNull Response<ClassificationReport> response) {
                if (response.body() != null && response.isSuccessful()) {

                    Clickbait clickbait = response.body().getClickbait();
                    Sentiment sentiment = response.body().getSentiment();
                    Writing writing = response.body().getWriting();
                    disclaimerText.setText(response.body().getDisclaimer());
                    writingResultText.setText(writing.getMessage());
                    sentimentResultText.setText(sentiment.getMessage());
                    clickbaitResultText.setText(clickbait.getMessage());

                    entriesClickbait.clear();
                    entriesSenti.clear();
                    entriesWrite.clear();

                    // for clickbait
                    entriesClickbait.add(new PieEntry(clickbait.getClickbait(), "Clickbait"));
                    entriesClickbait.add(new PieEntry(clickbait.getNews(), "News"));

                    //for writing
                    entriesWrite.add(new PieEntry(writing.getReal(), "Real"));
                    entriesWrite.add(new PieEntry(writing.getFake(), "Fake"));


                    //for sentiment
                    entriesSenti.add(new PieEntry(sentiment.getNegative(), "Negative"));
                    entriesSenti.add(new PieEntry(sentiment.getPositive(), "Positive"));
                    entriesSenti.add(new PieEntry(sentiment.getNeutral(), "Neutral"));

                    clickBaitChart.notifyDataSetChanged();
                    sentimentChart.notifyDataSetChanged();
                    writingChart.notifyDataSetChanged();

                    clickBaitChart.invalidate(); // refresh
                    sentimentChart.invalidate(); // refresh
                    writingChart.invalidate(); // refresh


                    classificationProgress.setVisibility(View.GONE);
                    classificationBody.setVisibility(View.VISIBLE);


                } else if (response.errorBody() != null) {
                    Gson gson = new GsonBuilder().create();
                    ErrorModel mErrorModel;
                    try {
                        mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                        //Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
                        new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(mErrorModel.getMessage())
                                .show();
                        ClassificationDialog.this.dismiss();
                        //logout();

                    } catch (IOException e) {
                        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
                        ClassificationDialog.this.dismiss();
                    }
                } else {
                    Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
                    ClassificationDialog.this.dismiss();
                }


            }

            @Override
            public void onFailure(@NonNull Call<ClassificationReport> call, @NonNull Throwable t) {

                SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No internet connection");
                pDialog.show();
            }

        });

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
