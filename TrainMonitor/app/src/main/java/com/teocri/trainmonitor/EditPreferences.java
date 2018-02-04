package com.teocri.trainmonitor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class EditPreferences extends PreferenceActivity {           //Classe per gestire le preferenze(e leggerle per mezzo del menù)

    Preference[] p = new Preference[6];                     //Vettore di preferenze (sei slot di preferenze - come il numero degli slot dei treni)

    @SuppressWarnings("deprecation")        //Disabilita alcuni avvisi del compilatore. In questo caso, l'avviso sul codice deprecato ("deprecazione")
    @Override
    public void onCreate(Bundle savedInstanceState) {       //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.train_data);   //Aggiunge le preferenze in relazione all'xml (train_data.xml)

        for (int i = 0; i < 6; i++){                    //Per ogni i (sei slot di preferenze)
            String id = "TrainNumber" + (i+1);          //Dichiarazione e inizializzazione stringa id -> TrainNumber (preso dall'xml) e i+1 per il numero slot
            p[i] = findPreference(id);                  //Nel vettore p, all'indice i, inserisco la preferenza in relazione alla stringa id
        }                                               //Mi servo della funzione "findPreference(id)", che trova una preferenza
    }                                                   //in relazione alla sua chiave "id", la cui stringa è uguale alla key nell'xml.

    @Override
    protected void onStart() {  //Metodo chiamato quando l'activity sta diventando visibile all'utente, quindi ogni volta che viene fatta partire l'activity.
        super.onStart();
        updatePreferences();    //Aggiorno le preferenze degli slot
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    //Metodo per tornare indietro cliccando il tasto "indietro", che porta all'activity precedente.
    //finish() -> chiamata quando l'activity è terminata e dovrebbe essere chiusa.

    @SuppressWarnings("deprecation")
    private void updatePreferences() {          //Metodo utilizzato per aggiornare le preferenze
        SharedPreferences sharedPreferences = getSharedPreferences("train_data", MODE_PRIVATE);
        //Recupera il contenuto di "train_data", restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare i suoi valori.

        for (int i = 0; i < 6; i++){                                          //Per ogni i (sei slot di preferenze)
            int stat = sharedPreferences.getInt("TrainStatus" + (i), -1);//Variabile stat -> viene recuperato il TrainStatus del treno nello slot i e salvato in stat
            if (stat != -1){                                         //Se variabile stat non è nulla
                p[i].setSummary(DataHolder.context.getText(R.string.mainscreen_train) + sharedPreferences.getString("TrainNumber" + (i), "####"));
            }else{                                                   //La preferenza, nell'indice i, risulta con "treno:" e "numero treno" in relazione alla key (TrainNumber+numeroSlot)
                p[i].setSummary(R.string.mainscreen_not_monitored);  //Altrimenti la preferenza, nell'indice i, risulta con treno "non monitorato"
            }
        }
    }
}