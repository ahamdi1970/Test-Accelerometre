package fr.assistdin.testaccelerometreavecgraphe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    TextView txt_currentAccel, txt_prevAccel, txt_acceleration;
    ProgressBar prog_shakeMeter;
    Switch m_Switch;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private double accelerationCurrentValue;
    private double accelerationPreviousValue;

    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;
    private Viewport viewport;

    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });

    private SensorEventListener sensorEventListener = new SensorEventListener () {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            accelerationCurrentValue = Math.sqrt ( x * x + y * y + z * z );
            double changeOfAcceleration = Math.abs ( accelerationCurrentValue - accelerationPreviousValue );
            accelerationPreviousValue = accelerationCurrentValue;


            txt_currentAccel.setText ( "Current Accel = " + ( int ) accelerationCurrentValue );
            txt_prevAccel.setText ( "Previous Accel =" + ( int ) accelerationPreviousValue );
            txt_acceleration.setText ( "Acceleration change = " + ( int ) changeOfAcceleration );

            prog_shakeMeter.setProgress ( ( int ) changeOfAcceleration );

            // change colors based on amount of shaking

            if (changeOfAcceleration > 14) {
                txt_acceleration.setBackgroundColor ( Color.RED );
            } else if (changeOfAcceleration > 5) {
                txt_acceleration.setBackgroundColor ( Color.parseColor ( "#f59b42" ) );
            } else if (changeOfAcceleration > 2) {
                txt_acceleration.setBackgroundColor ( Color.YELLOW );
            } else {
                txt_acceleration.setBackgroundColor ( getResources ().getColor ( R.color.design_default_color_background ) );
            }

            //update graph

            pointsPlotted++;
            series.appendData ( new DataPoint(pointsPlotted, changeOfAcceleration),true,pointsPlotted  );
            viewport.setMaxX ( pointsPlotted );
            viewport.setMinX ( pointsPlotted - 200 );

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        txt_acceleration = findViewById ( R.id.txt_accel );
        txt_currentAccel = findViewById ( R.id.txt_currentAccel );
        txt_prevAccel = findViewById ( R.id.txt_previousAccel );

        m_Switch = findViewById ( R.id.switch1 );

        prog_shakeMeter = findViewById ( R.id.progressBar2 );

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport ();
        viewport.setScrollable ( true );
        viewport.setXAxisBoundsManual ( true );
        m_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The switch is enabled
                    graph.addSeries(series);
                } else {
                    // The switch is disabled
                    graph.removeAllSeries ();
                }
            }
        });



    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}