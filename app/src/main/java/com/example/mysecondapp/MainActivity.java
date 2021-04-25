package com.example.mysecondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void CompassButton(View view){
        //imageView.setRotation(180);

        openCompass();
    }

    public void AccelButton(View view){

        openAccelerometer();
    }

    public void openCompass(){
        Intent intent = new Intent(this, CompassActivity.class);

        startActivity(intent);
    }
    public void openAccelerometer(){
        Intent intent = new Intent( this, AccelActivity.class);

        startActivity(intent);
    }

}