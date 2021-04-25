package com.example.mysecondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;

import android.content.Intent;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.view.View;
import android.widget.TextView;

public class AccelActivity extends AppCompatActivity {


    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;

    private  TextView riktning;
    private  TextView Xaxis;
    private  TextView Yaxis;
    private  TextView Zaxis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accel);

        riktning = findViewById(R.id.accRiktning);
        Xaxis = findViewById(R.id.Xaxis);
        Yaxis = findViewById(R.id.Yaxis);
        Zaxis = findViewById(R.id.Zaxis);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListenerAccelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float X = event.values[0];
                float Y = event.values[1];
                float Z = event.values[2];
                Xaxis.setText("X: " + X);
                Yaxis.setText("Y: " + Y);
                Zaxis.setText("Z: " + Z);
                float xAbs = Math.abs(X);
                float yAbs = Math.abs(Y);
                float zAbs = Math.abs(Z);

                if(xAbs > yAbs && xAbs > zAbs ) {
                    if (X < 0 && xAbs > 1) {
                        riktning.setText("På g höger");
                    } else if (X > 0 && xAbs > 1) {
                        riktning.setText("På g vänster");
                    }
                }else if(yAbs > xAbs && yAbs > zAbs){
                    if (Y > 0 && yAbs > 1){
                        riktning.setText("På g upp");
                    }else if(Y < 0 && yAbs > 1){
                        riktning.setText("På g ned");
                    }
                }else if(zAbs > xAbs && zAbs >yAbs){
                    if(Z > 0 && zAbs > 1){
                        riktning.setText("På g framåt");
                    }else if(Z < 0 && zAbs > 1){
                        riktning.setText("På g bakåt");
                    }
                }else{
                    riktning.setText("Ganska still atm");
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(sensorEventListenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }


    public void backButton(View view){

        moveBack();

    }
    public void moveBack(){
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}