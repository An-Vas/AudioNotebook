package an.vas.audionotebook.onClickListeners;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.HashSet;

import an.vas.audionotebook.R;
import an.vas.audionotebook.inputFilters.RecordNameInputFilter;
import an.vas.audionotebook.recorder.AudioRecorder;
import an.vas.audionotebook.recorder.Recordable;
import an.vas.audionotebook.ui.FilesLoader;
import an.vas.audionotebook.ui.RecordInfoView;

/**
 * MicBtnOnClickListener implements View.OnClickListener and and implements the process of
 * recording audio and the process of subsequently saving information about the file
 */
public class MicBtnOnClickListener implements View.OnClickListener {
    private MaterialButton micButton;
    private Context context;
    private Recordable recorder;
    private HashSet<RecordInfoView> recordInfoViewHashSet;
    private Activity activity;
    private LinearLayout scrollViewLinearLayout;
    private TextView noRecordingsTV;
    private String curRecordName;
    private String defaultFileName;
    private File defaultFile;
    private String storage;

    public MicBtnOnClickListener (Context context,
                                  Activity activity,
                                  MaterialButton micButton,
                                  Recordable recorder,
                                  String curRecordName,
                                  String storage,
                                  LinearLayout scrollViewLinearLayout,
                                  TextView noRecordingsTV,
                                  HashSet<RecordInfoView> recordInfoViewHashSet){
        this.micButton = micButton;
        this.recorder = recorder;
        this.curRecordName = curRecordName;
        this.storage = storage;
        this.noRecordingsTV = noRecordingsTV;
        this.recordInfoViewHashSet = recordInfoViewHashSet;
        this.scrollViewLinearLayout = scrollViewLinearLayout;
        this.context = context;
        this.activity = activity;
        defaultFileName = context.getResources().getString(R.string.default_recording_name);
    }


    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        if (!micButton.isSelected()){
            micButton.setSelected(true);
            micButton.setBackgroundTintList(context.getResources().getColorStateList(R.drawable.color_selector_dark_gray));
            micButton.setIcon(context.getResources().getDrawable(R.drawable.baseline_stop_24));

            // If one of recordings is being played it should be stopped
            for (RecordInfoView rv : recordInfoViewHashSet){
                if (rv.isSelected()){
                    rv.performClick();
                }
            }
            recorder = new AudioRecorder( Environment.getExternalStorageDirectory().getAbsolutePath()+"/AudioNotebook", "Audio");
            recorder.startRecording(context, activity);

        } else {

            micButton.setSelected(false);
            micButton.setBackgroundTintList(context.getResources().getColorStateList(R.drawable.color_selecter_blue));
            micButton.setIcon(context.getResources().getDrawable(R.drawable.baseline_mic_24));

            curRecordName = context.getResources().getString(R.string.default_recording_name) + ".3gp";
            defaultFile = new File(storage + "/" + curRecordName);
            showEnterNameAlertDialog();

            recorder.pauseRecording(storage + "/" + curRecordName);
            defaultFile = new File(recorder.getFileName());

            new FilesLoader(context, activity, scrollViewLinearLayout, noRecordingsTV, recordInfoViewHashSet, storage).updateFiles();
        }
    }


    private void showEnterNameAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.enter_record_name);

        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.new_track_input_name_dialog, null);

        final EditText input = dialogView.findViewById(R.id.et_track_name);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter filter = new RecordNameInputFilter();
        input.setFilters(new InputFilter[]{filter});

        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                curRecordName = input.getText().toString().trim();
                // if input string == "" recording will have default name
                if (TextUtils.isEmpty(curRecordName)){
                    return;
                }
                File file2 = new File (Environment.getExternalStorageDirectory().getAbsolutePath()+"/AudioNotebook/"+ curRecordName +".3gp");

                // if file with this name already exist, we add (1) to the and of the name
                while (file2.exists()){
                    curRecordName +=" (1)";
                    file2 = new File (Environment.getExternalStorageDirectory().getAbsolutePath()+"/AudioNotebook/"+ curRecordName +".3gp");
                }

                defaultFile.renameTo(file2);

                new FilesLoader(context, activity, scrollViewLinearLayout, noRecordingsTV, recordInfoViewHashSet, storage).updateFiles();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setBackgroundColor(context.getResources()
                    .getColor(R.color.blue));

            positiveButton.setTextColor(context.getResources()
                    .getColor(android.R.color.white));
        }
    }
}
