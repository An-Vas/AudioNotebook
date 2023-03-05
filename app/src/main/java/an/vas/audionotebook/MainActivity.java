package an.vas.audionotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.io.File;

import java.util.HashSet;

import an.vas.audionotebook.onClickListeners.MicBtnOnClickListener;
import an.vas.audionotebook.recorder.AudioRecorder;
import an.vas.audionotebook.recorder.Recordable;
import an.vas.audionotebook.ui.FilesLoader;
import an.vas.audionotebook.ui.RecordInfoView;

public class MainActivity extends AppCompatActivity {

    private MaterialButton micButton;
    private TextView noRecordingTV;
    private LinearLayout scrollViewLinearLayout;
    
    private MediaPlayer mediaPlayer;
    private Recordable recorder;

    private HashSet<RecordInfoView> recordInfoViewHashSet;
    private String curRecordName = "";
    private String defaultRecordName = "Audio";
    private final String storage = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AudioNotebook";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noRecordingTV = findViewById(R.id.noRecordingsTV);
        scrollViewLinearLayout = findViewById(R.id.mainMenuScrollLinearLayout);
        micButton = findViewById(R.id.micButtton);
        micButton.setSelected(false);

        // Creating directory for saving audioNotes, if it is not exist
        new File(storage).mkdirs();

        recorder = new AudioRecorder( storage, defaultRecordName);
        defaultRecordName = recorder.getFileName();

        recordInfoViewHashSet = new HashSet<>();


        new FilesLoader(getApplicationContext(), MainActivity.this, scrollViewLinearLayout, noRecordingTV, recordInfoViewHashSet, storage).updateFiles();

        micButton.setOnClickListener(new MicBtnOnClickListener(getApplicationContext(), MainActivity.this,micButton, recorder, curRecordName, storage,  scrollViewLinearLayout, noRecordingTV, recordInfoViewHashSet));

    }

    @Override
    public void onResume(){
        super.onResume();
        // If the user collapses the application and deletes files from the folder or the entire folder, then it is needed to restore it
        new File(storage).mkdirs();
        new FilesLoader(getApplicationContext(), MainActivity.this, scrollViewLinearLayout, noRecordingTV, recordInfoViewHashSet, storage).updateFiles();
    }

}