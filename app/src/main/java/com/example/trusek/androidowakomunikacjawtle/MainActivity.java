package com.example.trusek.androidowakomunikacjawtle;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private class ZadanieAsynchroniczne extends AsyncTask<String, String, String> {
        String mRozmiar;
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
                mRozmiar = Integer.toString(polaczenie.getContentLength());
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
            textFileSize.setText(mRozmiar);

            super.onPostExecute(result);
        }
    }

    String url;
    EditText editUrl;
    TextView textFileType;

    TextView textFileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textFileSize = findViewById(R.id.editSize);
        textFileType = findViewById(R.id.editType);
        editUrl = findViewById(R.id.editAdres);

        textFileSize.setText("wololo");


        Button buttonInfo = findViewById(R.id.buttonInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uruchomZadanieAcynchroniczne();
            }
        });

        Button buttonDownload = findViewById(R.id.buttonDownload);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MojaIntentService.pobiezInformacje(
                        MainActivity.this); //kontekst
            }
        });

    }

    private void uruchomZadanieAcynchroniczne() {
        url = editUrl.getText().toString();
        ZadanieAsynchroniczne zadanie=new ZadanieAsynchroniczne();
        zadanie.execute(new String[] {url});
        Log.d("async_task", "zadanie uruchomione");
    }
}