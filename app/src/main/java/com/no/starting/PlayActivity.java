
package com.no.starting;
import com.google.android.gms.ads.*;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class PlayActivity extends Activity {
   
    protected String currentAnswerCheck;
    int max;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_play);
       

        int pr = loadPref("USED_QUESTION", 1);
        max = getResources().getInteger(R.integer.questNum);
        progressInit(pr,max);
        int p = loadPref("ROW", 0);
        int io = loadPref("ROW_RECORD", 0);
        rowInit(p,io);
        achieveInit();
        taskInit();
        if(loadPref("FINISH",0)!=1){
            currentAnswerCheck = selectQuestion();

            LinearLayout llLeft = (LinearLayout) findViewById(R.id.iSelectLeft);
            LinearLayout llRight = (LinearLayout) findViewById(R.id.iSelectRight);
            llLeft.setOnClickListener(oclBtn);
            llRight.setOnClickListener(oclBtn);


        }
        else {
            finishGame();
        }

    }
    protected void fullScreen(){

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


//----Возвращает "1" если верный левый ответ, "2" если правый

    protected String selectQuestion(){

        int i = loadPref("USED_QUESTION", 1);

            String[] currQuestion = getResources().getStringArray(getResources().getIdentifier("qu" + String.valueOf(i), "array", getPackageName()));
            TextView questView = (TextView) findViewById(R.id.questView);
            questView.setText(currQuestion[0]);
            ImageView imgViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
            imgViewLeft.setImageResource(getResources().getIdentifier(currQuestion[1], "drawable", getPackageName()));
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageViewRight);
            imgViewRight.setImageResource(getResources().getIdentifier(currQuestion[2], "drawable", getPackageName()));
            TextView currQuestionNum = (TextView)findViewById(R.id.currQuestionNum);
            currQuestionNum.setText("Вопрос № "+ i);
            TextView descrLeft = (TextView) findViewById(R.id.descrLeft);
            descrLeft.setText(currQuestion[3]);
            TextView descrRight = (TextView) findViewById(R.id.descrRight);
            descrRight.setText(currQuestion[4]);

            return currQuestion[5];

    }
//-----------------------------------------------------------
int row,record,trues;
    protected void checkTrue(String selected){
            record = loadPref("ROW_RECORD",0);
            trues = loadPref("TRUES",0);
            if(selected.equals(currentAnswerCheck)) {

                row = loadPref("ROW",0);
                row = row+1;
                if(row>record) {
                    record = row;
                }
                rowInit(row, record);
                trues++;
                savePref("ROW",row);
                savePref("ROW_RECORD",record);
                savePref("TRUES",trues);

                checkTasks();
                Toast t = Toast.makeText(this, "Верно", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();


            }
            else
            {
                Toast t = Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                row = 0;
                rowInit(row, record);
                savePref("ROW",0);
                savePref("ROW_RECORD",record);
            }
        int i = loadPref("USED_QUESTION", 1);
        if(i!=max) {
            i++;
            savePref("USED_QUESTION", i);
            progressInit(i,max);
            currentAnswerCheck = selectQuestion();
        }
        else {
            progressInit(max+1,max);
            finishGame();
        }
        }



    OnClickListener oclBtn = new OnClickListener() {
        @Override
    public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iSelectLeft:
                    checkTrue("1");
                    break;
                case R.id.iSelectRight:
                    checkTrue("2");
                    break;
            }
        }
    };

//------Два метода для работы с сохранением (загрузка и выгрузка)
    void savePref(String name,int value) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(name, value);
        ed.commit();
    }

    int loadPref(String name, int def) {
        sPref = getPreferences(MODE_PRIVATE);
        return sPref.getInt(name, def);
    }
//----------------------------------------

//-----Метод заканчивающий игру если вопросов нет
    void finishGame(){
        savePref("FINISH",1);
        trues = loadPref("TRUES",0);
        record = loadPref("ROW_RECORD",record);
        LinearLayout llLeft = (LinearLayout) findViewById(R.id.iSelectLeft);
        LinearLayout llRight = (LinearLayout) findViewById(R.id.iSelectRight);
        TextView currQuestionNum = (TextView) findViewById(R.id.currQuestionNum);
        TextView descrLeft = (TextView) findViewById(R.id.descrLeft);
        TextView descrRight = (TextView) findViewById(R.id.descrRight);
        llLeft.setVisibility(View.INVISIBLE);
        llRight.setVisibility(View.INVISIBLE);
        descrLeft.setVisibility(View.INVISIBLE);
        descrRight.setVisibility(View.INVISIBLE);
        currQuestionNum.setText(getResources().getIdentifier("greetings", "string", getPackageName()));
        LinearLayout gratz = (LinearLayout) findViewById(R.id.gratz);
        gratz.setBackground(getResources().getDrawable(R.drawable.goldcup));
        TextView questView = (TextView) findViewById(R.id.questView);
        questView.setText("Поздравляем, вы прошли игру, ответив верно на "+trues+" из "+max+" вопросов!" +
                " Ваша наилучшая серия - "+record+" вопросов подряд!");
        TextView helpme = (TextView) findViewById(R.id.textView6);
        helpme.setVisibility(View.GONE);
        progressInit(101,100);

    }

