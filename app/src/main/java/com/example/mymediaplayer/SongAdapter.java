package com.example.mymediaplayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // создаём View элемент
        // используя шаблон song.xml
        LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.song, parent, false);
        // получаем textview из song.xml
        TextView songView = (TextView) songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        // получаем песню по позиции
        Song currSong = songs.get(position);
        // устанавливаем название и артиста
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        // размещаем элемент в списке
        songLay.setTag(position);
        return songLay;
    }
}
