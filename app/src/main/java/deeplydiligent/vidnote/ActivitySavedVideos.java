package deeplydiligent.vidnote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ActivitySavedVideos extends AppCompatActivity {

    private ListView listView1;
    private ArrayList<String> listIDs;
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private Context context = this;
    private int positioninlist;
    private ArrayList<String> data = new ArrayList<String>();
    private String prefsString;
    SharedPreferences prefs;

    private final String PREFS_NAME = "com.example.vidnote.VIDEONOTES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listItems = new ArrayList<String>();
        listIDs = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_videos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.saved_videos_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Saved Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView1 = (ListView) findViewById(R.id.menuList);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listView1.setAdapter(adapter);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Map<String, ?> keys = prefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            YoutubeData data = new YoutubeData();
            data.setOnResponseListener(new ResponseListener() {
                @Override
                public void onResponseReceive(String data, String id) {
                    listItems.add(data);
                    listIDs.add(id);
                    adapter.notifyDataSetChanged();
                }
            });
            data.execute(entry.getKey().substring(11));
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());

        }
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                getData(listIDs.get(positioninlist));
                positioninlist = position;
                new AlertDialog.Builder(context)
                        .setTitle("Options")
                        .setMessage("Would you like to play, delete or export the subtitles?")
                        .setPositiveButton("Play", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, video.class);
                                intent.putExtra("com.example.vidnote.MESSAGE", "https://m.youtube.com/watch?v=" + listIDs.get(positioninlist));
                                startActivity(intent);
                            }
                        }).setNegativeButton("Export", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String file = "";

                        for (String string : data) {
                            file += string;
                        }

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, file);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Please Select an App"));
                    }})
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prefs.edit().remove("textboxdata"+listIDs.get(positioninlist)).apply();
                                listItems.remove(positioninlist);
                                listIDs.remove(positioninlist);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .show();

            }
        });
    }

    public void getData(String vidID) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        data = new ArrayList<String>(1);
        ArrayList<ArrayList<String>> savedTasks = new ArrayList<ArrayList<String>>(1);
        try {
            prefsString = prefs.getString("textboxdata" + vidID, ObjectSearlizer.serialize(new ArrayList<ArrayList<String>>()));
            savedTasks = (ArrayList<ArrayList<String>>) ObjectSearlizer.deserialize(prefsString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        for (ArrayList<String> line : savedTasks) {
            data.add(line.get(0).toString() + "\t" + line.get(1).toString());
        }
//        textView.setText(textView.getText().toString().trim());
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
