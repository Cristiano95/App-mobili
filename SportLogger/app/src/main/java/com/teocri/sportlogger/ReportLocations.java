package com.teocri.sportlogger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.teocri.sportlogger.MainActivity.db;
//Questa activity, tramite una query al db, mostra le date in cui sono stati piazzati dei markers sulla mappa e il numero di markers per quel dato giorno.
//I giorni sono ordinati per data crescente e cliccando sopra una di queste date si muoverà la mappa di conseguenza al primo marker di quel giorno.
public class ReportLocations extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> dates = db.getSingleDates();                //Variabile "dates" -> lista di stringhe di date ("getSingleDates" per effettuare la query)
    private String[][] locations = new String[dates.size()][2];   //Variabile "locations" -> Ogni location ha due indici -> (matrice contenente "dates.size" righe e "2" colonne)
    ArrayList<HashMap<String, String>> list = new ArrayList<>();  //Variabile "list" -> contiene una lista delle date e i relativi marker per ognuna di esse
    private SimpleAdapter adapter;                                //Variabile "adapter" per mappare i dati statici alle view dell'xml (list_item.xml)
    ListView listView;                                            //listView - Textview riferita alla vista della lista di date (in cui è stato piazzato almeno un marker)

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_locations);     //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Settaggio per includere nell'activity la barra.

        HashMap<String, String> item;                   //Dichiarazione variabile "item" che è un HashMap formata da due stringhe -> data e numero marker
        for (int i = 0; i < dates.size(); i++){         //Per ogni data presente in "dates"
            locations[i][0] = dates.get(i);                                               //Imposto come primo elemento di "indice data i" la data stessa
            locations[i][1] = getString(R.string.entries_per_day) + db.getDateIstances(dates.get(i)); //Imposto come secondo elemento di "indice data i"
        }           //Il messaggio "Inserimenti per questo giorno:" e il numero stesso dei luoghi aggiunti per quella data, preso dalla "getDateIstances"

        for (int i = 0; i < dates.size(); i++){         //Per ogni data presente in "dates"
            item = new HashMap<>();                         //Creazione "HashMap" di nome "item" (temporanea)
            item.put("line1", locations[i][0]);             //Inserisco nel primo campo la data di "location di i"
            item.put("line2", locations[i][1]);             //Inserisco nel secondo campo il numero di "luoghi per la data i"
            list.add(item);                                 //Aggiungi "item" alla lista "list" (contenente tutte le date e per ciascuna i relativi marker inseriti)
        }

        adapter = new SimpleAdapter(this, list, R.layout.list_item, //"adapter" per mappare i dati statici alle viste definite nel file "list_item.xml"
                new String[] {"line1", "line2"},                           //Quindi passo "list" come HashMap contenente -> date e i relativi marker
                new int[] {R.id.item, R.id.subItem});                      //Inoltre il contenuto in list della "line1" viene gestito nella sezione "item" dell'xml (data in verde -> titolo)
                                                                           //Infine il contenuto in list della "line2" viene gestito nella sezione "subitem" dell'xml (numero luoghi -> sottotitolo)

        listView = (ListView) findViewById(R.id.listView);  //listView - Textview riferita alla view sulle diverse date (activity_report_locations.xml)
        listView.setAdapter(adapter);                       //Settaggio adapter in relazione all'adapter "adapter" creato
        listView.setOnItemClickListener(this);              //Registra un callback da richiamare quando si clicca su una delle date in questo AdapterView.
    }

    @Override //Metodo chiamato quando l'activity ha rilevato la pressione da parte dell'utente del tasto "indietro".
              //In tal caso, in relazione all'opzione selezionata, non faccio nulla (torna semplicemente indietro all'altra activity)
    public void onBackPressed() { onOptionsItemSelected(null); }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //Metodo utilizzato quando è stata cliccata una data in questo AdapterView.
        String lat = db.getDateLatitude(dates.get(i));                         //Variabile "latitudine" che è uguale alla latitudine della data "i" (presa tramite query al db)
        String lon = db.getDateLongitude(dates.get(i));                        //Variabile "longitudine" che è uguale alla longitudine della data "i" (presa tramite query al db)
        MapsActivity.moveCamLatLon(Double.valueOf(lat), Double.valueOf(lon));  //Tramite la "moveCamLatLon" della "MapsActivity" sposto la camera nel valore di latitudine e longitudine passati
        onBackPressed();                                                       //Richiamo il metodo "onBackPressed"
    }

    public boolean onOptionsItemSelected(MenuItem item){    //Metodo chiamato ogni volta che viene selezionato un elemento nel menu delle opzioni.
        finish();                                           //Chiamato quando l'activity è terminata e dovrebbe essere chiusa.
        return true;
    }
}

