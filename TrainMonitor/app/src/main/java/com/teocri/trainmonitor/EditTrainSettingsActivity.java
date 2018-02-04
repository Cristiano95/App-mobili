package com.teocri.trainmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
//Questa activity viene (contrariamente alla precedente) chiamata quando l’utente preme su edit/modifica
//quando lo slot non è vuoto. Permette semplicemente di modificare l’intervallo di aggiornamento del treno o di eliminarlo
public class EditTrainSettingsActivity extends AppCompatActivity {

    TextView    textViewTrain;          //Dichiarazione variabile "Textview" "textViewTrain"
    RadioGroup  radioTime;              //Dichiarazione variabile "gruppo di bottoni" (RadioGroup)
    RadioButton radioTimeButton;        //Dichiarazione variabile bottone scelto per il tempo di polling
    int oldId;                          //Dichiarazione variabile intera vecchio id

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_train_settings);              //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);              //Settaggio per includere nell'activity la barra.

        int ind = getIntent().getIntExtra("index", 0);      //Variabile indice -> Settato grazie a "getIntExtra" che recupera i dati estesi ("index") dall'intent

        textViewTrain = (TextView) findViewById(R.id.textViewTrain);        //textViewTrain - Textview riferita al numero treno
        textViewTrain.setText(DataHolder.context.getText(R.string.mainscreen_train) + DataHolder.getTrainNumber(ind));   //Settagio testo Textview -> treno: numero treno (preso in relazione all'indice)
        radioTime = (RadioGroup) findViewById(R.id.radio);                  //Settaggio "radioTime", in relazione a "activity_edit_train_settings.xml"
    }

    @Override
    protected void onStart() {                  //Metodo chiamato quando l'attività sta diventando visibile all'utente.
        super.onStart();
        int ind = getIntent().getIntExtra("index", 0);  //Variabile indice -> Settato grazie a "getIntExtra" che recupera i dati estesi ("index") dall'intent
        int actual_pTime = DataHolder.pTime[ind];                       //Variabile "polling time attuale", settato grazie al "DataHolder", da cui prendo il pollingTime di "ind"

        RadioButton radioB;                                             //Dichiarazione variabile radioB per la gestione del tempo di polling

        switch(actual_pTime){            //In relazione al polling time attuale:
            case 30000:                                                         //Caso 30 secondi di polling
                radioB = (RadioButton) findViewById(R.id.radioButton30s);       //Variabile radioB -> caso "radioButton30s" (activity_edit_train_settings.xml)
                radioB.setChecked(true);                                        //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case 60000:                                                         //Caso 60 secondi di polling
                radioB = (RadioButton) findViewById(R.id.radioButton1m);        //Variabile radioB -> caso "radioButton1m" (activity_edit_train_settings.xml)
                radioB.setChecked(true);                                        //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case 120000:                                                        //Caso 120 secondi di polling
                radioB = (RadioButton) findViewById(R.id.radioButton2m);        //Variabile radioB -> caso "radioButton2m" (activity_edit_train_settings.xml)
                radioB.setChecked(true);                                        //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case 15000:                                                         //Caso 15 secondi di polling
            default:                                                            //Caso default
                radioB = (RadioButton) findViewById(R.id.radioButton15s);       //Variabile radioB -> caso "radioButton15s" (activity_edit_train_settings.xml)
                radioB.setChecked(true);                                        //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
        }
        oldId = radioB.getId(); //Modifico la variabile "vecchio parametro id", settandolo in base al "radioB" scelto (impostandolo con l'id)
    }

    public void SetChanges(View view) {         //Metodo per applicare o meno le modifiche effettuate
        int selectedId = radioTime.getCheckedRadioButtonId();                   //Variabile "selectedId" -> id del bottone radiobutton selezionato

        if (selectedId == oldId){                                               //Se il bottone selezionato è uguale a quello che era già selezionato
            Toast.makeText(getApplicationContext(), R.string.toast_no_changes, Toast.LENGTH_SHORT).show();  //Messaggio "Niente da modificare"
            return;                                                                                   //e return
        }

        int ind = getIntent().getIntExtra("index", 0);              //Variabile indice -> Settato grazie a "getIntExtra" che recupera i dati estesi ("index") dall'intent
        radioTimeButton = (RadioButton) findViewById(selectedId);                   //Variabile "radioTimeButton" -> in relazione a quello dell'id cliccato (selectedId)
        int pollingValue = Integer.parseInt(radioTimeButton.getHint().toString());  //Settaggio valore di polling in relazione alla stringa relativa al bottone cliccato

        DataHolder.set_pTime(ind, pollingValue);            //Setto il tempo di polling, in posizione ind, in relazione al valore di polling scelto
        ThreadHive.restartUnit(ind);                        //Tramite la classe ThreadHive faccio la "restartUnit" del thread in posizione ind

        Toast.makeText(getApplicationContext(), R.string.toast_polling_changed, Toast.LENGTH_SHORT).show();   //Messaggio "Tempo di polling modificato"
        onOptionsItemSelected(null);
    }

    public void deleteTrain(View view) {                   //Metodo per la cancellazione di un treno (presente in un determinato slot)
        int ind = getIntent().getIntExtra("index", 0);  //Variabile indice -> Settato grazie a "getIntExtra" che recupera i dati estesi ("index") dall'intent

        DataHolder.resetDataIndex(ind);                    //Tramite il DataHolder richiamo "resetDataIndex", metodo di reset dei dati in posizione ind

        ThreadHive.killUnit(ind);                          //Tramite la classe ThreadHive faccio la "kill" del thread in posizione ind

        onOptionsItemSelected(null);                       //Viene chiamato ogni volta che viene selezionato un elemento nel menu delle opzioni.
    }                                                      //In tal caso, in relazione all'opzione selezionata, non faccio nulla

    @Override //Metodo chiamato quando l'activity ha rilevato la pressione da parte dell'utente del tasto "indietro".
              //In tal caso, in relazione all'opzione selezionata, non faccio nulla
    public void onBackPressed() { onOptionsItemSelected(null); }

    public boolean onOptionsItemSelected(MenuItem item){    //Metodo chiamato ogni volta che viene selezionato un elemento nel menu delle opzioni.
        finish();                                           //Chiamato quando l'activity è terminata e dovrebbe essere chiusa.
        return true;
    }
}
