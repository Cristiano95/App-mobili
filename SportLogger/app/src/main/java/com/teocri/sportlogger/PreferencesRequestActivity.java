package com.teocri.sportlogger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

//Questa classe permette di modificare il tempo e lo spostamento con cui verranno aggiunti i markers sulla mappa.
//Se delle impostazioni vengono modficate, verrà effettuata la riconnessione al servizio di localizzazione per utilizzare le nuove impostazioni appena modificate
public class PreferencesRequestActivity extends AppCompatActivity {

    RadioGroup radioTime;               //Dichiarazione variabile "gruppo di bottoni" (RadioGroup)
    RadioGroup radioGap;                //Dichiarazione variabile "gruppo di bottoni" (RadioGroup)
    RadioButton radioTimeButton;        //Dichiarazione variabile bottone scelto per il tempo
    RadioButton radioGapButton;         //Dichiarazione variabile bottone scelto per lo spostamento
    int oldTime;                        //Variabile vecchio parametro tempo
    int oldGap;                         //Variabile vecchio parametro spostamento

    @Override
    protected void onCreate(Bundle savedInstanceState) {  //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_request);   //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //Settaggio per includere nell'activity la barra.

        radioTime = (RadioGroup) findViewById(R.id.radioTime);   //Inizializzazione "gruppo di bottoni" per il tempo, in relazione a quello inserito (activity_preferences_request.xml)
        radioGap = (RadioGroup) findViewById(R.id.radioGap);     //Inizializzazione "gruppo di bottoni" per lo spostamento, in relazione a quello inserito (activity_preferences_request.xml)
    }

    @Override
    protected void onStart() {                            //Metodo chiamato quando l'attività sta diventando visibile all'utente.
        super.onStart();

        String actualTime = String.valueOf(MyForegroundService.updateTime); //Mi servo della classe "MyForegroundService" per settaggio del tempo attuale
        switch(actualTime){              //In relazione al "tempo attuale" in cui mi trovo
            case "300000":    //radio_2 - caso 5 minuti:
                radioTimeButton = (RadioButton) findViewById(R.id.radioButton2);  //Variabile radioTimeButton -> caso "5 minuti" (activity_preferences_request.xml)
                radioTimeButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case "600000":    //radio_3 - caso 10 minuti:
                radioTimeButton = (RadioButton) findViewById(R.id.radioButton3);  //Variabile radioTimeButton -> caso "10 minuti" (activity_preferences_request.xml)
                radioTimeButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case "900000":    //radio_4 - caso 15 minuti:
                radioTimeButton = (RadioButton) findViewById(R.id.radioButton4);  //Variabile radioTimeButton -> caso "15 minuti" (activity_preferences_request.xml)
                radioTimeButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case "60000":     //radio_1 - caso un minuto(caso default):
            default:
                radioTimeButton = (RadioButton) findViewById(R.id.radioButton1);  //Variabile radioTimeButton -> caso "1 minuto" (activity_preferences_request.xml)
                radioTimeButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
        }
        oldTime = radioTimeButton.getId();//Modifico la variabile "vecchio parametro tempo", settandolo in base al "radioTimeButton" scelto (impostandolo con l'id)

        String actualGap = String.valueOf(MyForegroundService.updateGap); //Mi servo della classe "MyForegroundService" per settaggio dello spostamento attuale
        switch(actualGap){              //In relazione allo "spostamento attuale" in cui mi trovo
            case "100":    //radio_6 - caso 100 metri:
                radioGapButton = (RadioButton) findViewById(R.id.radioButton6);  //Variabile radioTimeButton -> caso "100 metri" (activity_preferences_request.xml)
                radioGapButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case "500":    //radio_7 - caso 500 metri:
                radioGapButton = (RadioButton) findViewById(R.id.radioButton7);  //Variabile radioTimeButton -> caso "500 metri" (activity_preferences_request.xml)
                radioGapButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case "1000":   //radio_8 - caso 1 chilometro:
                radioGapButton = (RadioButton) findViewById(R.id.radioButton8);  //Variabile radioTimeButton -> caso "1 chilometro" (activity_preferences_request.xml)
                radioGapButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
                break;
            case "50":     //radio_5 - caso 50 metri (caso default):
            default:
                radioGapButton = (RadioButton) findViewById(R.id.radioButton5);  //Variabile radioTimeButton -> caso "50 metri" (activity_preferences_request.xml)
                radioGapButton.setChecked(true);                                 //Imposta a "true" la "setChecked" che indica di aver cliccato questo bottone
        }
        oldGap = radioGapButton.getId();//Modifico la variabile "vecchio parametro spostamento", settandolo in base al "radioTimeButton" scelto (impostandolo con l'id)
    }


