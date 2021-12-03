package com.dutchtech.dutchgo;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Set;

public class OutputView {

    public static <T> void printMemByChecked(AppCompatActivity activity, FlexboxLayout layout, Set<String> members,Class<T> classToCreate) {
        layout.setPadding(50, 0, 50, 0);
        GenericInstance<T> a= new GenericInstance<>();
        for (String str : members) {
            T instance=a.create(classToCreate);
            if(instance!=null){
                ((TextView)instance).setText(str);
                layout.addView((View) instance);
            }
        }
    }

    public static void addCheckedItems(FlexboxLayout layout, Set<String> checkedItem) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (((CompoundButton) view).isChecked()) {
                String cbText = (String) ((TextView) view).getText();
                checkedItem.add(cbText);
            }
        }
    }
}
