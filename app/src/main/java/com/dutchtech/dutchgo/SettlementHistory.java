package com.dutchtech.dutchgo;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;

//DAO

public class SettlementHistory {

    private final WeakHashMap<String, LinkedHashSet<String>> payKindByAttendee = new WeakHashMap<>();
    private final WeakHashMap<String, Integer> costByPayer = new WeakHashMap<>();
    private final WeakHashMap<String, WeakHashMap<String, Double>> costToSendOrReceiveByAttendee = new WeakHashMap<>();

    public void setPayKindsByAttendee(Set<String> members,Set<PayHistory> payHistories) {
        payKindByAttendee.clear();
        for (String member : members) {
            LinkedHashSet<String> payKind = new LinkedHashSet<>();
            payKindByAttendee.put(member, payKind);
        }

        for (PayHistory payHistory : payHistories) {
            for (String attendee : payHistory.getAttendees()) {
                LinkedHashSet<String> payKind = payKindByAttendee.get(attendee);
                if (payKind != null) {
                    payKind.add(payHistory.getPayKind());
                }
            }
        }

    }

    public void setCostByPayer(Set<String> members,Set<PayHistory> payHistories) {
        costByPayer.clear();
        int cost;
        for (String member : members) {
            cost = 0;
            for (PayHistory payHistory : payHistories) {
                if (payHistory.getPayer().equals(member)) {
                    cost += payHistory.getCost();
                }
            }
            costByPayer.put(member, cost);
        }
    }

    public void setSettlementHistory(Set<String> members,Set<PayHistory> payHistories){
        setPayKindsByAttendee(members,payHistories);
        setCostByPayer(members,payHistories);
    }

    public void setCostToSendOrReceiveByAttendee(LinkedHashSet<String> members,Set<PayHistory> payHistories){
        WeakHashMap<String, Double> costToSendOrReceive;
        double dutchPay;
        for (String member : members) {
            costToSendOrReceive = new WeakHashMap<>();
            costToSendOrReceiveByAttendee.put(member, costToSendOrReceive);
        }

        for (PayHistory obj : payHistories) {
            dutchPay = (double) obj.getCost() / obj.getAttendeesSize();
            dutchPay = -dutchPay;
            for (String member : obj.getAttendees()) {
                costToSendOrReceive = costToSendOrReceiveByAttendee.get(member);
                if (costToSendOrReceive != null) {
                    if (!member.equals(obj.getPayer())) {
                        Double preCost = costToSendOrReceive.get(obj.getPayer());
                        if (preCost != null) {
                            preCost += dutchPay;
                        } else {
                            preCost = dutchPay;
                        }
                        costToSendOrReceive.put(obj.getPayer(), preCost); //preCost 값은 minus

                        WeakHashMap<String, Double> costMapByPayer = costToSendOrReceiveByAttendee.get(obj.getPayer());
                        if (costMapByPayer != null) {
                            preCost = costMapByPayer.get(member);
                            if (preCost != null) {
                                preCost -= dutchPay;
                            } else {
                                preCost = -dutchPay;
                            }
                            costMapByPayer.put(member, preCost); //preCost 값은 plus
                        }
                    }
                }
            }
        }
    }

    public StringBuilder getSettlementHistoryText(LinkedHashSet<String> members){
        double totalCostSendOrReceive;
        Integer totalCost;
        Double costSendOrReceive;
        WeakHashMap<String, Double> costToSendOrReceive;
        StringBuilder settlement = new StringBuilder();
        for (String member : members) {
            settlement.append(member);
            settlement.append("님");
            settlement.append("\n");
            settlement.append("참석 : ");
            LinkedHashSet<String> payKind = payKindByAttendee.get(member);
            if (payKind != null) {
                if (payKind.isEmpty()) {
                    settlement.append("없음");
                } else {
                    String prefix = "";
                    for (String str : payKind) {
                        settlement.append(prefix);
                        prefix = ", ";
                        settlement.append(str);
                    }
                }
            }
            settlement.append("\n");

            totalCost = costByPayer.get(member);
            if (totalCost != null && totalCost > 0) {
                settlement.append("낸 돈 : ");
                settlement.append(totalCost);
                settlement.append("원");
                settlement.append("\n");
            }

            totalCostSendOrReceive = 0d;
            costToSendOrReceive = costToSendOrReceiveByAttendee.get(member);
            if (costToSendOrReceive != null) {
                for (String key : costToSendOrReceive.keySet()) {
                    costSendOrReceive = costToSendOrReceive.get(key);
                    if (costSendOrReceive != null) {
                        totalCostSendOrReceive += costSendOrReceive;
                    }
                }
                if (totalCostSendOrReceive != 0) {
                    if (totalCostSendOrReceive < 0) {
                        settlement.append("낼 돈 : ");
                        settlement.append(String.format(Locale.KOREA, "%.2f", -totalCostSendOrReceive));
                    } else if (totalCostSendOrReceive > 0) {
                        settlement.append("받을 돈 : ");
                        settlement.append(String.format(Locale.KOREA, "%.2f", totalCostSendOrReceive));
                    }
                    settlement.append("원");
                    settlement.append("\n");
                }
            }
        }
        return settlement;
    }

}


