package com.example.trusek.androidowakomunikacjawtle;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MojaIntentService extends IntentService {
    private static final String AKCJA_ZADANIE1 =
            "com.example.trusek.androidowakomunikacjawtle.action.zadanie1";
    //tekstowe identyfikatory parametrów potrzebnych do
    //wykonania akji (może być więcej niż jeden)
    private static final String PARAMETR1 =
            "com.example.trusek.androidowakomunikacjawtle.extra.parametr1";
    //statyczna metoda pomocnicza uruchamiająca zadanie (oczywiście parametrów może być
    //więcej)
    public static void pobiezInformacje(Context context) {
        Intent zamiar = new Intent(context, MojaIntentService.class);
        zamiar.setAction(AKCJA_ZADANIE1);
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
            if (AKCJA_ZADANIE1.equals(action)) {
                //wykonanie zadania
                wykonajZadanie();
            } else {
                Log.e("intent_service","nieznana akcja");
            }
        }
        Log.d("intent_service","usługa wykonała zadanie");
    }
    private void wykonajZadanie() {
        //kod faktycznie wykonujący zadanie...
        Log.d("da", "wololo");
    }
}
