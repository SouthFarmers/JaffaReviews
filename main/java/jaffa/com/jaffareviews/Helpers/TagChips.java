package jaffa.com.jaffareviews.Helpers;

import android.content.Context;
import android.util.AttributeSet;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.views.ChipsInputEditText;

import jaffa.com.jaffareviews.R;

public class TagChips extends ChipsInput {

    public TagChips(Context context) {
        super(context);
    }

    public TagChips(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onTextChanged(CharSequence text) {}

    @Override
    public ChipsInputEditText getEditText() {
        ChipsInputEditText editText = new ChipsInputEditText(getContext());
        editText.clearFocus();
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        return editText;
    }
}
