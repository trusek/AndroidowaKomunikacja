package com.example.trusek.androidowakomunikacjawtle;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private class ZadanieAsynchroniczne extends AsyncTask<String, String, String> {
        int mRozmiar;
        String mTyp;

        @Override
        protected String doInBackground(String... params) {
            String adres_url = params[0];
            HttpURLConnection polaczenie = null;
            try {
                URL url = new URL(adres_url);
                polaczenie = (HttpURLConnection) url.openConnection();
                polaczenie.setRequestMethod("GET");
                polaczenie.setDoOutput(true);
                mRozmiar = polaczenie.getContentLength();
                mTyp = polaczenie.getContentType();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (polaczenie != null) polaczenie.disconnect();
            }
            return "zakończono";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            //wyświetla wyniki
            Log.d("async_task", "wynik: "+result);

            textFileType.setText(mTyp);
            textFileSize.setText(Integer.toString(mRozmiar));

            super.onPostExecute(result);
        }
    }

    String url;
    EditText editUrl;
    TextView textFileType;
    TextView textFileSize;
    TextView editProgress;
    ProgressBar progressBar;

    Button buttonInfo;
    Button buttonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        textFileSize = findViewById(R.id.editSize);
        textFileType = findViewById(R.id.editType);
        editUrl = findViewById(R.id.editAdres);
        editProgress = findViewById(R.id.editProgres);

        progressBar = findViewById(R.id.progressBar);


        buttonInfo = findViewById(R.id.buttonInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if(CanIClick()){
                    uruchomZadanieAcynchroniczne();
                }
            }
        });

        buttonDownload = findViewById(R.id.buttonDownload);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);
                url = editUrl.getText().toString();
                if(CanIClick()){
                    MojaIntentService.pobiezInformacje(
                            MainActivity.this, //kontekst
                            url); //url
                }
            }
        });

    }

    private boolean CanIClick() {
        //jeśli adres jest podany
        if (editUrl.getText().toString().isEmpty()){
            Toster("Adres nie może być pusty");
            return false;
        }
        else{
            //jeśli adres rozpoczyna się od http
            if(editUrl.getText().toString().startsWith("http")){
                return true;
            }
            else{
                Toster("Adres powinien rozpoczynać się od \"http\"");
                return false;
            }
        }
    }

    //funkcja wyświetlająca komunikat
    private void Toster(String komunikat) {
        final Toast grzanka = Toast.makeText(
                this,//kontekst-zazwyczaj referencja do Activity
                komunikat,//napis do wyświetlenia
                Toast.LENGTH_LONG);//długość wyświetlania napisu
        grzanka.show();
    }

    private void uruchomZadanieAcynchroniczne() {
        url = editUrl.getText().toString();
        ZadanieAsynchroniczne zadanie=new ZadanieAsynchroniczne();
        zadanie.execute(new String[] {url});
        Log.d("async_task", "zadanie uruchomione");
    }

    private BroadcastReceiver mOdbiorcaRozgloszen = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        //obsługa odebrania komunikatu
        public void onReceive(Context context, Intent intent) {
            Bundle tobolek = intent.getExtras();
            ProgresInfo progresInfo = tobolek.getParcelable(MojaIntentService.INFO);

            editProgress.setText(Long.toString(progresInfo.Pobrano));
            progressBar.setProgress(progresInfo.mProgres);
        }
    };
    @Override //zarejestrowanie odbiorcy
    protected void onResume() {
        super.onResume();
        registerReceiver(mOdbiorcaRozgloszen, new IntentFilter(
                MojaIntentService.POWIADOMIENIE));
    }
    @Override //wyrejestrowanie odbiorcy
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mOdbiorcaRozgloszen);
    }

    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
        }
    }

}