    public void SetChanges(View view) {  //Metodo per effettuare le modifiche
        int selectedTimeId = radioTime.getCheckedRadioButtonId();               //Settaggio "selectedTimeId", in relazione all'id del bottone cliccato
        int selectedGapId = radioGap.getCheckedRadioButtonId();                 //Settaggio "selectedGapId", in relazione all'id del bottone cliccato

        //Se "selectedTimeId", che è il bottone selezionato nel radioGroup "radioTime", è uguale al "vecchio parametro tempo" e
        //se "selectedGapId", che è il bottone selezionato nel radioGroup "radioGap", è uguale al "vecchio parametro spostamento"
        if (selectedTimeId == oldTime && selectedGapId == oldGap){
            Toast.makeText(getApplicationContext(), R.string.toast_no_changes, Toast.LENGTH_SHORT).show(); //Messaggio stampato "Niente da modificare"
            return;                                                                                  //e return
        }

        if (selectedTimeId != oldTime){//Se "selectedTimeId", che è il bottone selezionato nel radioGroup "radioTime", è diverso dal "vecchio parametro tempo"
            radioTimeButton = (RadioButton) findViewById(selectedTimeId);          //Variabile radioTimeButton -> caso uguale al "selectedTimeId" (activity_preferences_request.xml)
            long newTime = Integer.parseInt(radioTimeButton.getHint().toString()); //Variabile "nuovo valore", creato grazie al "getHint().toString()"  col quale prendiamo il valore dal "radioTimeButton"
            MyForegroundService.updateTime = newTime;                              //Vado a modificare, nella classe "MyForegroundService", il valore di "updateTime" col nuovo valore del tempo
        }

        if (selectedGapId != oldGap){//Se "selectedGapId", che è il bottone selezionato nel radioGroup "radioGap", è diverso dal "vecchio parametro spostamento"
            radioGapButton = (RadioButton) findViewById(selectedGapId);            //Variabile radioGapButton -> caso uguale al "selectedGapId" (activity_preferences_request.xml)
            long newGap = Integer.parseInt(radioGapButton.getHint().toString());   //Variabile "nuovo valore", creato grazie al "getHint().toString()" col quale prendiamo il valore dal "radioGapButton"
            MyForegroundService.updateGap = newGap;                                //Vado a modificare, nella classe "MyForegroundService", il valore di "updateGap" col nuovo valore dello spostamento
        }

        MapsActivity.newOptions = true;     //Se arrivo qui qualcosa ho modificato, di conseguenza pongo a "true" il valore della variabile "newOptions"
        Toast.makeText(this, R.string.toast_changes, Toast.LENGTH_SHORT).show(); //Messaggio stampato "Preferenze modificate"
        onOptionsItemSelected(null);        //Viene chiamato ogni volta che viene selezionato un elemento nel menu delle opzioni.
    }                                       //In tal caso, in relazione all'opzione selezionata, non faccio nulla

    @Override //Metodo chiamato quando l'activity ha rilevato la pressione da parte dell'utente del tasto "indietro".
              //In tal caso, in relazione all'opzione selezionata, non faccio nulla
    public void onBackPressed() { onOptionsItemSelected(null); }

    public boolean onOptionsItemSelected(MenuItem item){    //Metodo chiamato ogni volta che viene selezionato un elemento nel menu delle opzioni.
        finish();                                           //Chiamato quando l'activity è terminata e dovrebbe essere chiusa.
        return true;
    }
}
