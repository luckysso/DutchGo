package com.dutchtech.dutchgo;

import android.view.View;
import java.util.Map;

public class GroupOnclickListener implements View.OnClickListener {

    private static GroupOnclickListener instance;
    private static final DutchPayGroups dutchPayGroups=DutchPayGroups.getInstance();

    private GroupOnclickListener(){

    }

    public static GroupOnclickListener getInstance(){
        if(instance==null){
            instance=new GroupOnclickListener();
        }
        return instance;
    }

    @Override
    public void onClick(View v){
        Map<String,DutchPayGroup> dutchPayGroup=dutchPayGroups.getDutchPayGroups();
        String groupName=v.getTag().toString();
        if(dutchPayGroup.containsKey(groupName)) {
             MainActivity.setCurrentDutchPayGroup(dutchPayGroup.get(groupName));
        }
    }
}

