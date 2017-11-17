package jaffa.com.jaffareviews.FullScreenDialog;

import android.view.MenuItem;

/**
 * Created by gautham on 11/17/17.
 */

public interface FullScreenDialogContent {
    /**
     * Called after the dialog has been initialized. It is invoked before the content onCreateView.
     *
     * @param dialogController that allows to control the container dialog
     */
    void onDialogCreated(FullScreenDialogController dialogController);

    /**
     * Called when the confirm button is clicked.
     *
     * @param dialogController allows to control the container dialog
     * @return true if the event has been consumed, false otherwise
     */
    boolean onConfirmClick(FullScreenDialogController dialogController);

    /**
     * Called when the discard button is clicked.
     *
     * @param dialogController allows to control the container dialog
     * @return true if the event has been consumed, false otherwise
     */
    boolean onDiscardClick(FullScreenDialogController dialogController);


}
