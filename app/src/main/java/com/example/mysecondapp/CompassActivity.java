package com.example.mysecondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class CompassActivity extends AppCompatActivity implements SensorEventListener{


    private ImageView imageView2;
    private ImageView imageViewN;
    private ImageView imageViewE;
    private ImageView imageViewV;
    private ImageView imageViewS;
    private TextView textView;
    private static Vibrator vibrator;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    static float ALPHA = 0.97f;
    private float azimuth = 0f;
    private float correctAzimuth = 0;


    private Point[] circle;
    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix =new float[9];
    private DisplayMetrics display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        imageView2 = findViewById(R.id.imageView2);
        imageViewN = findViewById(R.id.imageView3);
        imageViewS = findViewById(R.id.imageViewS);
        imageViewV = findViewById(R.id.imageViewV);
        imageViewE = findViewById(R.id.imageViewE);
        textView = findViewById(R.id.riktning);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
       // circle = calcCircle(imageView2.getHeight()/2, (int) imageView2.getX() + (imageView2.getWidth()/2),(int) imageView2.getY() +(imageView2.getHeight()/2));


        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListenerAccelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
              //  floatGravity = event.values;
               // floatGravity = lowPass(event.values, floatGravity);


                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

                if((azimuth <= 15  || (azimuth >= 345))){
                    if(Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE));
                    }else{
                        vibrator.vibrate(1);
                    }

                    vibrator.cancel();
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        SensorEventListener sensorEventListenerMagneticField = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {



            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(sensorEventListenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                for(int i = 0; i< floatGeoMagnetic.length; i++){
                    floatGeoMagnetic[i] = ALPHA*floatGeoMagnetic[i]+(1-ALPHA)*event.values[i];
                }
            }
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                for(int i = 0; i< floatGravity.length; i++){
                    floatGravity[i] = ALPHA*floatGravity[i]+(1-ALPHA)*event.values[i];
                }
            }
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,floatGravity,floatGeoMagnetic);
            if(success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);

                azimuth = (azimuth+360)%360;

                textView.setText("Vi är på väg "+ Math.round(azimuth) +" grader!");


                Point newPosN = calcPoint(Math.round(azimuth) -180, display.widthPixels /2 -60 , display.heightPixels /2 -40,imageView2.getHeight()/2 + 60);
                Point newPosE = calcPoint(Math.round(azimuth) -270, display.widthPixels /2 -60 , display.heightPixels /2 -40,imageView2.getHeight()/2 + 60);
                Point newPosV = calcPoint(Math.round(azimuth) -90, display.widthPixels /2 -60 , display.heightPixels /2 -40,imageView2.getHeight()/2 + 60);
                Point newPosS = calcPoint(Math.round(azimuth) -0, display.widthPixels /2 -60 , display.heightPixels /2 -40,imageView2.getHeight()/2 + 60);
                imageViewN.setX(newPosN.getX());
                imageViewN.setY(newPosN.getY());
                imageViewE.setX(newPosE.getX());
                imageViewE.setY(newPosE.getY());
                imageViewV.setX(newPosV.getX());
                imageViewV.setY(newPosV.getY());
                imageViewS.setX(newPosS.getX());
                imageViewS.setY(newPosS.getY());


                Animation move = new RotateAnimation(-correctAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                correctAzimuth = azimuth;


                move.setDuration(500);
                move.setRepeatCount(0);
                move.setFillAfter(true);
                imageView2.startAnimation(move);



            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void backButton(View view){

        vibrator.cancel();
        moveBack();
    }

    public void moveBack(){

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();

    }


    private class Point{
         int x;
         int y;
        Point (int x, int y){
            this.x = x;
            this.y = y;
        }
        public int getX(){
            return x;
        }
        public int getY(){
            return y;
        }
    }

    private Point calcPoint(int angle, int x, int y, int r){

        int newX = (int) Math.round(r * Math.sin(Math.PI * 2 * angle /360)) + x;
        int newY = (int) Math.round(r * Math.cos(Math.PI * 2 * angle /360)) + y;

        Point point = new Point(newX, newY);

        return point;

    }

}