package an.vas.audionotebook.ui;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import org.apache.commons.io.FilenameUtils;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import an.vas.audionotebook.R;

/**
 * FilesLoader loads info about files from the app storage directory
 */
public class FilesLoader {
    private LinearLayout scrollViewLinearLayout;
    private TextView noRecordingsTV;
    private HashSet<RecordInfoView> recordInfoViewHashSet;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Context context;
    private Activity activity;
    private String storage;

    public FilesLoader (Context context,
                        Activity activity,
                        LinearLayout scrollViewLinearLayout,
                        TextView noRecordingsTV,
                        HashSet<RecordInfoView> recordInfoViewHashSet,
                        String storage){
        this.scrollViewLinearLayout = scrollViewLinearLayout;
        this.noRecordingsTV = noRecordingsTV;
        this.recordInfoViewHashSet = recordInfoViewHashSet;
        this.context = context;
        this.activity = activity;
        this.storage = storage;

    }

    public void updateFiles(){
        handler = new Handler(context.getMainLooper());
        String path = storage;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null){
            scrollViewLinearLayout.removeAllViews();
            recordInfoViewHashSet.removeAll(recordInfoViewHashSet);
            if (files.length == 0) {
                noRecordingsTV.setVisibility(View.VISIBLE);
                return;
            }
            noRecordingsTV.setVisibility(View.GONE);

            for (int i = 0; i < files.length; i++)
            {
                // Checking the case if user will place some files of unsuitable file extension to the storage
                if (FilenameUtils.getExtension(files[i].getAbsolutePath()).equals(".3gp"))
                    continue;

                RecordInfoView rv = new RecordInfoView(activity);
                scrollViewLinearLayout.addView(rv);
                recordInfoViewHashSet.add(rv);

                rv.setRecordName(files[i].getName());

                Date lastModDate = new Date(files[i].lastModified());

                MediaPlayer mp = MediaPlayer.create(context, Uri.parse(files[i].getAbsolutePath()));
                int duration = mp.getDuration(); //продолжительность аудио

                rv.setRecordFullTime(duration);
                rv.setRecordDate(lastModDate);
                rv.setFile(files[i]);

                int paddingDp = context.getResources().getDimensionPixelOffset(R.dimen.main_between_record_info_margin);
                rv.setPadding(0,0,0,paddingDp);



                String filename = files[i].getAbsolutePath();
                View.OnClickListener onClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!rv.isSelected()){
                            File file = new File(filename);
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(filename);
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            mediaPlayer.start();

                            Timer timer = new Timer();

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    rv.performClick();
                                    timer.cancel();
                                    timer.purge();
                                }
                            });

                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mediaPlayer != null /*&& mediaPlayer.isPlaying()*/) {

                                                rv.postRecordPlayingTime(mediaPlayer.getCurrentPosition());
                                            } else {
                                                timer.cancel();
                                                timer.purge();
                                            }
                                        }
                                    });
                                }
                            }, 0, 1000);
                        } else {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }
                        }
                    }
                };

                rv.setOnClickListener(onClickListener);

            }
        }
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }
}
