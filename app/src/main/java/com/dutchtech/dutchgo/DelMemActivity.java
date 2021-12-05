package com.dutchtech.dutchgo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dutchtech.dutchgo.databinding.ActivityDelMemBinding;

import java.util.LinkedHashSet;
import java.util.Set;

public class DelMemActivity extends AppCompatActivity {

    private ActivityDelMemBinding binding;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDelMemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        Set<String> members = (Set<String>) intent.getSerializableExtra("members");

        assert members != null;
        OutputView.printMemByAttendee(this, binding.cbLayout, members);

        LinkedHashSet<String> memberToDelete = new LinkedHashSet<>();

        StringBuilder memberToDeleteText = new StringBuilder();
        binding.confirmMem.setOnClickListener(v ->
        {
            memberToDelete.clear();
            memberToDeleteText.setLength(0);
            String prefix = "";
            for (int i = 0; i < binding.cbLayout.getChildCount(); i++) {
                View cbView = binding.cbLayout.getChildAt(i);
                if (cbView instanceof CheckBox) {
                    CheckBox cb = (CheckBox) cbView;
                    if (cb.isChecked()) {
                        memberToDeleteText.append(prefix);
                        prefix = ", ";
                        memberToDeleteText.append(cb.getText());
                        memberToDelete.add((String) cb.getText());
                    }
                }
            }

            if (memberToDelete.isEmpty()) {
                Toast.makeText(getApplicationContext(), "삭제할 인원을 선택하세요", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(DelMemActivity.this);
                builder.setTitle("선택된 인원 : " + memberToDeleteText);
                builder.setMessage("정말 삭제하시겠습니까?");

                builder.setPositiveButton("확인", (dialogInterface, id) ->
                {
                    intent.putExtra("memberToDelete", memberToDelete);
                    setResult(RESULT_OK, intent);
                    finish();
                });

                builder.setNegativeButton("취소", (dialogInterface, id) ->
                        dialogInterface.cancel());

                AlertDialog msgDlg = builder.create();
                msgDlg.show();
            }
        });

        binding.cancelMem.setOnClickListener(v -> finish());

    }

}