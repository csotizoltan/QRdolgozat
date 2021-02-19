package com.example.qrdolgozat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView tvQREredmeny;
    private Button btncSan, btnKiir;

    private String qrCodeEredmeny;
    private boolean writePermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        btncSan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setPrompt("QR CODE SCAN");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });


        btnKiir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writePermission) {
                    try {
                        Fajlbairas.kiir(qrCodeEredmeny);
                        Toast.makeText(MainActivity.this,
                                "Sikeres fájlba írás (scannedCodes.csv)...", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.d("Kiírási hiba", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Kiléptél a scanből", Toast.LENGTH_SHORT).show();
            }
            else {
                tvQREredmeny.setText(result.getContents());
                qrCodeEredmeny = result.getContents();

                try {
                    Uri url = Uri.parse(result.getContents());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(intent);
                } catch (Exception exception) {
                    Log.d("URI ERROR", exception.toString());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void init() {
        tvQREredmeny = findViewById(R.id.tvQREredmeny);
        btncSan = findViewById(R.id.btncSan);
        btnKiir = findViewById(R.id.btnKiir);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
            writePermission = true;
        }
        else {
            writePermission = false;
            String[] permissions =
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }
}