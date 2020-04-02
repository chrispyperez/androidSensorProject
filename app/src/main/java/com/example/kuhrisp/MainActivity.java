package com.example.kuhrisp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyro;


    private static final float NS2S = 1.0f / 1000000000.0f;  //constant to convert nanoseconds to seconds.
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    @Override
    public final void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        Log.v("Debug:", "SensorActivityOnCreateInvoked");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this,gyro,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){
        System.out.println("Accuracy: " + accuracy);
    }

    @Override
    public final void onSensorChanged(SensorEvent event){
        //Take current timestep's DeltaRotation * CurrentRotation
        if(timestamp !=0){
            final float dT = (event.timestamp - timestamp) * NS2S; //Change in Time

            //Axis of the rotation sample, not normalized
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            //Calculate the angular speed of the sample
            float omegaMagnitude = (float)Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            //Normalize the rotation vector if it's big enough to get the axis
            //(EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > 0){
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }



            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;

        }

        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationVector,deltaRotationVector);


        float x = deltaRotationVector[0];
        float y = deltaRotationVector[1];
        float z = deltaRotationVector[2];


        TextView text = findViewById(R.id.GyroxAxis);
        text.setText(Float.toString(x));

        text = findViewById(R.id.GyroyAxis);
        text.setText(Float.toString(y));

        text = findViewById(R.id.GyrozAxis);
        text.setText(Float.toString(z));



    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
