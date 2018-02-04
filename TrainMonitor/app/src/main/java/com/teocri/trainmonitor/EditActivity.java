package com.teocri.trainmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
//Questa è la classe che verrà chiamata dal MainActivity quando l’utente preme su edit/modifica in uno slot vuoto. Quello che fa quindi è chiedere un numero di treno in input e il tempo di aggiornamento
public class EditActivity extends AppCompatActivity {

    EditText trainNumber;           //Dichiarazione variabile numero del treno
    RadioGroup radioTime;           //Dichiarazione variabile "gruppo di bottoni" (RadioGroup)
    RadioButton radioTimeButton;    //Dichiarazione variabile bottone scelto per il tempo di polling

    @Override
    protected void onCreate(Bundle savedInstanceState) {       //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);                //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Settaggio per includere nell'activity la barra.

        trainNumber = (EditText) findViewById(R.id.editTextGetNumber);  //Inizializzazione numero treno in relazione a quello inserito (activity_edit.xml)
        radioTime = (RadioGroup) findViewById(R.id.radio);              //Inizializzazione gruppo di bottoni (activity_edit.xml)
    }

    public boolean onOptionsItemSelected(MenuItem item){        //Metodo chiamato ogni volta che viene selezionato un elemento nel menu delle opzioni.
        finish();                                               //Chiamato quando l'activity è terminata e dovrebbe essere chiusa.
        return true;
    }

    @Override  //Metodo chiamato quando l'activity ha rilevato la pressione da parte dell'utente del tasto "indietro".
              //In tal caso, in relazione all'opzione selezionata, non faccio nulla (torna semplicemente indietro all'altra activity)
    public void onBackPressed() { onOptionsItemSelected(null); }

    public void addTrain(View view) {           //Metodo per l'aggiunta del treno

        if (trainNumber.getText().length() == 0){                                           //Se la lunghezza del numero del treno inserito è zero (valore nullo)
            Toast.makeText(this, R.string.toast_insert_train, Toast.LENGTH_LONG).show(); //Stampo messaggio "inserisci il numero di treno"
            return;                                                                         //e return
        }

        if (isMonitored(String.valueOf(trainNumber.getText()))){              //Se il numero di treno inserito (preso come stringa) è gia monitorato
            Toast.makeText(this,
                    DataHolder.context.getText(R.string.mainscreen_train) +                                                //Stampo la scritta "treno con numero treno ... è già monitorato"
                    String.valueOf(trainNumber.getText()) +
                    " " +
                    getString(R.string.is_already_monitored), Toast.LENGTH_LONG).show();
            return;                                                           //e return
        }

        int selectedId = radioTime.getCheckedRadioButtonId();                       //Variabile selectedId -> bottone cliccato
        radioTimeButton = (RadioButton) findViewById(selectedId);                   //Variabile "radioTimeButton" -> in relazione a quello dell'id cliccato (selectedId)
        int pollingValue = Integer.parseInt(radioTimeButton.getHint().toString());  //Settaggio valore di polling in relazione alla stringa relativa al bottone cliccato
        int ind = getIntent().getIntExtra("index", 0);              //Variabile indice -> Settato grazie a "getIntExtra" che recupera i dati estesi ("index") dall'intent

        DataHolder.setTrainNumber(ind, trainNumber.getText().toString());   //Tramite il DataHolder, setto il numero treno, in posizione ind, in relazione a quello inserito
        DataHolder.set_tStatus(ind, 8);                               //Setto lo stato del treno, in posizione ind, a 8 -> "Updating"
        DataHolder.set_pStatus(ind, true);                            //Setto lo stato del polling, in posizione ind, a true (polling attivo)
        DataHolder.set_pTime(ind, pollingValue);                           //Setto il tempo di polling, in posizione ind, in relazione al polling scelto
        DataHolder.updateMainScreen(ind);                   //Aggiorno la situazione a video dello slot di indice ind in relazione ai nuovi dati

        ThreadHive.executeUnit(ind);                        //Tramite la classe ThreadHive faccio la "execute" del thread in posizione ind

        onOptionsItemSelected(null);                        //Non faccio nulla (torna semplicemente indietro all'altra activity)
    }

    private boolean isMonitored(String trainNumb) {     //Metodo per controllare se un treno è monitorato già in un altro slot
        for (int i = 0; i < DataHolder.TRAIN_SLOTS; i++){                       //Per ogni slot
            if (DataHolder.getTrainNumber(i).trim().equals(trainNumb.trim()))   //Controllo se il numero del treno nello slot i è uguale al numero di treno inserito
                return true;                                                    //Ritorno true
        }
        return false;                                   //In caso contrario il treno non è monitorato e ritorno false
    }
}
