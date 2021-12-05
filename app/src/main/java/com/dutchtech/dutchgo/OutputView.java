package com.dutchtech.dutchgo;

import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Set;

public class OutputView {

    public static void printMemByAttendee(AppCompatActivity activity, FlexboxLayout layout, Set<String> members) {
        layout.setPadding(50, 0, 50, 0);
        for (String str : members) {
            CheckBox cb = new CheckBox(activity);
            cb.setText(str);
            layout.addView(cb);
        }
    }
}
