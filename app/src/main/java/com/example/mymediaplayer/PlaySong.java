package com.example.mymediaplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlaySong  extends AppCompatActivity  implements SeekBar.OnSeekBarChangeListener{

//    https://coderlessons.com/articles/mobilnaia-razrabotka-articles/fonovoe-audio-v-android-s-mediasessioncompat
    // создаём плеер
    MediaPlayer mPlayer;
    private ArrayList<Song> songList;
    String songTitle = "";
    String path = "";
    String artist = "";
    int position = 0;
    private SeekBar seekBar;
    private Handler myHandler = new Handler();
    private int startTime = 0;
    private Runnable mRunnable;
    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_song);

        seekBar = findViewById(R.id.seekBar);

        // получаем массив песен
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            songList = bundle.getParcelableArrayList("songList");
        }

        // получаем позицию песни
        position = getIntent().getExtras().getInt("position");

        timeText = findViewById(R.id.time);

        path = songList.get(position).getPath();
        Uri uri = Uri.fromFile(new File(path));
        // создаём плеер
        mPlayer = MediaPlayer.create(this, uri);

        // запускаем песню
        runMusic(position);
        // добавляем слушателя для перемотки
        seekBar.setOnSeekBarChangeListener(this);

        // если песня доиграла до конца, то запускаем следующую
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.stop();
                if (position == songList.size() - 1) {
                    position = 0;
                }
                else {
                    position += 1;
                }
                initMusic(position);
            }
        });
    }

    private boolean play = true;
    private Handler mHandler = new Handler();

    protected void initializeSeekBar(){
        seekBar.setMax(mPlayer.getDuration()/1000);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mPlayer!=null){
                    int mCurrentPosition = mPlayer.getCurrentPosition()/1000; // In milliseconds
                    int maxPosition = mPlayer.getDuration()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    int minutesPos = mCurrentPosition / 60;
                    int secondsPos = mCurrentPosition%60;
                    int minutesMax = maxPosition / 60;
                    int secondsMax = maxPosition%60;
                    timeText.setText(minutesPos + ":" + (secondsPos < 10 ? "0" : "") + secondsPos + " / " + minutesMax + ":" + (secondsMax < 10 ? "0" : "") + secondsMax);
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
    }

    public void initMusic(int position) {
        path = songList.get(position).getPath();
        Uri uri = Uri.fromFile(new File(path));
        try {
            mPlayer.reset();
            mPlayer.setDataSource(this, uri);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepare();
            runMusic(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runMusic(int position) {
        // получаем путь, заголовок, артиста
        path = songList.get(position).getPath();
        songTitle = songList.get(position).getTitle();
        artist = songList.get(position).getArtist();

        // если у песни есть картинка, то ставится она
        // в противном случае остаётся по умалчанию
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(path);
        byte[] cover = metaRetriver.getEmbeddedPicture();
        if (cover != null) {
            Bitmap image = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            ImageView imgView = (ImageView) findViewById(R.id.songImage);
            imgView.setImageBitmap(image);
        }
        else {
            Bitmap pauseImage = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
            ImageView imgPlayPause = (ImageView) findViewById(R.id.pausePlay);
            imgPlayPause.setImageBitmap(pauseImage);
        }

        // начало проигрывания
        mPlayer.start();

        // устанавливаем заголовок
        TextView titleTextView = (TextView)findViewById(R.id.songTitle);
        titleTextView.setText(songTitle);
        titleTextView.setSelected(true);

        // устанавливаем артиста
        if (artist.equals("<unknown>")) {
            artist = songTitle.split(" ")[0];
        }
        TextView artistTextView = (TextView)findViewById(R.id.songArtist);
        artistTextView.setText(artist);

        // устанавливаем значение прокрутки
        seekBar.setProgress((int)startTime);
        // создаём поток
        initializeSeekBar();
    }

    @Override
    public void onBackPressed() {
        // если нажата стрелочка назад, то
        // останавливаем песню и возвращаемся на главный экран
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onBack(View view) {
        // если нажата стрелочка назад, то
        // останавливаем песню и возвращаемся на главный экран
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void rewindLeft(View view) {
        if (position == 0){
            position = songList.size() - 1;
        }
        else {
            position -= 1;
        }
        initMusic(position);
    }

    public void pausePlay(View view) {
        Bitmap playImage = BitmapFactory.decodeResource(getResources(), R.drawable.play);
        Bitmap pauseImage = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        ImageView imgPlayPause = (ImageView) findViewById(R.id.pausePlay);
        if (play) {
            mPlayer.pause();
            imgPlayPause.setImageBitmap(playImage);
            play = false;
        }
        else {
            mPlayer.start();
            imgPlayPause.setImageBitmap(pauseImage);
            play = true;
        }
    }

    public void rewindRight(View view) {
        if (position == songList.size() - 1) {
            position = 0;
        }
        else {
            position += 1;
        }
        initMusic(position);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(mPlayer!=null && b){
            mPlayer.seekTo(i*1000);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
}
