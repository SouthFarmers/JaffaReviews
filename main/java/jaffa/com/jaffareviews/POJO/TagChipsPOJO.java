package jaffa.com.jaffareviews.POJO;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

public class TagChipsPOJO implements ChipInterface {


    private String name;

    public TagChipsPOJO(String name) {
        this.name = name;
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getInfo() {
        return null;
    }
}
