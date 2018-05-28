package com.example.trusek.androidowakomunikacjawtle;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojaIntentService extends IntentService {
    private static final String AKCJA_DOWNLOAD =
            "com.example.trusek.androidowakomunikacjawtle.action.zadanie1";
    public static final String INFO = "info";
    public static final String PROGRES = "progress";
    public final static String POWIADOMIENIE =
            "com.example.intent_service.odbiornik";

    int ROZMIAR_BLOKU = 1024;
    ProgresInfo progresInfo = new ProgresInfo();
    long mPobranychBajtow = 0;
    int mRozmiar =0;

    public static void pobiezInformacje(Context context, String adres_url) {
        Intent zamiar = new Intent(context, MojaIntentService.class);
        zamiar.setAction(AKCJA_DOWNLOAD);
        zamiar.putExtra("url",adres_url);
        context.startService(zamiar);
    }

    //konstruktor
    public MojaIntentService() {
        super("MojaIntentService");
    }

    //metoda wykonująca zadanie
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            //sprawdzenie o jaką akcję chodzi
            if (AKCJA_DOWNLOAD.equals(action)) {
                String mAdres = intent.getStringExtra("url");
                //pobranie pliku
                HttpURLConnection polaczenie = null;
                try {
                    Log.d("incest service", "rozpoczynam ściąganie pliku");
                    URL url = new URL(mAdres);
                    polaczenie = (HttpURLConnection) url.openConnection();
                    polaczenie.connect();
                    mRozmiar = polaczenie.getContentLength();

                    File plikRoboczy = new File(url.getFile());
                    File plikWyjsciowy = new File(
                            Environment.getExternalStorageDirectory() +
                                    File.separator+ plikRoboczy.getName());
                    if (plikWyjsciowy.exists()){
                        plikWyjsciowy.delete();
                        Log.d("incest service", "plik wykryto i usunieto");
                    }

                    InputStream strumienZSieci = null;
                    FileOutputStream strumienDoPliku;

                    DataInputStream czytnik = new DataInputStream(polaczenie.getInputStream());
                    strumienDoPliku = new FileOutputStream(plikWyjsciowy.getPath());
                    byte bufor[] = new byte[ROZMIAR_BLOKU];
                    int pobrano = czytnik.read(bufor, 0, ROZMIAR_BLOKU);
                    while (pobrano != -1) {
                        strumienDoPliku.write(bufor, 0, pobrano);
                        mPobranychBajtow += pobrano;
                        pobrano = czytnik.read(bufor, 0, ROZMIAR_BLOKU);
                        wyslijBroadcast();
                    }

                    if(strumienDoPliku != null){
                        strumienDoPliku.flush();
                        strumienDoPliku.close();
                    }
                    if(strumienZSieci != null){
                        strumienZSieci.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (polaczenie != null) polaczenie.disconnect();
                }
            } else {
                Log.e("incest service", "nieznana akcja");
            }
        }
        Log.d("incest service", "usługa wykonała zadanie");
    }

    //procedura wysylająca komunikat
    void wyslijBroadcast(){
        Intent zamiar = new Intent(POWIADOMIENIE);
        progresInfo.mProgres = (int) (mPobranychBajtow * 100 / mRozmiar);
        progresInfo.Pobrano = mPobranychBajtow;
        zamiar.putExtra(INFO,progresInfo);
        sendBroadcast(zamiar);
    }
}
