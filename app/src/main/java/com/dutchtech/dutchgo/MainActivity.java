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
import android.widget.LinearLayout;
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
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private ActivityMainContentBinding mainContentBinding;
    private LinearLayout payHistoryBoxLayout;
    private static NavigationView navigationView;
    private static DrawerLayout drawerLayout;
    private static double density = 0;
    private static final DutchPayGroups dutchPayGroups = DutchPayGroups.getInstance();
    private static DutchPayGroup currentDutchPayGroup;
    private static MenuItem currentMenuItem;
    private final String PAY_HISTORY_EXPLAIN="payHistoryExplain";

    public static int convertDPtoPX(int dp) {
        return (int) (dp * density + 0.5);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mainContentBinding = mainBinding.mainContent;

        payHistoryBoxLayout=mainContentBinding.payHistoryBox;
        AppBarMainBinding appBarMainBinding = mainContentBinding.appBarMain;

        setContentView(mainBinding.getRoot());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;

        setSupportActionBar(appBarMainBinding.toolbar);

        drawerLayout = mainBinding.dlMainDrawerRoot;
        navigationView = mainBinding.nvMainNavigationRoot;
        View navigationHeaderView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                appBarMainBinding.toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        addDutchPayGroup("??????1");

        navigationHeaderView.findViewWithTag("addGroup").setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("?????? ??????");

            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("??????", (dialogInterface, id) ->
            {
                String inputText = input.getText().toString();
                if (dutchPayGroups.isContainGroup(inputText)) {
                    Toast.makeText(getApplicationContext(), "???????????? ?????? ????????????", Toast.LENGTH_LONG).show();
                } else if (StringExtend.isEmptyString(inputText)) {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ??? ????????????", Toast.LENGTH_LONG).show();
                } else {
                    addDutchPayGroup(inputText);
                    loadCurrentDutchPayGroup();
                }
            });

            builder.setNegativeButton("??????", (dialogInterface, id) ->
                    dialogInterface.cancel());

            AlertDialog msgDlg = builder.create();
            msgDlg.show();
        });

        navigationHeaderView.findViewWithTag("deleteGroup").setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("?????? ??????");
            builder.setMessage(currentMenuItem.getTitle().toString().substring(1) + " ????????? ?????? ?????????????????????????");

            builder.setPositiveButton("??????", (dialogInterface, id) ->
            {
                dutchPayGroups.removeDutchPayGroup(currentMenuItem.getTitle().toString().substring(1));
                navigationView.getMenu().removeItem(currentMenuItem.getItemId());
                currentMenuItem = navigationView.getMenu().getItem(0);
                CharSequence title = currentMenuItem.getTitle();
                currentMenuItem.setTitle("???" + title);
                currentDutchPayGroup = dutchPayGroups.getDutchPayGroup(title.toString());
                loadCurrentDutchPayGroup();
            });

            builder.setNegativeButton("??????", (dialogInterface, id) ->
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
            payHistoryBoxLayout.removeAllViews();
            addPayHistoryExplainBoxIfEmpty(payHistoryBoxLayout);
            mainContentBinding.settlement.setText("");
            mainContentBinding.settlementInfo.setText("");
        });

        mainContentBinding.delMem.setOnClickListener(v ->
        {
            if (currentDutchPayGroup.isEmptyMembers()) {
                Toast.makeText(getApplicationContext(), "????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), DelMemActivity.class);
                intent.putExtra("members", (Serializable) currentDutchPayGroup.getMembers());
                delMemForResult.launch(intent);
            }
        });

        mainContentBinding.addHistory.setOnClickListener(v ->
        {
            if (currentDutchPayGroup.isEmptyMembers()) {
                Toast.makeText(getApplicationContext(), "????????? ???????????? ?????????", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), DutchKindActivity.class);
                intent.putExtra("members", (Serializable) currentDutchPayGroup.getMembers());
                historyForResult.launch(intent);
            }
        });

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
                removePayHistoryExplainBox(payHistoryBoxLayout,navigationView.findViewWithTag(PAY_HISTORY_EXPLAIN));
                refreshDutchPayText();
            }
        }
    }

    private void addDutchPayGroup(String groupName) {
        dutchPayGroups.addDutchPayGroup(groupName);
        String menuName = "???" + groupName;

        MenuItem menuItem = navigationView.getMenu().add(0, View.generateViewId(), 0, menuName);

        if (currentMenuItem != null) {
            currentMenuItem.setTitle(currentDutchPayGroup.getGroupName());
        }
        currentMenuItem = menuItem;
        currentDutchPayGroup = dutchPayGroups.getDutchPayGroup(groupName);
        menuItem.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (!currentMenuItem.getTitle().toString().equals(title)) {
                currentMenuItem.setTitle(currentDutchPayGroup.getGroupName());
                currentDutchPayGroup = dutchPayGroups.getDutchPayGroup(title);
                item.setTitle("???" + title);
                currentMenuItem = item;
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
        payHistoryText.append("???");
        if (attendees.size() > 1) {
            payHistoryText.append(" ??? ");
            payHistoryText.append((attendees.size() - 1));
            payHistoryText.append("??? ");
        }
        payHistoryText.append("??? ");
        payHistoryText.append(payKind);
        payHistoryText.append(" ");
        payHistoryText.append(price);
        payHistoryText.append("??? ??????");

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

        payHistoryBoxLayout.addView(payHistorylayout);

        deleteBtnView.setOnClickListener(v -> {
            currentDutchPayGroup.removePayHistory(payHistory);
            payHistoryBoxLayout.removeView(payHistorylayout);
            addPayHistoryExplainBoxIfEmpty(payHistoryBoxLayout);
            refreshDutchPayText();
        });
    }

    private void addPayHistoryExplainBoxIfEmpty(ViewGroup payHistoryBoxLayout) {
        if (payHistoryBoxLayout.findViewWithTag(PAY_HISTORY_EXPLAIN) ==null) {
            TextView view = getPayHistoryExplainBox();
            payHistoryBoxLayout.addView(view);
        }
    }

    public void loadCurrentDutchPayGroup() {
        printMember();
        Set<PayHistory> payHistories = currentDutchPayGroup.getPayHistories();
        for (PayHistory payHistory : payHistories) {
            loadPayHistory(payHistory);
        }
        addPayHistoryExplainBoxIfEmpty(payHistoryBoxLayout);

        mainContentBinding.settlementInfo.setText(currentDutchPayGroup.getPayHistoryText());
        String settlementText="";
        if(!payHistories.isEmpty()){
            settlementText=currentDutchPayGroup.getSettlementHistoryText().toString();
        }
        mainContentBinding.settlement.setText(settlementText);
    }

    private void removePayHistoryExplainBox(ViewGroup payHistoryBoxLayout,TextView viewToRemove) {
        if (viewToRemove != null) {
            payHistoryBoxLayout.removeView(viewToRemove);
        }
    }

    private TextView getPayHistoryExplainBox() {
        TextView payHistoryExplain = new TextView(this);
        payHistoryExplain.setTag(PAY_HISTORY_EXPLAIN);
        payHistoryExplain.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                convertDPtoPX(48)));
        payHistoryExplain.setTextSize(18);
        payHistoryExplain.setText(R.string.????????????????????????);
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
        mainContentBinding.memText.setText(currentDutchPayGroup.getMembersText());
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
                        memberOverlapText.append(" ????????? ?????? ????????????");
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
                                ConstraintLayout layout = payHistoryBoxLayout.findViewWithTag(payHistory);
                                ((ViewGroup) layout.getParent()).removeView(layout);
                            }
                        }
                    }
                    if (currentDutchPayGroup.isEmptyPayHistories()) {
                        TextView view = getPayHistoryExplainBox();
                        payHistoryBoxLayout.addView(view);
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

