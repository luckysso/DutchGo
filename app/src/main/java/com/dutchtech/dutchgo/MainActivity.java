package com.dutchtech.dutchgo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.dutchtech.dutchgo.databinding.ActivityMainBinding;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private static MainActivity context;
    private static double density = 0;
    private static final DutchPayGroups dutchPayGroups=DutchPayGroups.getInstance();
    private static DutchPayGroup currentDutchPayGroup;

    public static MainActivity getContext() {
        return context;
    }

    public static int convertDPtoPX(int dp) {
        return (int) (dp * density + 0.5);
    }

    public static void setCurrentDutchPayGroup(DutchPayGroup dutchPayGroup) {
        currentDutchPayGroup = dutchPayGroup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.nav_button);

        addDutchPayGroup("모임1");

        binding.addGroup.setOnClickListener(v->{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("그룹 추가");

            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("확인", (dialogInterface, id) ->
            {
                String inputText = input.getText().toString();
                if (dutchPayGroups.isContainGroup(inputText)) {
                    Toast.makeText(getApplicationContext(), "그룹명이 이미 있습니다", Toast.LENGTH_LONG).show();
                } else if (StringExtend.isEmptyString(inputText)) {
                    Toast.makeText(getApplicationContext(), "공백을 입력할 수 없습니다", Toast.LENGTH_LONG).show();
                } else {
                    addDutchPayGroup(inputText);
                    loadCurrentDutchPayGroup();

                }
            });

            builder.setNegativeButton("취소", (dialogInterface, id) ->
                    dialogInterface.cancel());

            AlertDialog msgDlg = builder.create();
            msgDlg.show();
        });

        binding.addMem.setOnClickListener(v ->
        {
            Intent intent = new Intent(getApplicationContext(), InputMemberActivity.class);
            inputMemForResult.launch(intent);
        });

        binding.reset.setOnClickListener(v -> {
            currentDutchPayGroup.clearMembers();
            binding.memText.setText("");
            currentDutchPayGroup.clearPayHistories();
            ((ViewGroup)binding.payHistoryBox.getParent()).removeAllViews();
            binding.settlement.setText("");
            binding.settlementInfo.setText("");
        });

        binding.delMem.setOnClickListener(v ->
        {
            if (currentDutchPayGroup.isEmptyMembers()) {
                Toast.makeText(getApplicationContext(), "삭제할 멤버가 없습니다", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), DelMemActivity.class);
                intent.putExtra("members", (Serializable) currentDutchPayGroup.getMembers());
                delMemForResult.launch(intent);
            }
        });

        binding.addHistory.setOnClickListener(v ->
        {
            if (currentDutchPayGroup.isEmptyMembers()) {
                Toast.makeText(getApplicationContext(), "멤버를 입력해야 합니다", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), DutchKindActivity.class);
                intent.putExtra("members", (Serializable) currentDutchPayGroup.getMembers());
                historyForResult.launch(intent);
            }
        });

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String className = intent.getStringExtra("class");
        if (className != null) {
            if (className.equals("InputPayInfo")) {
                PayHistory payHistory = (PayHistory) intent.getSerializableExtra("payHistory");
                currentDutchPayGroup.addPayHistory(payHistory);

                refreshDutchPayText();
            }
        }
    }

    private void addDutchPayGroup(String groupName){
        dutchPayGroups.addDutchPayGroup(groupName);
        addGroup(groupName);
        setCurrentDutchPayGroup(dutchPayGroups.getDutchPayGroup(groupName));
    }

    private void addGroup(String groupName){
        GroupOnclickListener groupOnclickListener=GroupOnclickListener.getInstance();
        TextView groupNameView = new TextView(this);
        groupNameView.setText(groupName);
        groupNameView.setTag(groupName);
        groupNameView.setOnClickListener(groupOnclickListener);
        binding.groupName.addView(groupNameView);
    }

    private void loadPayHistory(PayHistory payHistory){
        Set<String> attendees = payHistory.getAttendees();
        String payKind = payHistory.getPayKind();
        int price = payHistory.getCost();

        StringBuilder payHistoryText = new StringBuilder();
        payHistoryText.append(attendees.iterator().next());
        payHistoryText.append("님");
        if (attendees.size() > 1) {
            payHistoryText.append(" 외 ");
            payHistoryText.append((attendees.size() - 1));
            payHistoryText.append("명 ");
        }
        payHistoryText.append("이 ");
        payHistoryText.append(payKind);
        payHistoryText.append(" ");
        payHistoryText.append(price);
        payHistoryText.append("원 사용");

        ConstraintLayout payHistorylayout = new ConstraintLayout(this);
        payHistorylayout.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDPtoPX(48)));

        TextView payHistoryView = new TextView(this);
        payHistoryView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                convertDPtoPX(48)));
        payHistoryView.setTextSize(16);
        payHistoryView.setGravity(Gravity.CENTER_VERTICAL);
        int padding = convertDPtoPX(10);
        payHistoryView.setPadding(padding, 0, 0, padding);
        payHistoryView.setText(payHistoryText);
        payHistorylayout.addView(payHistoryView);

        ImageView deleteBtnView = new ImageView(this);
        deleteBtnView.setBackgroundResource(R.drawable.ic_minus);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(convertDPtoPX(30), convertDPtoPX(30));
        params.setMargins(0, convertDPtoPX(8), convertDPtoPX(8), 0);
        deleteBtnView.setLayoutParams(params);

        payHistorylayout.addView(deleteBtnView);

        payHistorylayout.setTag(payHistory);
        payHistoryView.setId(View.generateViewId());
        deleteBtnView.setId(View.generateViewId());

        ConstraintSet set = new ConstraintSet();
        set.clone(payHistorylayout);
        set.connect(deleteBtnView.getId(), ConstraintSet.TOP, payHistoryView.getId(), ConstraintSet.TOP);
        set.connect(deleteBtnView.getId(), ConstraintSet.END, payHistoryView.getId(), ConstraintSet.END);
        set.applyTo(payHistorylayout);

        binding.payHistoryBox.addView(payHistorylayout);

        deleteBtnView.setOnClickListener(v -> {
            currentDutchPayGroup.removePayHistory(payHistory);
            binding.payHistoryBox.removeView(payHistorylayout);
            if (binding.payHistoryBox.getChildCount() == 0) {
                TextView view = getPayHistoryExplainBox();
                binding.payHistoryBox.addView(view);
            }
            refreshDutchPayText();
        });
    }


    public void loadCurrentDutchPayGroup(){
        printMember();
        Set<PayHistory> payHistories=currentDutchPayGroup.getPayHistories();
        for(PayHistory payHistory:payHistories){
            loadPayHistory(payHistory);
        }
        TextView payHistoryExplain = binding.payHistoryBox.findViewWithTag("payHistoryExplain");
        if (payHistoryExplain != null) {
            binding.payHistoryBox.removeView(payHistoryExplain);
        }
        binding.settlementInfo.setText(currentDutchPayGroup.getPayHistoryText());
        binding.settlement.setText(currentDutchPayGroup.getSettlementHistoryText());
    }

    private TextView getPayHistoryExplainBox() {
        TextView payHistoryExplain = new TextView(this);
        payHistoryExplain.setTag("payHistoryExplain");
        payHistoryExplain.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                convertDPtoPX(48)));
        payHistoryExplain.setTextSize(18);
        payHistoryExplain.setText(R.string.결제내역추가설명);
        payHistoryExplain.setGravity(Gravity.CENTER);
        return payHistoryExplain;
    }

    private void refreshDutchPayText() {
        currentDutchPayGroup.setSettlementHistory();
        currentDutchPayGroup.setCostToSendOrReceiveByAttendee();
        binding.settlementInfo.setText(currentDutchPayGroup.getPayHistoryText());
        binding.settlement.setText(currentDutchPayGroup.getSettlementHistoryText());
    }

    private void printMember() {
        binding.memText.setText(currentDutchPayGroup.getMembersText());
    }

    ActivityResultLauncher<Intent> inputMemForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;

                    LinkedHashSet<String> memberToInput = new LinkedHashSet<>();
                    LinkedHashSet<String> memberOverlap = new LinkedHashSet<>();
                    String[] MemberToInputText = ((String) data.getSerializableExtra("memberToInput")).trim().replaceAll(" +"," ").split(" ");
                    for (String str : MemberToInputText) {
                        if (memberToInput.contains(str)) {
                            memberOverlap.add(str);
                        }
                        memberToInput.add(str);
                    }
                    for (String str : memberToInput) {
                        if (currentDutchPayGroup.getMembers().contains(str)) {
                            memberOverlap.add(str);
                        }
                        currentDutchPayGroup.addMember(str);
                    }

                    printMember();

                    String prefix;
                    if (!memberOverlap.isEmpty()) {
                        StringBuilder memberOverlapText = new StringBuilder();
                        prefix = "";
                        for (String s : memberOverlap) {
                            memberOverlapText.append(prefix);
                            prefix = ", ";
                            memberOverlapText.append(s);
                        }
                        memberOverlapText.append(" 인원이 이미 있습니다");
                        Toast.makeText(getApplicationContext(), memberOverlapText, Toast.LENGTH_LONG).show();
                    }

                }
            });
    @SuppressWarnings("unchecked")
    ActivityResultLauncher<Intent> delMemForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    boolean isPayerOrAttendee;

                    Set<PayHistory> payHistories=currentDutchPayGroup.getPayHistories();
                    LinkedHashSet<String> memberToDelete = (LinkedHashSet<String>) data.getSerializableExtra("memberToDelete");
                    for (String member : memberToDelete) {
                        currentDutchPayGroup.removeMember(member);

                        isPayerOrAttendee = false;
                        for (PayHistory payHistory : payHistories) {
                            if (member.equals(payHistory.getPayer())) {
                                isPayerOrAttendee = true;
                            } else {
                                for (String attendee : payHistory.getAttendees()) {
                                    if (member.equals(attendee)) {
                                        isPayerOrAttendee = true;
                                        break;
                                    }
                                }
                            }
                            if (isPayerOrAttendee) {
                                currentDutchPayGroup.removePayHistory(payHistory);
                                ConstraintLayout layout= binding.payHistoryBox.findViewWithTag(payHistory);
                                ((ViewGroup)layout.getParent()).removeView(layout);
                            }
                        }
                    }
                    if(currentDutchPayGroup.isEmptyPayHistories()){
                        TextView view = getPayHistoryExplainBox();
                        binding.payHistoryBox.addView(view);
                    }
                    printMember();
                    refreshDutchPayText();
                }
            });


    ActivityResultLauncher<Intent> historyForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;

                }
            });

}

