package com.example.rsalgorithm;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private  String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED) {

            RoughSetsTool roughSetsTool = new RoughSetsTool(testData());

            roughSetsTool.findingReduce();
        } else {
            Log.d(TAG, "未申请权限");
            Toast.makeText(this, "正在申请权限", Toast.LENGTH_LONG);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }



    private ArrayList testData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Element Color Shape Size Stability");
        list.add("x1 Red Triangle Large Stable");
        list.add("x2 Red Triangle Large Stable");
        list.add("x3 Yellow Circle Small UnStable");
        list.add("x4 Yellow Circle Small UnStable");
        list.add("x5 Blue Rectangle Large Stable");
        list.add("x6 Red Circle Middle UnStable");
        list.add("x7 Blue Circle Small UnStable");
        list.add("x8 Blue Rectangle Middle UnStable");

        return list;
    }










}