//----Вывод прогресса на прогресс-бар и подсчёт пройденных процентов.

    void progressInit(int num, int max)
    {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView textProgress = (TextView)findViewById(R.id.textProgress);

        num--;
        progressBar.setMax(max);
        progressBar.setProgress(num);

        double res = ((double)num / (double)max )*100;
        int resShow = (int) res;
        textProgress.setText("Пройдено "+ resShow + "%");


    }

//-------Запись значений ВЕРНО ПОДРЯД и РЕКОРД ПОДРЯД
    void rowInit(int r, int rec){
        TextView textInARow = (TextView)findViewById(R.id.textInARow);
        TextView textInARowRecord = (TextView)findViewById(R.id.textInARowRecord);
        textInARow.setText("Верно подряд: "+ r );
        textInARowRecord.setText("Рекорд подряд: "+ rec );
    }
//---------------------------------------------------

//-------Проверка количества правильных ответов для заданий (1,10,100,500)
    void checkTasks(){
        if(trues==1){
            Toast t = Toast.makeText(this, "Задание выполнено. Получена «Медаль Новичка»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
            TextView currQuestionNum = (TextView) findViewById(R.id.textView6);
            currQuestionNum.setText(getResources().getIdentifier("task1", "string", getPackageName()));
            savePref("ACHIVEMENT1",1);
            savePref("NUMTASK",1);

        }
        if(trues==10){
            Toast t = Toast.makeText(this, "Задание выполнено. Получена «Бронзовая медаль Угадайки»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
            TextView currQuestionNum = (TextView) findViewById(R.id.textView6);
            currQuestionNum.setText(getResources().getIdentifier("task2", "string", getPackageName()));
            savePref("ACHIVEMENT2",1);
            savePref("NUMTASK",2);
        }
        if(trues==100){
            Toast t = Toast.makeText(this, "Задание выполнено. Получена «Серебрянная медаль Угадайки»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
            TextView currQuestionNum = (TextView) findViewById(R.id.textView6);
            currQuestionNum.setText(getResources().getIdentifier("task3", "string", getPackageName()));
            savePref("ACHIVEMENT3",1);
            savePref("NUMTASK",3);
        }
        if(trues==500){
            Toast t = Toast.makeText(this, "Задание выполнено. Получена «Золотая медаль Угадайки»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();

            savePref("ACHIVEMENT4",1);
            savePref("NUMTASK",4);

        }

        if(trues==1) {
            Toast t = Toast.makeText(this, "Вы получаете бронзовый кубок Угадайки за 10 вопросов подряд!»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
            savePref("ACHIVEMENT8",1);
        }
        if(row==30) {
            Toast t = Toast.makeText(this, "Вы получаете серебрянный кубок Угадайки за 30 вопросов подряд!»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();

            savePref("ACHIVEMENT7",1);
        }
        if(row==50) {
            Toast t = Toast.makeText(this, "Вы получаете золотой кубок Угадайки за 50 вопросов подряд!»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();

            savePref("ACHIVEMENT6",1);
        }
        if(row==100) {
            Toast t = Toast.makeText(this, "Вы получаете золотую корону Угадайки за 100 вопросов подряд!»", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();

            savePref("ACHIVEMENT5",1);
        }
        achieveInit();
        taskInit();
        }


    void achieveInit(){
        if(loadPref("ACHIVEMENT1",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView);
            imgViewRight.setVisibility(View.VISIBLE);
        }
        if(loadPref("ACHIVEMENT2",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView2);
            imgViewRight.setVisibility(View.VISIBLE);
        }
        if(loadPref("ACHIVEMENT3",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView3);
            imgViewRight.setVisibility(View.VISIBLE);
        }
        if(loadPref("ACHIVEMENT4",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView4);
            imgViewRight.setVisibility(View.VISIBLE);
        }
        if(loadPref("ACHIVEMENT5",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView5);
            imgViewRight.setVisibility(View.VISIBLE);
        }
        if(loadPref("ACHIVEMENT6",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView6);
            imgViewRight.setVisibility(View.VISIBLE);
        }
        if(loadPref("ACHIVEMENT7",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView7);
            imgViewRight.setVisibility(View.VISIBLE);
        }
        if(loadPref("ACHIVEMENT8",0)!=0){
            ImageView imgViewRight = (ImageView) findViewById(R.id.imageView8);
            imgViewRight.setVisibility(View.VISIBLE);
        }
    }
    void taskInit(){
        int i = loadPref("NUMTASK",0);
            TextView currQuestionNum = (TextView) findViewById(R.id.textView6);
            currQuestionNum.setText(getResources().getIdentifier("task"+String.valueOf(i), "string", getPackageName()));
        }
    }


