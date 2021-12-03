package com.dutchtech.dutchgo;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

//DTO

public class PayHistory implements Serializable {

    private final LinkedHashSet<String> attendees;
    private final String payer;
    private final String date;
    private final String payKind;
    private final int cost;

    PayHistory(LinkedHashSet<String> attendees, String payer, String date, String payKind, int cost) {
        this.attendees = attendees;
        this.payer = payer;
        this.date = date;
        this.payKind = payKind;
        this.cost = cost;
    }

    public int getAttendeesSize() {
        return attendees.size();
    }

    public Set<String> getAttendees() {
        return java.util.Collections.unmodifiableSet(attendees);
    }

    public String getPayer() {
        return payer;
    }

    public String getDate() {
        return date;
    }

    public String getPayKind() {
        return payKind;
    }

    public int getCost() {
        return cost;
    }


    /*
    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    protected PayHistory clone() {
        PayHistory clone=null;
        try{
            clone=(PayHistory) super.clone();
            clone.attendees= (LinkedHashSet<String>) attendees.clone();
        }catch (CloneNotSupportedException e){
            Log.d("error",e.toString());
        }
        assert clone != null;
        return clone;
    }
     */
}
