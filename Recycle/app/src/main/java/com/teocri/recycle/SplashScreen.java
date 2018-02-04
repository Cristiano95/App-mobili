package com.teocri.recycle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
//Serve a far ritardare di qualche secondo la visualizzazione del main, per far sapere all’utente che serve qualche istante a caricare i dati.
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {            //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);                    //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayShowHomeEnabled(true);       //Settaggio per includere l'accessibilità alla home nella barra delle azioni.
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);         //Settaggio icona dell'applicazione
    }

    //Start activity
    public void startMaps(View v) {                   //Metodo per passaggio da questa activity al MainActivity
        Intent intent = new Intent(this, MainActivity.class);  //Un intent è una descrizione astratta di un'operazione. (Passaggio da SplashScreen a MainActivity)
        startActivity(intent);                                             //In tal caso usato con startActivity per avviare la nuova activity
    }

    @Override
    protected void onStart() {                  //Metodo chiamato quando l'attività sta diventando visibile all'utente.
        super.onStart();
        final Handler handler = new Handler();  //Variabile di tipo "Handler" per effettuare la "postDelayed"
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMaps(getCurrentFocus());
            }
        }, 1000);
    }                                           //Chiama la funzione startMaps per l'avvio dell'activity dopo un secondo, in relazione al currentFocus

                                                            //Metodo per tornare indietro cliccando il
    @Override                                               //tasto "indietro", che porta all'activity precedente
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override                                        //Metodo per eseguire qualsiasi pulizia finale prima che l'activity venga distrutta.
    protected void onDestroy() {
        super.onDestroy();
    }
}
