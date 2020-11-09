package com.example.mymediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    // массив песен
    private ArrayList<Song> songList;
    // элементы списка
    private ListView songView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // получаем список
        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        // создаём список
        getSongList();

        // добавляем слушателя по нажатию
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // получаем позицию песни
                Song currSong = songList.get(position);
                Log.d("Song", "click song title: " + currSong.getTitle());

                // перенаправляем на другую активность
                Intent intent = new Intent(MainActivity.this, PlaySong.class);

                // записываем заголовок, артиста, путь песни
//                intent.putExtra("songTitle", currSong.getTitle());
//                intent.putExtra("songArtist", currSong.getArtist());
//                intent.putExtra("songPath", currSong.getPath());
//                intent.putExtra("songPosition", position);
                Bundle args = new Bundle();
                args.putParcelableArrayList("songList", songList);
                intent.putExtras(args);
                intent.putExtra("position", position);
//                PlaySong play = new PlaySong();
//                play.setArguments(args);
//                intent.putExtra("songList", songList);
                startActivity(intent);
            }
        });
    }

    public void getSongList() {
        // получаем доступ к базе данных
        ContentResolver musicResolver = getContentResolver();
        // получаем uri медиафайлов
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // получение в виде таблицы всех аудиофайлов
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            // получаем колонки с индексом, названием, артистом
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            //add songs to list
            // пока можно перемещаться по списку
            while (musicCursor.moveToNext()) {
                // перешли на следующую строку
                // и считываем данные
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisData = musicCursor.getString(dataColumn);
                // создаём класс песни и добовляем его в массив
                songList.add(new Song(thisTitle, thisArtist, thisData));
            }
        }
        // создаём свой адаптер и передаём ему параметры
        SongAdapter songAdt = new SongAdapter(this, songList);
        // устанавливаем адаптер
        songView.setAdapter(songAdt);
    }
}