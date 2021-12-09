package com.dutchtech.dutchgo;

import java.util.LinkedHashSet;
import java.util.Set;

//DAO

public class DutchPayGroup {
    private final LinkedHashSet<String> members = new LinkedHashSet<>();
    private final PayHistories payHistories = new PayHistories();
    private final SettlementHistory settlementHistory=new SettlementHistory();
    private String groupName;

    public DutchPayGroup(String groupName){
        this.groupName=groupName;
    }

    public void modifyGroupName(String groupName){
        this.groupName=groupName;
    }

    public String getGroupName(){
        return groupName;
    }

    public Set<String> getMembers() {
        return java.util.Collections.unmodifiableSet(members);
    }

    public boolean isEmptyMembers() {
        return members.isEmpty();
    }

    public void addMember(String str) {
        members.add(str);
    }

    public void removeMember(String str) {
        members.remove(str);
    }

    public void clearMembers() {
        members.clear();
    }

    public Set<PayHistory> getPayHistories()  {
        return payHistories.getPayHistories();
    }

    public boolean isEmptyPayHistories(){
        return payHistories.getPayHistories().isEmpty();
    }

    public void clearPayHistories() {
        payHistories.clearPayHistories();
    }

    public void addPayHistory(PayHistory payHistory) {
        payHistories.addPayHistory(payHistory);
    }

    public void removePayHistory(PayHistory payHistory) {
        payHistories.removePayHistory(payHistory);
    }

    public StringBuilder getSettlementHistoryText(){
        return settlementHistory.getSettlementHistoryText(members);
    }

    public StringBuilder getPayHistoryText(){
        return payHistories.getPayHistoryText();
    }

    public String getMembersText() {
        StringBuilder memberText = new StringBuilder();
        String prefix = "";
        for (String str : members) {
            memberText.append(prefix);
            prefix = ", ";
            memberText.append(str);
        }
        if(memberText.length()==0){
            return "";
        }
        return memberText.toString();
    }

    public void setSettlementHistory(){
        settlementHistory.setSettlementHistory(members,payHistories.getPayHistories());
    }

    public void setCostToSendOrReceiveByAttendee(){
        settlementHistory.setCostToSendOrReceiveByAttendee(members,payHistories.getPayHistories());
    }

}
