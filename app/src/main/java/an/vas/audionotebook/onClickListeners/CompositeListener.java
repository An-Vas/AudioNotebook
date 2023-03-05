package an.vas.audionotebook.onClickListeners;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * CompositeListener implements View.OnClickListener and it allows you to put several actions from
 * different code fragments on one button.
 */
public class CompositeListener implements View.OnClickListener {

    private List<View.OnClickListener> registeredListeners = new ArrayList<View.OnClickListener>();

    public void registerListener (View.OnClickListener listener) {
        registeredListeners.add(listener);
    }

    @Override
    public void onClick(View v) {

        for(View.OnClickListener listener:registeredListeners) {
            listener.onClick(v);
        }
    }
}