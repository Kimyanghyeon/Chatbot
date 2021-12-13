package com.example.chatbot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.example.chatbot.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final int REQUEST_EXTERNAL_STORAGE = 200;

    RadioGroup radioGroupMy, radioGroupYou;
    String my,you;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 저장소 읽기 권한이 없을 시 권한 요청 팝업 생성
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        initUI();
    }

    private void initUI() {
        // 다크 모드 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding.enterBtn.setOnClickListener(v -> {

            binding.radioGroupMy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.myKo:
                                my= "ko";
                            break;
                        case R.id.myEn:
                                my= "en";
                            break;
                        case R.id.myZhCH:
                                my= "zh_CH";
                            break;
                        case R.id.myZhTW:
                                my= "zh_TW";
                            break;
                        case R.id.myJo:
                                my= "ja";
                            break;
                    }
                }//end of onChecked
            });//end of listen

            binding.radioGroupYou.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.youKo:
                            you= "ko";
                            break;
                        case R.id.youEn:
                            you= "en";
                            break;
                        case R.id.youZhCH:
                            you= "zh_CH";
                            break;
                        case R.id.youZhTW:
                            you= "zh_TW";
                            break;
                        case R.id.youJo:
                            you= "ja";
                            break;
                    }
                }//end of onCheck
            });//end of listen

            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("my", my);
           // Toast.makeText(MainActivity.this,"나의 언어 "+my+"  번역 언어 "+you,Toast.LENGTH_SHORT).show();
            intent.putExtra("you",you);
            intent.putExtra("username", binding.usernameEdit.getText().toString());
            intent.putExtra("roomNumber", binding.roomEdit.getText().toString());
            startActivity(intent);
        });
    }
}