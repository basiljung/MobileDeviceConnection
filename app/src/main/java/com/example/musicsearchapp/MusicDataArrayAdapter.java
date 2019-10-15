package com.example.musicsearchapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MusicDataArrayAdapter extends ArrayAdapter<MusicData> {
    private Context mContext;
    private List<MusicData> musicDataList;

    public MusicDataArrayAdapter(Context context, ArrayList<MusicData> list) {
        super(context, 0, list);
        this.mContext = context;
        this.musicDataList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        ImageView imageView = listItem.findViewById(R.id.imageView);

        Glide
                .with(getContext())
                .load(musicDataList.get(position).getAlbumImage())
                .centerCrop()
                .override(300,300)
                .error(R.drawable.no_photo)
                .into(imageView);

        TextView artistView = listItem.findViewById(R.id.artistView);
        TextView titleView = listItem.findViewById(R.id.titleView);
        artistView.setText( musicDataList.get(position).getArtistName());
        titleView.setText(musicDataList.get(position).getSongTitle());

        return listItem;
    }
}
