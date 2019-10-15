package com.example.musicpractice;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.musicpractice.MyService.ACTION_PAUSE;
import static com.example.musicpractice.MyService.ACTION_PLAY;

public class MainActivity extends AppCompatActivity {

    private Button mPlay;
    private Button mPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlay = findViewById(R.id.play);
        mPause = findViewById(R.id.pause);
    }

    public void onClick(View view) {
        if (view == mPlay) {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction(ACTION_PLAY);
            startService(intent);
        } else if (view == mPause) {
            Intent intent = new Intent(this, MyService.class);
            intent.setAction(ACTION_PAUSE);
            startService(intent);
        }
    }
}
