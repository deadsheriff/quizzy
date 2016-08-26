package com.no.starting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_main);
        setTip();
       // this.deleteFile("used");
    }
    protected void setTip() {
        Random r = new Random();
        int num = getResources().getInteger(R.integer.tipCount)+1;
        int i = r.nextInt(num-1)+1;
        // int id = getResources().getIdentifier("tip" + String.valueOf(i), "string", getPackageName());
        String currentTip = (String) getResources().getText(getResources().getIdentifier("tip" + String.valueOf(i), "string", getPackageName()));
        TextView tipBox = (TextView) findViewById(R.id.tipBox);
        tipBox.setText(currentTip);
    }
    protected void fullScreen(){

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    public void onClickStart(View v) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

}
