package com.example.a2fa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class QRscanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    boolean CameraPermission = false;
    final int CAMERA_PERM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        askPermission();
        if(CameraPermission) {
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull Result result) {
                    String totp;
                    String issuer;
                    if (result.getText().indexOf(":") != -1) {
                        String[] parts = result.getText().split(":");
                    if (parts[1].matches("\\w{3}-\\w{3}-\\w{3}-\\w{3}-\\w{3}-\\w{3}")) {
                        Intent retIntent = new Intent();

                        totp = parts[1];
                        issuer = parts[3];
                        retIntent.putExtra("qrdata", totp + ";" + issuer);
                        setResult(Activity.RESULT_OK, retIntent);
                        finish();
                    } else {
                        System.out.println("blogai1");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"blogai",Toast.LENGTH_SHORT).show();
                                 }
                        });


                    }
                }else{
                        System.out.println("blogai2");
                    }

                }
            });

        }
    }

    public void back(View v){
        finish();
    }
    private void askPermission(){

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){

            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(QRscanner.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERM);

            }else{
                mCodeScanner.startPreview();
                CameraPermission = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == CAMERA_PERM){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                mCodeScanner.startPreview();
                CameraPermission = true;

            }else{

                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){

                    new AlertDialog.Builder(this)
                            .setTitle("Leidimai")
                            .setMessage("Suteikite leidimus naudotis įrenginio kamera norint turėti pilna funkcionalumą")
                            .setPositiveButton("Leisti", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {

                                    ActivityCompat.requestPermissions(QRscanner.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERM);

                                }
                            }).setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            dialog.dismiss();

                        }
                    }).create().show();
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("Leidimai")
                            .setMessage("Jūs nesuteikėte kelių leidimų. Suteikite visus leidimus naudojant [settings] > [Permissions]")
                            .setPositiveButton("Nustatymai", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {

                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS,
                                            Uri.fromParts("package",getPackageName(),null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }
                            }).setNegativeButton("Ne, palikti applikacija", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            dialog.dismiss();
                            finish();

                        }
                    }).create().show();
                }

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        if(CameraPermission) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }}
