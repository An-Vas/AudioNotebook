package an.vas.audionotebook.recorder;


import android.app.Activity;
import android.content.Context;

public interface Recordable {

    String getFileName();

    void startRecording(Context context, Activity activity) ;
    void pauseRecording(String recordingName);

}
