package com.teocri.sportlogger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
//Serve a far ritardare di qualche secondo la visualizzazione del main, per far sapere all’utente che serve qualche istante a caricare i dati.
//Questo perchè devono essere caricati i dati per la creazione del database
public class MainActivity extends AppCompatActivity {

    static DatabaseManager db;            //Dichiarazione variabile per gestire il database

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                    //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayShowHomeEnabled(true);     //Settaggio per includere l'accessibilità alla home nella barra delle azioni.
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);       //Settaggio icona dell'applicazione

        db = new DatabaseManager(this);       //Creazione nuovo database (motivo per cui occorre aspettare un secondo per il caricamento prima di passare al MapsActivity)
    }

    @Override
    protected void onStart() {                         //Metodo chiamato quando l'attività sta diventando visibile all'utente.
        super.onStart();
        final Handler handler = new Handler();         //Variabile di tipo "Handler" per effettuare la "postDelayed"
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMaps(getCurrentFocus());
            }
        }, 1000);
    }                                                  //Chiama la funzione startMaps per l'avvio dell'activity dopo un secondo, in relazione al currentFocus

    /**********_START_ACTIVITIES_**********/
    public void startMaps(View v) {                    //Metodo per passaggio da questa activity al MapsActivity
        Intent intent = new Intent(MainActivity.this, MapsActivity.class); //Un intent è una descrizione astratta di un'operazione. (Passaggio da MainActivity a MapsActivity)
        startActivity(intent);                         //In tal caso usato con startActivity per avviare la nuova activity
    }

    /**********_ON_EVENTS_**********/                   //Metodo per tornare indietro cliccando il
    @Override                                           //tasto "indietro", che porta all'activity precedente
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {                        //Metodo per eseguire qualsiasi pulizia finale prima che l'activity venga distrutta.
        super.onDestroy();
        db.close();                                     //Chiusura db
    }
}