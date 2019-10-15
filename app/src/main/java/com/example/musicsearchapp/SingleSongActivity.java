package com.example.musicsearchapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SingleSongActivity extends AppCompatActivity implements Runnable {

    boolean audioIsPlaying;
    TextView songTitleView, artistNameView, albumNameView, songDurationView, seekBarHint;
    ImageView artistImageView, albumImageView;
    FloatingActionButton actionButton;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    MusicData musicData = new MusicData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData(getIntent());
        setTitle(musicData.getSongTitle());

        songTitleView = findViewById(R.id.songTitleView);
        artistNameView = findViewById(R.id.artistNameView);
        artistImageView = findViewById(R.id.artistImageView);
        albumNameView = findViewById(R.id.albumNameView);
        albumImageView = findViewById(R.id.albumImageView);
        songDurationView = findViewById(R.id.songDurationView);

        actionButton = findViewById(R.id.floatingButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong();
            }
        });
        seekBarHint = findViewById(R.id.seekBarHint);
        seekBar = findViewById(R.id.progressBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10) {
                    seekBarHint.setText("0:0" + x);
                } else {
                    seekBarHint.setText("0:" + x);
                }

                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    actionButton.setImageDrawable(ContextCompat.getDrawable(SingleSongActivity.this, android.R.drawable.ic_media_play));
                    SingleSongActivity.this.seekBar.setProgress(0);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        artistImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artistImageView.startAnimation(AnimationUtils.loadAnimation(SingleSongActivity.this, R.anim.imageclick_animation1));
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(musicData.getArtistLink())));
            }
        });

        albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumImageView.startAnimation(AnimationUtils.loadAnimation(SingleSongActivity.this, R.anim.imageclick_animation2));
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(musicData.getLinkToDeezer())));
            }
        });
        printData(musicData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadData(Intent intent) {
        musicData.setArtistName(intent.getStringExtra("artistName"));
        musicData.setArtistImage(intent.getStringExtra("artistImage"));
        musicData.setArtistLink(intent.getStringExtra("artistLink"));
        musicData.setSongTitle(intent.getStringExtra("title"));
        musicData.setLinkToDeezer(intent.getStringExtra("linkToDeezer"));
        musicData.setDuration(intent.getIntExtra("duration", 0));
        musicData.setPreviewLink(intent.getStringExtra("previewLink"));
        musicData.setAlbumName(intent.getStringExtra("albumName"));
        musicData.setAlbumImage(intent.getStringExtra("albumImage"));
    }

    public void printData(MusicData musicData) {
        songTitleView.setText(musicData.getSongTitle());
        artistNameView.setText(musicData.getArtistName());
        albumNameView.setText(musicData.getAlbumName());
        songDurationView.setText(musicData.getDuration() + " seconds");

        Glide
                .with(getApplicationContext())
                .load(musicData.getAlbumImage())
                .fitCenter()
                .error(R.drawable.no_photo)
                .into(albumImageView);

        Glide
                .with(getApplicationContext())
                .load(musicData.getArtistImage())
                .fitCenter()
                .error(R.drawable.no_photo)
                .into(artistImageView);
    }

    public void playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                audioIsPlaying = true;
                actionButton.setImageDrawable(ContextCompat.getDrawable(SingleSongActivity.this, android.R.drawable.ic_media_play));
            }

            if (!audioIsPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                actionButton.setImageDrawable(ContextCompat.getDrawable(SingleSongActivity.this, android.R.drawable.ic_media_pause));
                mediaPlayer.setDataSource(musicData.getPreviewLink());
                mediaPlayer.prepare();
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                new Thread(this).start();
            }
            audioIsPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();

        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }

    private void clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }
}
