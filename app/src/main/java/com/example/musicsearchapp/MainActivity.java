package com.example.musicsearchapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;
import org.json.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    EditText editText;
    Spinner spinner;
    Button button;
    String searchString, filterString;
    MusicDataArrayAdapter musicDataArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editText);
        spinner = findViewById(R.id.spinner);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString() != null) {
                    searchString = editText.getText().toString();
                } else {
                    searchString = "";
                }
                filterString = String.valueOf(spinner.getSelectedItem());
                List<String> data = new ArrayList<>();
                data.add(searchString);
                data.add(filterString);
                new ListViewTask().execute(data);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MusicData musicData = (MusicData) listView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, SingleSongActivity.class);
                intent.putExtra("artistName", musicData.getArtistName());
                intent.putExtra("artistImage", musicData.getArtistImage());
                intent.putExtra("artistLink", musicData.getArtistLink());
                intent.putExtra("title", musicData.getSongTitle());
                intent.putExtra("linkToDeezer", musicData.getLinkToDeezer());
                intent.putExtra("duration", musicData.getDuration());
                intent.putExtra("previewLink", musicData.getPreviewLink());
                intent.putExtra("albumName", musicData.getAlbumName());
                intent.putExtra("albumImage", musicData.getAlbumImage());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (spinner.getVisibility() == View.VISIBLE || editText.getVisibility() == View.VISIBLE || button.getVisibility() == View.VISIBLE) {
            spinner.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        } else {
            spinner.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }
        return true;
    }

    class ListViewTask extends AsyncTask<List<String>, Void, Void> {
        ArrayList<MusicData> musicDataArrayList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Loading data...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(List<String>... params) {
            String searchString = params[0].get(0).toLowerCase();
            String filterString = params[0].get(1).toLowerCase();
            JSONParser jsonParser = new JSONParser();
            String url = "https://api.deezer.com/search?q=" + filterString + ":'" + searchString + "'";
            String response = jsonParser.makeServiceCall(url);

            if (response != null) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray data  = obj.getJSONArray("data");

                   /* if (data.length() == 0) {
                        Toast.makeText(getApplicationContext(), "No results for the selected", Toast.LENGTH_LONG).show();
                    }*/

                    for (int i = 0; i < data.length(); i++) {
                        MusicData musicData = new MusicData();
                        JSONObject songData = data.getJSONObject(i);

                        if (songData.has("title")) {
                            musicData.setSongTitle(songData.getString("title"));
                        } else {
                            musicData.setSongTitle("-");
                        }

                        if (songData.has("link")) {
                            musicData.setLinkToDeezer(songData.getString("link"));
                        } else {
                            musicData.setLinkToDeezer("-");
                        }

                        if (songData.has("duration")) {
                            musicData.setDuration(songData.getInt("duration"));
                        } else {
                            musicData.setDuration(0);
                        }

                        if (songData.has("preview")) {
                            musicData.setPreviewLink(songData.getString("preview"));
                        } else {
                            musicData.setPreviewLink("-");
                        }

                        if (songData.has("artist")) {
                            JSONObject artistObj = songData.getJSONObject("artist");
                            if (artistObj.has("name")) {
                                musicData.setArtistName(artistObj.getString("name"));
                            } else {
                                musicData.setArtistName("-");
                            }

                            if (artistObj.has("link")) {
                                musicData.setArtistLink(artistObj.getString("link"));
                            } else {
                                musicData.setArtistLink("-");
                            }

                            if (artistObj.has("picture_big")) {
                                musicData.setArtistImage(artistObj.getString("picture_big"));
                            } else {
                                musicData.setArtistImage("-");
                            }
                        }

                        if (songData.has("album")) {
                            JSONObject albumObj = songData.getJSONObject("album");
                            if (albumObj.has("title")) {
                                musicData.setAlbumName(albumObj.getString("title"));
                            } else {
                                musicData.setAlbumName("-");
                            }

                            if (albumObj.has("cover_big")) {
                                musicData.setAlbumImage(albumObj.getString("cover_big"));
                            } else {
                                musicData.setAlbumImage("-");
                            }
                        }
                        musicDataArrayList.add(musicData);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"No data received. An error ocurred",Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            listView = findViewById(R.id.listView);
            musicDataArrayAdapter = new MusicDataArrayAdapter(MainActivity.this, musicDataArrayList);
            listView.setAdapter(musicDataArrayAdapter);
        }
    }
}
