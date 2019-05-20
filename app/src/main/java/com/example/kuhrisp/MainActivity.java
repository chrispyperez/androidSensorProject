package com.example.kuhrisp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accl;
    private static final String TAG = "SensorShit";

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    public final void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("Debug:", "SensorActivityOnCreateInvoked");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accl = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accl,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){
        System.out.println("Accuracy: " + accuracy);
    }

    @Override
    public final void onSensorChanged(SensorEvent event){
        Sensor mySensor = event.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y +z -last_x -last_y - last_z)/diffTime*10000;

                if(speed > SHAKE_THRESHOLD){
                    Toast.makeText(this,"Chill", Toast.LENGTH_SHORT).show();
                }
                last_x = x;
                last_y = y;
                last_z = z;

                TextView text = findViewById(R.id.xAxis);
                text.setText(Float.toString(last_x));

                text = findViewById(R.id.yAxis);
                text.setText(Float.toString(last_y));

                text = findViewById(R.id.zAxis);
                text.setText(Float.toString(last_z));

            }
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accl, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
