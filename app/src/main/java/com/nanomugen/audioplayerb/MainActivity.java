package com.nanomugen.audioplayerb;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    Boolean toprepare =true;
    ImageView playView;
    ProgressBar progress;
    private int oneT=0;
    private double startTime = 0;
    private double endTime = 0;
    private Handler handler = new Handler();
    TextView final_time,current_time;
    TextView info;
    MediaMetadataRetriever media;
    TextView detail;
    ImageView before;
    ImageView backward;
    ImageView stop;
    ImageView forward;
    ImageView after;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = new MediaPlayer();
        try {
            player.setDataSource(this, Uri.parse("android.resource://com.nanomugen.audioplayerb/raw/r_u_mine"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final_time = (TextView) findViewById(R.id.final_time);
        current_time = (TextView)findViewById(R.id.current_time);
        progress = (ProgressBar)findViewById(R.id.progress);
        info = (TextView) findViewById(R.id.teste);
        playView = (ImageView)findViewById(R.id.play);
        stop = (ImageView) findViewById(R.id.stop);
        media = new MediaMetadataRetriever();
        media.setDataSource(this, Uri.parse("android.resource://com.nanomugen.audioplayerb/raw/r_u_mine"));

        //CREATING THE MEDIAPLAYER OBJECT AND ARRANGING THE ONCLOMPLETE FUNCTIONS

        //player = MediaPlayer.create(this,R.raw.r_u_mine);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //AT THE END OF THE MUSIC
                handler.removeCallbacks(UpdateSongTime);
                player.stop();
                toprepare = true;
                stop.setImageResource(R.drawable.unstopd);
                playView.setImageResource(R.drawable.play);
                progress.setProgress(0);
                current_time.setText("0 min, 0 sec");

            }
        });

        //


        String artist = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String album=media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);


        detail = (TextView)findViewById(R.id.detail);
        detail.setText(title+"\n"+artist+"\n"+album);


        info.setText(title+" / "+artist);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(detail.getVisibility()==View.INVISIBLE){
                    detail.setVisibility(View.VISIBLE);
                }
                else{
                    detail.setVisibility(View.INVISIBLE);
                }
            }
        });



        //SETTING PLAY AND PAUSE BUTTON

        playView.setImageResource(R.drawable.play);
        playView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //PLAY LOGIC HERE
                if(toprepare){
                    try {
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    toprepare=false;
                }
                if(!player.isPlaying()) {
                    player.start();
                    playView.setImageResource(R.drawable.pause);
                     stop.setImageResource(R.drawable.stop);
                    endTime = player.getDuration();
                    startTime = player.getCurrentPosition();
                    if (oneT == 0) {
                        oneT = 1;
                        progress.setMax((int) endTime);
                        final_time.setText(String.format(" / %d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) endTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                endTime)))
                        );
                    }


                    current_time.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            startTime)))
                    );
                    progress.setProgress((int) startTime);
                    handler.postDelayed(UpdateSongTime, 100);
                }
                else{
                    player.pause();
                    playView.setImageResource(R.drawable.play);
                }

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(UpdateSongTime);
                player.stop();
                toprepare = true;
                stop.setImageResource(R.drawable.unstopd);
                playView.setImageResource(R.drawable.play);
                progress.setProgress(0);
                current_time.setText("0 min, 0 sec");

            }
        });

    }
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = player.getCurrentPosition();
            current_time.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    startTime)))
            );
            progress.setProgress((int)startTime);
            handler.postDelayed(UpdateSongTime,100);
        }
    };

}
