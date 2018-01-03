package deeplydiligent.vidnote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;
import java.sql.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class video extends AppCompatActivity {
    private YouTubePlayerView player;
    public TextView textView;
    public EditText editText;
    private final String PREFS_NAME = "com.example.vidnote.VIDEONOTES";
    private boolean timestampAll = true;
    private boolean stampWhenTypingStarts = false;
    private int secondsToSkip = 5;
    private int secondsToSkipBack = 5;


    public YouTubePlayer getActualPlayer() {
        return actualPlayer;
    }

    private YouTubePlayer actualPlayer;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private Button playPause;
    private Button saveButton;
    private Button plus5;
    private Button minus5;
    private Toolbar myToolbar;
    ArrayList<String> linesInEditText = new ArrayList<>(100);
    String vidUrl;


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSavedNotes();
        initNoteController();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = savedInstanceState;
        setContentView(R.layout.activity_video);


        YouTubePlayerSupportFragment player = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_video_requested, player).commit();


        Intent intent = getIntent();
        vidUrl = intent.getStringExtra("com.example.vidnote.MESSAGE");
        vidUrl = vidUrl.substring(vidUrl.length()-11,vidUrl.length());

        plus5 = (Button) findViewById(R.id.button_plus5);
        minus5 = (Button) findViewById(R.id.button_minus5);

        myToolbar = (Toolbar) findViewById(R.id.video_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().hide();

        updateToolbarTitle();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.editTextNotes);
        final View activityRootView = findViewById(R.id.top_root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                Log.d("lmao",Integer.toString(heightDiff));

                if (heightDiff > 200) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
            }
        });

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                actualPlayer = youTubePlayer;
//                actualPlayer.setShowFullscreenButton(false);
                youTubePlayer.loadVideo(vidUrl);
                actualPlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                    @Override
                    public void onPlaying() {
                        setPlayButtonText();
                    }

                    @Override
                    public void onPaused() {
                        setPlayButtonText();
                    }

                    @Override
                    public void onStopped() {
                        setPlayButtonText();
                    }

                    @Override
                    public void onBuffering(boolean b) {

                    }

                    @Override
                    public void onSeekTo(int i) {

                    }
                });
                actualPlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {
                        setPlayButtonText();
                        plus5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                actualPlayer.seekRelativeMillis(secondsToSkip*1000);
                            }
                        });
                        minus5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                actualPlayer.seekRelativeMillis(secondsToSkipBack*-1000);
                            }
                        });


                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {
                        if (bundle != null) {
                            actualPlayer.seekToMillis(bundle.getInt("vidposition",0));
                            actualPlayer.play();
                        }
                    }

                    @Override
                    public void onVideoEnded() {

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }
                });

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        playPause = (Button) findViewById(R.id.button_playpause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (actualPlayer.isPlaying()){
                    actualPlayer.pause();
                }else{
                    actualPlayer.play();
                }
                setPlayButtonText();
            }
        });

        saveButton = (Button) findViewById(R.id.button_save);
        Drawable saveButtonDrawable;
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            saveButtonDrawable = this.getResources().getDrawable(R.drawable.ic_save_black_24dp, this.getTheme());
        } else {
            saveButtonDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.ic_save_black_24dp, this.getTheme());
        }
        saveButton.setCompoundDrawablesWithIntrinsicBounds(saveButtonDrawable, null, null, null);;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotesNow();
            }
        });


        player.initialize("AIzaSyC7WB1t3Kvk-WwMfyVLRCaJr6JMv8NUmnI", onInitializedListener);

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("vidposition", actualPlayer.getCurrentTimeMillis());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void getSavedNotes(){
        textView = (TextView) findViewById(R.id.textViewNotes);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        try {
            if (prefs.contains("textboxdata"+vidUrl)){
                ArrayList<ArrayList<String>> savedTasks = (ArrayList<ArrayList<String>>)
                        ObjectSearlizer.deserialize(prefs.getString("textboxdata"+vidUrl,
                                ObjectSearlizer.serialize(new ArrayList<ArrayList<String>>())));
                textView.setText("");
                editText.setText("");
                linesInEditText = new ArrayList<>(1);
                for (ArrayList<String> line: savedTasks) {
                    textView.setText(textView.getText()+line.get(0)+"\n");
                    editText.setText(editText.getText()+line.get(1));
                    linesInEditText.add(line.get(0));
                }
                textView.setText(textView.getText().toString().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void initNoteController(){
        textView = (TextView) findViewById(R.id.textViewNotes);
        editText = (EditText) findViewById(R.id.editTextNotes);
        editText.addTextChangedListener(new TextWatcher() {
            int linesbefore;
            String lineTextBefore;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                linesbefore = editText.getLineCount();
                lineTextBefore = editText.getText().toString().substring(
                        editText.getLayout().getLineStart(getCurrentCursorLine(editText)) ,
                        editText.getLayout().getLineEnd(getCurrentCursorLine(editText)));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.UK);

                Date date = new Date(getActualPlayer().getCurrentTimeMillis());
                String time = formatter.format(date);
                refreshEditText(linesInEditText,time,linesbefore,lineTextBefore);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }


    public void refreshEditText(ArrayList linesInEditText, String time, int linesbefore, String lineTextBefore){
        if (editText.getText().toString().length() == 1){
            linesInEditText.clear();
            linesInEditText.add(0,time);
        } else if (linesbefore != editText.getLineCount()){
            if (linesbefore>editText.getLineCount()){
                linesInEditText.remove(getCurrentCursorLine(editText)+1);
            } else if (linesbefore<editText.getLineCount()) {
                linesInEditText.add(getCurrentCursorLine(editText),timestampAll ? time:"");
            }
        } else if (lineTextBefore.equals("") && editText.getText().toString().length()>0 && stampWhenTypingStarts){
            linesInEditText.set(getCurrentCursorLine(editText),time);
        }
        textView.setText(joinArrayListString(linesInEditText,"\n"));
        //TODO: FIX THE EDITOR :D
    }

    public int getCurrentCursorLine(EditText editText)
    {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart);
        }

        return -1;
    }

    public String joinArrayListString(ArrayList<String> r, String delimiter) {
        if(r == null || r.size() == 0 ){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int i, len = r.size() - 1;
        for (i = 0; i < len; i++){
            sb.append(r.get(i) + delimiter);
        }
        return sb.toString() + r.get(i);
    }

    public void updateToolbarTitle(){
        YoutubeData data = new YoutubeData();
        data.setOnResponseListener(new ResponseListener() {
            @Override
            public void onResponseReceive(String data, String id) {
                myToolbar.setTitle(data);
            }
        });
        data.execute(vidUrl);
    }
    public String saveNotesNow(){

        ArrayList<ArrayList<String>> parseInput = new ArrayList<>(100);

        for (int i=0; i<linesInEditText.size();i++){
            ArrayList<String> line = new ArrayList<>(2);
            line.add(linesInEditText.get(i));
            line.add(editText.getText().toString().substring(
                    editText.getLayout().getLineStart(i) ,
                    editText.getLayout().getLineEnd(i)));
            parseInput.add(line);
        }

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putString("textboxdata"+vidUrl, ObjectSearlizer.serialize(parseInput));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
        Toast toast = Toast.makeText(this, "All Saved :)", Toast.LENGTH_SHORT);
        toast.show();
        return null;
    }

    public void setPlayButtonText(){
        Drawable drawable_pause;
        Drawable drawable_play;

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            drawable_pause = this.getResources().getDrawable(R.drawable.ic_pause_black_24dp, this.getTheme());
            drawable_play = this.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp, this.getTheme());
        } else {
            drawable_pause = VectorDrawableCompat.create(this.getResources(), R.drawable.ic_pause_black_24dp, this.getTheme());
            drawable_play = VectorDrawableCompat.create(this.getResources(), R.drawable.ic_play_arrow_black_24dp, this.getTheme());
        }


        if (actualPlayer.isPlaying()){
            playPause.setCompoundDrawablesWithIntrinsicBounds(drawable_pause, null, null, null);;
        }else{
            playPause.setCompoundDrawablesWithIntrinsicBounds(drawable_play, null, null, null);;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_menu, menu);
        return true;
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        if (!editText.getText().toString().equals("")){
            saveNotesNow();
        }
        super.onPause();
    }
}
