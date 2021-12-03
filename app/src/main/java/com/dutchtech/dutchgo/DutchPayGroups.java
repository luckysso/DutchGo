package com.dutchtech.dutchgo;

import java.util.LinkedHashMap;
import java.util.Map;

//DAO

public class DutchPayGroups {

    private static DutchPayGroups instance;
    private static final LinkedHashMap<String,DutchPayGroup> dutchPayGroups=new LinkedHashMap<>();

    private DutchPayGroups(){

    }

    public static DutchPayGroups getInstance(){
        if(instance==null){
            instance=new DutchPayGroups();
        }
        return instance;
    }

    public Map<String, DutchPayGroup> getDutchPayGroups() {
        return java.util.Collections.unmodifiableMap(dutchPayGroups);
    }

    public DutchPayGroup getDutchPayGroup(String groupName) {
        return dutchPayGroups.get(groupName);
    }

    public void addDutchPayGroup(String groupName){
        DutchPayGroup dutchPayGroup=new DutchPayGroup();
        dutchPayGroups.put(groupName,dutchPayGroup);
    }

    public boolean isContainGroup(String groupName){
        return dutchPayGroups.containsKey(groupName);
    }


}
