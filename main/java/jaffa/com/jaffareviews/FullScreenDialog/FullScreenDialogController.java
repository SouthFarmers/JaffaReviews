package jaffa.com.jaffareviews.FullScreenDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
/**
 * Created by gautham on 11/17/17.
 */

public interface FullScreenDialogController {

    /**
     * Enable or disable the confirm button.
     *
     * @param enabled true to enable the button, false to disable it
     */
    void setConfirmButtonEnabled(boolean enabled);

    /**
     * Closes the dialog with a confirm action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnConfirmListener} will be called.
     *
     * @param result optional bundle with result data that will be passed to the
     *               {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnConfirmListener} callback
     */
    void confirm(@Nullable Bundle result);

    /**
     * Closes the dialog with a discard action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardListener} will be called.
     */
    void discard();

    /**
     * Closes de dialog from extra action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardFromExtraActionListener} will be called
     *
     * @param actionId menu item id to identify the action
     * @param result   optional bundle with result data that will be passed to the
     *                 {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardFromExtraActionListener} callback
     */
    void discardFromExtraAction(int actionId, @Nullable Bundle result);


}
