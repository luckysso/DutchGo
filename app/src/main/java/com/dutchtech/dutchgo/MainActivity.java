package com.dutchtech.dutchgo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dutchtech.dutchgo.databinding.ActivityMainBinding;
import com.dutchtech.dutchgo.databinding.ActivityMainContentBinding;
import com.dutchtech.dutchgo.databinding.AppBarMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding mainBinding;
    private ActivityMainContentBinding mainContentBinding;
    private static DrawerLayout drawerLayout;
    private static ActionBarDrawerToggle drawerToggle;
    private static double density = 0;
    private static final DutchPayGroups dutchPayGroups = DutchPayGroups.getInstance();
    private static DutchPayGroup currentDutchPayGroup;

    public static int convertDPtoPX(int dp) {
        return (int) (dp * density + 0.5);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= ActivityMainBinding.inflate(getLayoutInflater());
        mainContentBinding = mainBinding.mainContent;

        AppBarMainBinding appBarMainBinding=mainContentBinding.appBarMain;
        setContentView(mainBinding.getRoot());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;

        setSupportActionBar(appBarMainBinding.toolbar);

        drawerLayout = mainBinding.dlMainDrawerRoot;
        NavigationView navigationView = mainBinding.nvMainNavigationRoot;
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                appBarMainBinding.toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle.syncState();
        addDutchPayGroup("모임1");
        currentDutchPayGroup=dutchPayGroups.getDutchPayGroup("모임1");

        navigationView.getHeaderView(0).findViewWithTag("addGroup").setOnClickListener(v -> {

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

        mainContentBinding.addMem.setOnClickListener(v ->
        {
            Intent intent = new Intent(getApplicationContext(), InputMemberActivity.class);
            inputMemForResult.launch(intent);
        });

        mainContentBinding.reset.setOnClickListener(v -> {
            currentDutchPayGroup.clearMembers();
            mainContentBinding.memText.setText("");
            currentDutchPayGroup.clearPayHistories();
            ((ViewGroup) mainContentBinding.payHistoryBox.getParent()).removeAllViews();
            mainContentBinding.settlement.setText("");
            mainContentBinding.settlementInfo.setText("");
        });

        mainContentBinding.delMem.setOnClickListener(v ->
        {
            if (currentDutchPayGroup.isEmptyMembers()) {
                Toast.makeText(getApplicationContext(), "삭제할 멤버가 없습니다", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), DelMemActivity.class);
                intent.putExtra("members", (Serializable) currentDutchPayGroup.getMembers());
                delMemForResult.launch(intent);
            }
        });

        mainContentBinding.addHistory.setOnClickListener(v ->
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
    public boolean onNavigationItemSelected(MenuItem item) {
        String title = (String)item.getTitle();
        Map<String,DutchPayGroup> map=dutchPayGroups.getDutchPayGroups();
        for(Map.Entry<String,DutchPayGroup> entry:map.entrySet()){

        }
        return true;
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (drawerToggle.onOptionsItemSelected(item)) {
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String className = intent.getStringExtra("class");
        if (className != null) {
            if (className.equals("InputPayInfo")) {
                PayHistory payHistory = (PayHistory) intent.getSerializableExtra("payHistory");
                currentDutchPayGroup.addPayHistory(payHistory);
                loadPayHistory(payHistory);
                removePayHistoryExplainBox();
                refreshDutchPayText();
            }
        }
    }

    private void addDutchPayGroup(String groupName) {
        dutchPayGroups.addDutchPayGroup(groupName);
        Map<String,DutchPayGroup> dutchPayGroup=dutchPayGroups.getDutchPayGroups();
        mainBinding.nvMainNavigationRoot.getMenu().add(groupName).setOnMenuItemClickListener(item->{
            if(dutchPayGroups.getDutchPayGroups().containsKey(groupName)) {
                currentDutchPayGroup=dutchPayGroup.get(groupName);
                loadCurrentDutchPayGroup();
            }
            return false;
        });
    }

    private void loadPayHistory(PayHistory payHistory) {
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

        mainContentBinding.payHistoryBox.addView(payHistorylayout);

        deleteBtnView.setOnClickListener(v -> {
            currentDutchPayGroup.removePayHistory(payHistory);
            mainContentBinding.payHistoryBox.removeView(payHistorylayout);
            if (mainContentBinding.payHistoryBox.getChildCount() == 0) {
                TextView view = getPayHistoryExplainBox();
                mainContentBinding.payHistoryBox.addView(view);
            }
            refreshDutchPayText();
        });
    }


    public void loadCurrentDutchPayGroup() {
        printMember();
        Set<PayHistory> payHistories = currentDutchPayGroup.getPayHistories();
        for (PayHistory payHistory : payHistories) {
            loadPayHistory(payHistory);
        }
        removePayHistoryExplainBox();
        mainContentBinding.settlementInfo.setText(currentDutchPayGroup.getPayHistoryText());
        mainContentBinding.settlement.setText(currentDutchPayGroup.getSettlementHistoryText());
    }

    private void removePayHistoryExplainBox() {
        TextView payHistoryExplain = mainContentBinding.payHistoryBox.findViewWithTag("payHistoryExplain");
        if (payHistoryExplain != null) {
            mainContentBinding.payHistoryBox.removeView(payHistoryExplain);
        }
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
        mainContentBinding.settlementInfo.setText(currentDutchPayGroup.getPayHistoryText());
        mainContentBinding.settlement.setText(currentDutchPayGroup.getSettlementHistoryText());
    }

    private void printMember() {
        mainContentBinding.memText.setText(currentDutchPayGroup.getMembersText().toString());
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
                    String[] MemberToInputText = ((String) data.getSerializableExtra("memberToInput")).trim().replaceAll(" +", " ").split(" ");
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

                    Set<PayHistory> payHistories = currentDutchPayGroup.getPayHistories();
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
                                ConstraintLayout layout = mainContentBinding.payHistoryBox.findViewWithTag(payHistory);
                                ((ViewGroup) layout.getParent()).removeView(layout);
                            }
                        }
                    }
                    if (currentDutchPayGroup.isEmptyPayHistories()) {
                        TextView view = getPayHistoryExplainBox();
                        mainContentBinding.payHistoryBox.addView(view);
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

