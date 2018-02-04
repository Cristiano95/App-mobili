package com.teocri.conference;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
//La classe in questione contiene un ExpandableListView con cui mostriamo una lista delle singole date in cui abbiamo almeno una conferenza.
//Se espandiamo una di queste date avremo una vista su titolo e orario delle conferenze per quella data, ordinate per orario.
//Il Main viene quindi usato per scaricare il file JSON da link firebase(usando un AsyncTask), leggere i dati da file, creare le liste di elementi mostrati nel ExpandableListView e startare la ConferenceDetails class.
public class MainActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;  //Variabile per la gestione dell'ExpandagleListView
    ExpandableListView expListView;     //"expListView" con cui mostriamo una lista delle singole date in cui abbiamo almeno una conferenza
    List<String> listGroup;                         //Variabile "listGroup" che identifica ogni data nel quale è presente almeno una conferenza (lista di stringhe -> date)
    HashMap<String, List<Element>> listChild;       //Variabile "listChild" che identifica le conferenze (data, lista di conferenze per quella data)

    private String TAG = MainActivity.class.getSimpleName(); //Scrivo nella stringa "TAG" -> semplicemente il nome della classe dell'activity (in tal caso "MainActivity")
    private static String url = "https://firebasestorage.googleapis.com/v0/b/conferenceapp-b0aaa.appspot.com/o/conference.json?alt=media&token=a0c0f26b-1328-4642-baee-94fa9f22159a";
    //Stringa "url" contenente il link firebase dal quale viene scaricato il file .json

    @Override
    protected void onCreate(Bundle savedInstanceState) {            //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                     //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayShowHomeEnabled(true);      //Settaggio per includere l'accessibilità alla home nella barra delle azioni.
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);        //Settaggio icona dell'applicazione

        listGroup = new ArrayList<String>();                 //Inizializzazione listGroup -> Date in cui è presente almeno una conferenza
        listChild = new HashMap<String, List<Element>>();    //Inizializzazione listChild -> HashMap contenente stringa della data e lista delle relative conferenze

        new GetSessions().execute();            //Chiamo l'esecuzione della "GetSessions" che utilizza l'Asynctask

        expListView = (ExpandableListView) findViewById(R.id.conferenceExpandableList);
    }                         //All'interno del relativo file xml linkato(layout/activity_main.xml), ricerco la view da utilizzare in base all'id

    // Start activity
    public void startConferenceDetailActivity(View v, Element info) {   //Metodo per passaggio da MainActivity a startConferenceDetailActivity
        Intent intent = new Intent(MainActivity.this, ConferenceDetails.class);     //Un intent è una descrizione astratta di un'operazione. (Passaggio da MainActivity a startConferenceDetailActivity)
        intent.putExtra("info", info);                                                     //Tramite putExtra() passo i dati tra le due activity
        startActivity(intent);                                                                  //In tal caso usato con startActivity per avviare la nuova activity
    }

    //Async task to get json by HTTP call
    private class GetSessions extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {         //Metodo da effettuare prima della effettiva esecuzione e i calcoli effettuati nella "doInBackground"
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "downloading JSON", Toast.LENGTH_SHORT).show(); //Stampo a video la conferma di scaricamento del json
        }

        @Override
        protected Void doInBackground(Void... arg0) {       //Metodo utilizzato per eseguire calcoli di background che possono richiedere molto tempo
            HttpHandler sh = new HttpHandler();                   //HttpHandler "sh" viene invocato per elaborare la richiesta HTTP.

            String jsonStr = sh.makeServiceCall(url);             //Richiesta all'url tramite "makeServiceCall" e ottenere risposta da inserire in "jsonStr"
            longInfo(jsonStr);                                    //Richiama la funzione "longInfo" passando il "jsonStr"

            if (jsonStr != null) {                                //Se il contenuto della stringa "jsonStr" non è vuoto (contenuto preso dal json non nullo)
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);     //Per la gestione del json viene creata la variabile di tipo "JSONObject" sulla base della stringa "jsonStr"
                    List<Element> elements = new ArrayList<>();       //Variabile "List<Element>" -> lista per l'aggiunta di ogni data

                    // Getting JSON array
                    JSONArray sessions = jsonObj.getJSONArray("sessions");    //Restituisce il valore mappato per nome "sessions" ed è un JSONArray (lo creo)

                    // Looping through all sessions
                    for (int i = 0; i < sessions.length(); i++) {               //Per tutta la lunghezza di "sessions"
                        JSONObject s = sessions.getJSONObject(i);               //Creo un JSONObject, grazie a "sessions" in base all'indice "i", e lo scrivo in "s"

                        if (!listGroup.contains(getDateValue(s.getString("startTime"))))  //Se il "listGroup" non contiene questa determinata data di inizio
                            listGroup.add(getDateValue(s.getString("startTime")));        //La aggiungo al "listGroup"

                        elements.add(new Element(                                   //Aggiungo la conferenza (creo il nuovo elemento letto dal json)
                                s.getString("uid"),                           //Ottengo la stringa che si riferisce all'id della conferenza
                                s.getString("title"),                         //Ottengo la stringa che si riferisce al titolo della conferenza
                                getDateValue(s.getString("startTime")),       //Ottengo la stringa che si riferisce alla data della conferenza
                                getTimeValue(s.getString("startTime")) + " - " + getTimeValue(s.getString("endTime")), //Ottengo la stringa che si riferisce all'ora di inizio e di fine della conferenza
                                s.getString("description"),                   //Ottengo la stringa che si riferisce alla descrizione della conferenza
                                s.getString("speaker")                        //Ottengo la stringa che si riferisce allo speaker
                        ));
                    }
                    for (int i = 0; i < listGroup.size(); i++){                      //Per ogni "listGroup", quindi per ogni data presente
                        List<Element> tmp = new ArrayList<>();                       //Creo lista di elementi temporanea per ogni data ("listChild.put" vuole un List<Element> come secondo elemento)
                        for (int j = 0; j < elements.size(); j ++){                  //Per ogni "elemento" presente
                            if (elements.get(j).getDate().trim().equals(listGroup.get(i).trim())) { //Se l'elemento in posizione "j" ha la data uguale a quella del "listGroup" di indice i
                                tmp.add(elements.get(j));                                           //Vinee aggiunto l'elemento alla lista
                                Collections.sort(tmp, new Comparator<Element>() {
                                    @Override
                                    public int compare(Element e1, Element e2) {    //"Collections.sort" -> effettua la comparazione, tra gli elementi
                                        return Integer                             //appartenenti alla stessa lista, in modo da ordinarli in relazione al tempo
                                                .valueOf(getNumericTimeValue(e1.getTime()))
                                                .compareTo(Integer.valueOf(getNumericTimeValue(e2.getTime())));
                                    }
                                });
                            }
                        }
                        listChild.put(listGroup.get(i), tmp); //Viene fatta la put per mezzo del "listChild",  per l'inserimento della lista di elementi "tmp"
                    }                                         //nella rispettiva riga "listGroup" di indice "i" che ne identifica la data

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());    //Messaggio di errore "Errore nell'analisi del json"
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();}
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");               //Messaggio di errore "Impossibile ottenere json dal server"
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();}});
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) { //Metodo da effettuare dopo l'esecuzione e i calcoli effettuati nella "doInBackground"
            super.onPostExecute(result);

            listAdapter = new ExpandableListAdapter(MainActivity.this, listGroup, listChild); //Creazione "listAdapter" creando e passando all'ExpandableListAdapter, il MainActivity, listGroup e listChild
            expListView.setAdapter(listAdapter);                                                     //"expListView" gestito settando l'adapter "listAdapter"

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    final Element selected = (Element) listAdapter.getChild(groupPosition, childPosition);  //Creazione "selezionato" passando alla funzione "getChild", "groupPosition" -> data e "childPosition" -> conferenza
                    startConferenceDetailActivity(getCurrentFocus(), selected);                             //Chiamo la "startConferenceDetailActivity" passando il focus corrente e
                    return true;                                                                            //"selected" che indentifica l'elemento selezionato
                }
            });
        }

        //Utilities
        private void longInfo(String str) {     //Metodo (ricorsivo) per stampare il json a blocchi di 4000 caratteri
            if(str.length() > 4000) {                           //Se la stringa è maggiore di 4000 caratteri
                Log.i(TAG, str.substring(0, 4000));             //Stampo solo i primi 4000 caratteri (sottostringa di "str") Log.i -> Costante di priorità per il metodo println (INFO)
                longInfo(str.substring(4000));                  //Richiamo la funzione passando la sottostringa
            } else                                              //Altrimenti
                Log.i(TAG, str);                                //Stampo la stringa completa e termino il metodo
        }

        private String getTimeValue (String s) {//Metodo per ottenere il valore del tempo dalla lettura del json
            String time = "";        //Stringa tempo -> settata vuota
            if (s.isEmpty())         //Se "s" passata al metodo è vuota
                return time;         //Ritorno il tempo

            int i = 0;                                     //Variabile indice "i"
            while (s.charAt(i) != 'T'){                    //Controllo e fin quando non trovo il carattere "T"
                if (i > s.length())                             //Eseguo il controllo -> se l'indice "i" è maggiore della lunghezza della stringa "s"
                    return time;                                //Ritorno il tempo (in quanto non ho trovato "T")
                i++;                                       //Incremento il contatore
            }                               //Se esco dal ciclo while e arrivo a questo ciclo significa che ho trovato "T"
            for (int j = 0; j < 5; j++) {   // e di conseguenza sono pronto per gestire il tempo (5 caratteri -> 00:00)
                i++;                                       //Incremento il contatore per prendere il carattere in questione (a partire dal 1° dopo "T")
                time += s.charAt(i);                       //Aggiungo al tempo il carattere in posizione "i"
            }
            return time;            //Ritorno il tempo
        }

        private String getDateValue (String s) {//Metodo per ottenere il valore della data dalla lettura del json (legge tutto quello che c'è prima di "T")
            String date = "";       //Stringa data -> settata vuota
            if (s.isEmpty())        //Se "s" passata al metodo è vuota
                return date;        //Ritorno la data

            int i = 0;                                     //Variabile indice "i"
            while (s.charAt(i) != 'T' || i > s.length()){  //Controllo e fin quando non trovo il carattere "T" oppure "i" è maggiore della lunghezza della stringa "s"
                date += s.charAt(i);                       //Aggiungo alla data il carattere in posizione "i"
                i++;                                       //Incremento il contatore
            }
            return date;            //Ritorno la data
        }

        private String getNumericTimeValue (String s) {//Metodo per ritornare un valore "numerico", data la stringa contenente il tempo. (In modo da facilitarne la gestione per andare ad effettuare l'ordinamento temporale)
            String tmp = s.substring(0, 4);                 //Stringa temporanea settata uguale alla sottostringa "s", dal carattere 0 al 4, rappresentante il tempo
            return tmp.replace(":", "");    //Ritorno la stringa rappresentante il tempo, senza i due punti, per far sì che siano 4 valori.
        }
    }
}
