package an.vas.audionotebook.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import an.vas.audionotebook.R;
import an.vas.audionotebook.onClickListeners.CompositeListener;

/**
 * RecordInfoView is a custom view to show info about one recording and allow to play|pause it.
 * Shows Recording name, date, full time and - while playing - current playing time
 */
public class RecordInfoView extends RelativeLayout {
    private MaterialButton playStopButton;
    private TextView recordName, recordDate, recoerdLength, recordPlayingTime, slashBetweenFullAndPlayedTime;
    private File file;


    public RecordInfoView(Context context) {
        super(context);
        init();
        findAllbyId();
    }

    public RecordInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        findAllbyId();
    }

    public RecordInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        findAllbyId();
    }


    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.record_info_view, this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        CompositeListener compositeListener = new CompositeListener();
        compositeListener.registerListener(onClickListener);

        OnClickListener buttonDesignOnClickListener = new OnClickListener()
        {

            @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables", "UseCompatLoadingForColorStateLists"})
            @Override
            public void onClick(View v)
            {
               if (playStopButton.isSelected()){
                   playStopButton.setSelected(false);
                   playStopButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.drawable.color_selecter_blue));
                   playStopButton.setIcon(getContext().getResources().getDrawable(R.drawable.baseline_play_arrow_24));
                   recordPlayingTime.setVisibility(View.GONE);
                   slashBetweenFullAndPlayedTime.setVisibility(View.GONE);
               } else if (!playStopButton.isSelected()){
                   playStopButton.setSelected(true);
                   playStopButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.drawable.color_selector_dark_gray));
                   playStopButton.setIcon(getContext().getResources().getDrawable(R.drawable.baseline_pause_24));
                   recordPlayingTime.setVisibility(View.VISIBLE);
                   slashBetweenFullAndPlayedTime.setVisibility(View.VISIBLE);
               }
            }
        };

        compositeListener.registerListener(buttonDesignOnClickListener);

        playStopButton.setOnClickListener(compositeListener);
    }

    public void setFile (File file){
        this.file = file;
    }

    public File getFile (){
        return file;
    }

    public void findAllbyId(){
        playStopButton = findViewById(R.id.micButtton);
        recordName = findViewById(R.id.trackNameTextView);
        recordDate = findViewById(R.id.trackDateTextView);
        recoerdLength = findViewById(R.id.fullTrackTimeTextView);
        recordPlayingTime = findViewById(R.id.playingTrackTimeTextView);
        slashBetweenFullAndPlayedTime = findViewById(R.id.timeSlashTextView);
        playStopButton.setSelected(false);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        setOnClickListener(onClickListener);

    }


    public void setRecordName(String title){
        recordName.setText(title);
    }

    public void setRecordDate(Date date){
        LocalDateTime datetime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String newstring = datetime.format(DateTimeFormatter.ofPattern("dd.MM.yy в hh:mm"));

        recordDate.setText(newstring);
    }

    public void setRecordFullTime(int time_ms){
        int min = time_ms / (1000*60);
        int sec = time_ms / 1000;

        String minStr = String.format("%02d", min);
        String secStr = String.format("%02d", sec);

        recoerdLength.setText(minStr + ":" + secStr);
    }

    public void setRecordPlayingTime(int time_ms){
        int min = time_ms / (1000*60);
        int sec = time_ms / 1000;

        String minStr = String.format("%02d", min);
        String secStr = String.format("%02d", sec);

        recordPlayingTime.setText(minStr + ":" + secStr);
    }


    public boolean isSelected(){
        return playStopButton.isSelected();
    }


    public boolean performClick(){
        playStopButton.performClick();
        return true;
    }

    public void postRecordPlayingTime(int time_ms){
        recordPlayingTime.post(new Runnable() {
            @Override
            public void run() {
                setRecordPlayingTime(50000);
                int min = time_ms / (1000*60);
                int sec = time_ms / 1000;

                String minStr = String.format("%02d", min);
                String secStr = String.format("%02d", sec);
                recordPlayingTime.setText(minStr + ":" + secStr);
            }
        });
    }
}
