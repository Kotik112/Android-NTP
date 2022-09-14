package com.example.uppg1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ClockManager{

    private TextView txtClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtClock = findViewById(R.id.txt_data);
        Button btnStart = findViewById(R.id.button_start);
        Button btnStop = findViewById(R.id.button_stop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_start:
                Toast.makeText(this, "Start button pressed", Toast.LENGTH_SHORT).show();
                txtClock.setText("Starting...");
                break;

            case R.id.button_stop:
                Toast.makeText(this, "Stop button pressed", Toast.LENGTH_SHORT).show();
                txtClock.setText("Stopping....");
                break;

            default:
                break;
        }
    }

    // Used by ClockManager interface to update the time view
    @Override
    public void updateClock(String time) {
        txtClock.setText(time);
    }
}