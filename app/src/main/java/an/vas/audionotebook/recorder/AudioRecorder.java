package an.vas.audionotebook.recorder;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

/**
 * Audio recorder is adapter to mediaRecoerder with adding some functional
 */
public class AudioRecorder implements Recordable{

    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private MediaRecorder mRecorder;
    private String fileName = null;

    public AudioRecorder(String filePath, String fileName){

        File result = new File(new File(filePath), fileName + ".3gp");

        // if file with such name already exist, we add (1) to the file name to avoid removing old file.
        while (result.exists()){
            fileName+=" (1)";
            result = new File(new File(filePath), fileName + ".3gp");
        }

        this.fileName = result.getAbsolutePath();
    }

    public String getFileName(){
        return fileName;
    }

    public void startRecording(Context context, Activity activity) {

        if (CheckPermissions(context)) {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(fileName);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("ERROR", "prepare() failed");
            }
            mRecorder.start();

        } else {
            RequestPermissions(activity);
        }
    }

    public void pauseRecording(String recordingName) {
        if (mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private boolean CheckPermissions(Context context) {
        int writePermission = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int recordPermission = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);

        return writePermission == PackageManager.PERMISSION_GRANTED && recordPermission == PackageManager.PERMISSION_GRANTED;
    }


    private void RequestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

}
