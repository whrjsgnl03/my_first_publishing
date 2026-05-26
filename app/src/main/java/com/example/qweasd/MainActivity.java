package com.example.qweasd;


import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    boolean i = true;
    SeekBar seekBar;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageButton button = findViewById(R.id.play_btn);//재생 버튼
        button.setImageResource(R.drawable.playbutton);//재생 버튼 이미지 설정
        seekBar = findViewById(R.id.soundSeekBar);//시크바 설정
        mp = new MediaPlayer();//미디어 플레이어 객체 생성
        mp = MediaPlayer.create(this, R.raw.music);//음악 파일 설정

        if (mp != null) {
            seekBar.setMax(mp.getDuration());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == true) {
                    startplayer(button);
                } else {                        pauseplayer(button);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mp != null) {
                    mp.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mp.setOnCompletionListener(
                mediaPlayer -> {
                    button.setImageResource(R.drawable.playbutton);
                    i = true;
                    seekBar.setProgress(0);
                }
        );
                //아래는 걍 붙는놈
                    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
    }
    private void startplayer(ImageButton button) {
        mp.start();
        button.setImageResource(R.drawable.pause_btn);
        i = false;
        updateseekbar();
    }
    private void pauseplayer(ImageButton button) {
        mp.pause();
        button.setImageResource(R.drawable.playbutton);
        i = true;
    }

    private void updateseekbar() {
        new Thread(() -> {
            while (mp != null && mp.isPlaying()) {
                try {
                    Thread.sleep(100);
                    runOnUiThread(() -> {
                        if (mp != null) {
                            seekBar.setProgress(mp.getCurrentPosition());
                            updatenowtime();
                            updatemax();
                        }
                    });
                } catch (InterruptedException e) {
                    android.util.Log.e(TAG, "SeekBar thread interrupted", e);
                }
            }
        }).start();
    }
    private void updatemax() {
        TextView max_time = (TextView) findViewById(R.id.max_time);
        long maxTime = mp.getDuration() / 1000;
        long maxMin = maxTime/60;
        long maxSec = maxTime%60;
        String maxTimeStr = String.format("%d:%02d", maxMin, maxSec);
        max_time.setText(maxTimeStr);
    }
    private void updatenowtime() {
        TextView now_time = findViewById(R.id.now_time);
        long nowTime = mp.getCurrentPosition() / 1000;
        long min = nowTime/60;
        long sec = nowTime%60;
        String time = String.format("%d:%02d", min, sec);
        now_time.setText(time);
    }
}