package com.teocri.conference;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
//Questa classe mostrerà semplicemente in una nuova activity i dati relativi alla conferenza scelta
// e permetterà, tramite apposito bottone, l’inserimento dell’evento all’interno del calendario.
public class ConferenceDetails extends AppCompatActivity {

    TextView description;       //Dichiarazione TextView per la descrizione della conferenza
    TextView speakers;          //Dichiarazione TextView per gli speakers della conferenza
    TextView date;              //Dichiarazione TextView per la data della conferenza
    Element info;               //Variabile per le informazioni passate dal MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {          //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_details);     //Tramite setContentView collego all'activity il file xml relativo

        Intent i = getIntent();                                   //Variabile per ottenere l'intent e salvarlo in "i"
        info = (Element) i.getSerializableExtra("info");    //Per mezzo di "i" e grazie alla funzione "getSerializableExtra", recupero i dati estesi dall'intent. (variabile "info" -> dati presi grazie a putExtra)

        date        = (TextView) findViewById(R.id.date);         //date - Textview riferita alla data della conferenza, in relazione a quella passata dal MainActivity (activity_conference_details.xml)
        description = (TextView) findViewById(R.id.description);  //description - Textview riferita alla descrizione della conferenza, in relazione a quella passata dal MainActivity(activity_conference_details.xml)
        speakers    = (TextView) findViewById(R.id.speakers);     //speakers - Textview riferita agli speaker della conferenza, in relazione a quelli passati dal MainActivity(activity_conference_details.xml)

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    //Settaggio mostra display home come attiva
        getSupportActionBar().setTitle(info.getTitle());          //Settaggio "Titolo" conferenza in relazione al titolo passato dal MainActivity
        getSupportActionBar().setSubtitle(info.getTime());        //Settaggio "SottoTitolo" conferenza in relazione al titolo passato dal MainActivity

        date.setText        (info.getDate());                //Settaggio testo della data
        description.setText (info.getDescription());         //Settaggio testo della descrizione
        speakers.setText    (info.getSpeakers());            //Settaggio testo degli speakers
    }

    public void addEventToCalendar(View view) throws ParseException {   //Metodo per l’inserimento dell’evento all’interno del calendario
        Intent intent = new Intent(Intent.ACTION_INSERT);     //Dichiarazione e inizializzazione "intent" per effettuare l'inserimento
        intent.setType("vnd.android.cursor.item/event");      //Settaggio tipo di intent -> evento per il calendario

        Calendar startDate = Calendar.getInstance();                //Variabile di tipo "Calendar" per la creazione dell'istanza "Data di inizio"
        startDate.set(info.getYear(), info.getMonth()-1, info.getDay(), info.getStartHour(), info.getStartMins());  //Settaggio "Data di inizio"
        Calendar endDate = Calendar.getInstance();                  //Variabile di tipo "Calendar" per la creazione dell'istanza "Data di fine"
        endDate.set(info.getYear(), info.getMonth()-1, info.getDay(), info.getEndHour(), info.getEndMins());        //Settaggio "Data di fine"

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTimeInMillis());    //Imposto il tempo di inizio
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,   endDate.getTimeInMillis());      //Imposto il tempo di fine
        intent.putExtra(CalendarContract.Events.TITLE,           info.getTitle());                //Imposto il titolo
        intent.putExtra(CalendarContract.Events.DESCRIPTION,     info.getDescription());          //Imposto descrizione
        intent.putExtra(CalendarContract.Events.HAS_ALARM,       1);                         //Imposto sveglia

        startActivity(intent);              //Effettua lo start dell'intent
    }

    @Override //Metodo chiamato quando l'activity ha rilevato la pressione dell'utente del tasto "Indietro".
              //In tal caso, in relazione all'opzione selezionata, non faccio nulla
    public void onBackPressed() { onOptionsItemSelected(null); }

    public boolean onOptionsItemSelected(MenuItem item){    //Metodo chiamato ogni volta che viene selezionato un elemento nel menu delle opzioni.
        finish();                                           //Chiamato quando l'activity è terminata e dovrebbe essere chiusa.
        return true;
    }
}
