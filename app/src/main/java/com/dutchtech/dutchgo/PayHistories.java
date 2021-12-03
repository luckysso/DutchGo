package com.dutchtech.dutchgo;

import java.util.LinkedHashSet;
import java.util.Set;

//DAO

public class PayHistories {

    private final LinkedHashSet<PayHistory> payHistories = new LinkedHashSet<>();

    public void addPayHistory(PayHistory payHistory) {
        payHistories.add(payHistory);
    }

    public void removePayHistory(PayHistory payHistory) {
        payHistories.remove(payHistory);
    }

    public void clearPayHistories() {
        payHistories.clear();
    }

    public Set<PayHistory> getPayHistories() {
        return java.util.Collections.unmodifiableSet(payHistories);
    }

    public StringBuilder getPayHistoryText(){

        StringBuilder settlementInfo = new StringBuilder();
        for (PayHistory obj : payHistories) {
            settlementInfo.append("\n");
            settlementInfo.append("날짜 : ");
            settlementInfo.append(obj.getDate());
            settlementInfo.append("\n");
            settlementInfo.append("결제내역종류 : ");
            settlementInfo.append(obj.getPayKind());
            settlementInfo.append("\n");
            settlementInfo.append("금액 : ");
            settlementInfo.append(obj.getCost());
            settlementInfo.append("원");
            settlementInfo.append("\n");
            settlementInfo.append("참석 : ");
            String prefix = "";
            for (String str : obj.getAttendees()) {
                settlementInfo.append(prefix);
                prefix = ", ";
                settlementInfo.append(str);
            }
            settlementInfo.append("\n");
            settlementInfo.append("결제 : ");
            settlementInfo.append(obj.getPayer());
            settlementInfo.append("\n");
        }
        return settlementInfo;
    }

    /*
    @NonNull
    @Override
    protected LinkedHashSet<PayHistory> clone(){
        PayHistories clone= null;
        try{
            clone=(PayHistories) super.clone();
            LinkedHashSet<PayHistory> copy=new LinkedHashSet<>();
            for (PayHistory payHistory : payHistories) {
                copy.add(payHistory.clone());
            }
            clone.payHistories=copy;
        }catch (CloneNotSupportedException e){
            Log.d("error",e.toString());
        }
        assert clone != null;
        return clone;
    }
    */
}