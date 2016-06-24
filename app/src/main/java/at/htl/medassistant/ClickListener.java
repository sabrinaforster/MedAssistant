package at.htl.medassistant;

import android.view.View;

/**
 * Created by Sabrina on 12.06.2016.
 */
public interface ClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
