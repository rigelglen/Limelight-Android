package com.limelight.limelight.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.limelight.limelight.R;

public class ClassificationDialog extends Dialog{
    public Context ctx;
    public Dialog d;
    public String url;
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

    }



}
