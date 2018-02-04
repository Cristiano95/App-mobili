package com.teocri.trainmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//Il MainActivity è la classe con cui l’utente potrà monitorare la situazione dei treni a video. Da questa activity vengono gestite le chiamate alle altre activity e il menu.
public class MainActivity extends AppCompatActivity {

    public static final int TRAIN_SLOTS = 6;                        //Dichiarazione e inizializzazione slot dei treni

    TextView[] trainNumbersTW    = new TextView[TRAIN_SLOTS];       //trainNumbersTextView - Textview riferita al numero treno
    TextView[] trainTimeTW       = new TextView[TRAIN_SLOTS];       //trainTimeTextView - Textview riferita alla situazione treno (in orario, ritardo ..)
    TextView[] trainDD           = new TextView[TRAIN_SLOTS];       //trainDepartureDestination - Textview riferita alle indicazioni partenza-destinazione
    LinearLayout[] linearLayouts = new LinearLayout[TRAIN_SLOTS];   //Dichiarazione e inizializzazione LinearLayout riferiti a ogni blocco (line)
    ProgressBar[] progressBars   = new ProgressBar[TRAIN_SLOTS];    //Dichiarazione e inizializzazione ProgressBar
    Button[] buttons             = new Button[TRAIN_SLOTS];         //Dichiarazione e inizializzazione bottoni (start-stop)

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                 //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayShowHomeEnabled(true);  //Settaggio per includere l'accessibilità alla home nella barra delle azioni.
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);    //Settaggio icona dell'applicazione

        for (int i = 0; i < TRAIN_SLOTS; i++){ //Per ogni Slot di monitoraggio dei treni
            String id = "textViewTN" + (i+1);  //Creazione stringa id in relazione al nome "textViewTN" dell'xml. Quindi col numero finale che identifica a quale slot appartiene.
            int temp = getResources().getIdentifier(id, "id", getPackageName()); //Creazione "temp" in relazione alla "getIdentifier" che restituisce un identificatore di risorsa, per il nome di risorsa specificato.
            trainNumbersTW[i] = (TextView)findViewById(temp);    //Settaggio di ogni textViewTN, in relazione all'id cliccato (temp)
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){  //Per ogni Slot di monitoraggio dei treni
            String id = "textViewTT" + (i+1);   //Creazione stringa id in relazione al nome "textViewTT" dell'xml. Quindi col numero finale che identifica a quale slot appartiene.
            int temp = getResources().getIdentifier(id, "id", getPackageName()); //Creazione "temp" in relazione alla "getIdentifier" che restituisce un identificatore di risorsa, per il nome di risorsa specificato.
            trainTimeTW[i] = (TextView)findViewById(temp);       //Settaggio di ogni textViewTT, in relazione all'id cliccato (temp)
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){  //Per ogni Slot di monitoraggio dei treni
            String id = "textViewDD" + (i+1);   //Creazione stringa id in relazione al nome "textViewDD" dell'xml. Quindi col numero finale che identifica a quale slot appartiene.
            int temp = getResources().getIdentifier(id, "id", getPackageName()); //Creazione "temp" in relazione alla "getIdentifier" che restituisce un identificatore di risorsa, per il nome di risorsa specificato.
            trainDD[i] = (TextView)findViewById(temp);           //Settaggio di ogni textViewDD, in relazione all'id cliccato (temp)
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){  //Per ogni Slot di monitoraggio dei treni
            String id = "line" + (i+1);         //Creazione stringa id in relazione al nome "line" dell'xml. Quindi col numero finale che identifica a quale slot appartiene.
            int temp = getResources().getIdentifier(id, "id", getPackageName()); //Creazione "temp" in relazione alla "getIdentifier" che restituisce un identificatore di risorsa, per il nome di risorsa specificato.
            linearLayouts[i] = (LinearLayout) findViewById(temp);//Settaggio di ogni layout, in relazione all'id cliccato (temp)
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){  //Per ogni Slot di monitoraggio dei treni
            String id = "updateT" + (i + 1);    //Creazione stringa id in relazione al nome "updateT" dell'xml. Quindi col numero finale che identifica a quale slot appartiene.
            int temp = getResources().getIdentifier(id, "id", getPackageName()); //Creazione "temp" in relazione alla "getIdentifier" che restituisce un identificatore di risorsa, per il nome di risorsa specificato.
            buttons[i] = (Button) findViewById(temp);            //Settaggio di ogni bottone, in relazione all'id cliccato (temp)
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){  //Per ogni Slot di monitoraggio dei treni
            String id = "progressBar" + (i + 1);//Creazione stringa id in relazione al nome "progressBar" dell'xml. Quindi col numero finale che identifica a quale slot appartiene.
            int temp = getResources().getIdentifier(id, "id", getPackageName()); //Creazione "temp" in relazione alla "getIdentifier" che restituisce un identificatore di risorsa, per il nome di risorsa specificato.
            progressBars[i] = (ProgressBar) findViewById(temp);  //Settaggio di ogni progressBar, in relazione all'id cliccato (temp)
        }
        //Settaggi vari
        //Classe statica DataHolder, per tenere ordinate tutte le informazioni dei treni così che da ogni altra activity sia possibile accedere a tali dati in modo semplice e chiaro
        new DataHolder(trainNumbersTW, trainTimeTW, trainDD,  progressBars, linearLayouts, buttons, this);
        new DataReader();              //Classe DataReader, nel quale effettuo connessione e lettura dell'html per gestire la situazione dei treni
        new ThreadHive(TRAIN_SLOTS);   //Classe ThreadHive, passandogli il numero di slot dei treni, per la gestione dei thread con diverse operazioni
        onReopening();                 //Metodo OnReopening per la riapertura dell'applicazione
    }

    /**********_START_ACTIVITIES_**********/
    public void startEditActivity(View v, int ind) {    //Metodo per passaggio da MainActivity a EditActivity
        Intent intent = new Intent(MainActivity.this, EditActivity.class);             //Un intent è una descrizione astratta di un'operazione. (Passaggio da MainActivity a EditActivity)
        intent.putExtra("index", ind);                                                        //Tramite putExtra() passo i dati tra le due activity
        startActivity(intent);                                                                      //In tal caso usato con startActivity per avviare la nuova activity
    }

    public void startSettingActivity(View v, int ind) { //Metodo per passaggio da MainActivity a EditTrainSettingsActivity
        Intent intent = new Intent(MainActivity.this, EditTrainSettingsActivity.class);//Un intent è una descrizione astratta di un'operazione. (Passaggio da MainActivity a EditTrainSettingsActivity)
        intent.putExtra("index", ind);                                                        //Tramite putExtra() passo i dati tra le due activity
        startActivity(intent);                                                                      //In tal caso usato con startActivity per avviare la nuova activity
    }

    public void startPreferences(View v) {              //Metodo per passaggio da MainActivity a EditPreferences
        Intent intent = new Intent(MainActivity.this, EditPreferences.class);          //Un intent è una descrizione astratta di un'operazione. (Passaggio da MainActivity a EditPreferences)
        startActivity(intent);                                                                      //In tal caso usato con startActivity per avviare la nuova activity
    }

    /**********_MENU_**********/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {     //Creazione menù
        MenuInflater inflater = getMenuInflater();                //Variabile inflater di tipo MenuInflater per la gestione del menù
        inflater.inflate(R.menu.main_menu, menu);                 //Funzione inflate per la creazione di menù, gestione parte grafica menù -> menu/main_menu.xml
        return super.onCreateOptionsMenu(menu);                   //true -> crea menù - false -> non crea menù/errore
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //Selezionando il menù
        switch (item.getItemId()) {                         //Switch in relazione all'id dell'item cliccato nel menù
            case R.id.item_preferences:                            //Caso tasto preferenze - in relazione a id presente in main_menu.xml
                startPreferences(getCurrentFocus());                          //Richiama il metodo startPreferences(), per il passaggio alla EditPreferences
                return true;

            case R.id.item_refresh:                                //Caso tasto refresh - in relazione a id presente in main_menu.xml
                resetToDefault();                                             //Richiamo il metodo resetToDefault per il ripristino delle preferences
                ThreadHive.DestroyHive();                                     //Mi servo del ThreadHive per DestroyHive mediante la quale faccio la kill dei thread
                DataHolder.resetData();                                       //Mi servo del DataHolder per fare la resetData
                DataHolder.updateAllMainScreen();                             //Mi servo del DataHolder per aggiornare la situazione di tutti i blocchi
                Toast.makeText(getApplicationContext(), R.string.toast_reset_done, Toast.LENGTH_SHORT).show(); //Toast per messaggio di conferma del refresh
                return true;                                                                                   //in values-it/strings.xml

            default:
                return super.onOptionsItemSelected(item);   //true -> mostra menù - false -> non mostrare menù | Per prima cosa viene sempre istanziato un oggetto della classe madre; ma se per istanziare un oggetto della classe madre devo passare degli argomenti al costruttore, allora sarà la classe figlia che si prenderà in carico l'onere di passarglieli.
        }
    }

    /**********_BUTTONS_CLICKED_**********/
    public void pollingStatus(View view) {     //Metodo per gestione del polling
        Button b = (Button)view;               //Variabile button per la modifica del testo del bottone
        int ind = getIndexLine(view);          //Variabile indice settato allo slot della linea interessata

        if (DataHolder.pStatus[ind]) {                  //Se lo stato di polling, presente in posizione ind, è true -> polling attivo
            if(DataHolder.tStatus[ind]!=-1) {           //Controllo se lo stato del treno, in posizione ind, è diverso da -1 (quindi slot non vuoto)
                DataHolder.set_tStatus(ind, 0);           //Tramite il DataHolder, setto lo stato del treno, in posizione ind, a 0 -> "Monitorato ma polling stop"
                ThreadHive.killUnit(ind);                      //Tramite la classe ThreadHive faccio la "kill" del thread in posizione ind
            }
            DataHolder.set_pStatus(ind, false);           //Tramite il DataHolder, setto lo stato del polling, del thread in posizione ind, a false
            b.setText(R.string.mainscreen_button_stop);                                 //Settaggio testo del bottone a "stop"

        }else{                                          //Altrimenti, essendo non attivo il polling status
            if(DataHolder.tStatus[ind]!=-1) {           //Controllo se lo stato del treno, in posizione ind, è diverso da -1 (quindi slot non vuoto)
                DataHolder.set_tStatus(ind, 8);           //Tramite il DataHolder, setto lo stato del treno, in posizione ind, a 8 -> "Updating"
                ThreadHive.executeUnit(ind);                   //Tramite la classe ThreadHive faccio la "execute" del thread in posizione ind
            }
            DataHolder.set_pStatus(ind, true);            //Tramite il DataHolder, setto lo stato del polling, del thread in posizione ind, a true
            b.setText(R.string.mainscreen_button_start);                                //Settaggio testo del bottone a "start"
        }
        DataHolder.updateMainScreen(ind);           //Tramite il DataHolder aggiorno la situazione a video del treno nello slot ind (in relazione alle nuove informazioni)
    }

    public void editTrain(View view) {       //Metodo per gestire i treni mediante il tasto "Edit"
        int ind = getIndexLine(view);                //Inizializzo la variabile indice allo slot della linea interessata
        if (DataHolder.tStatus[ind] == -1)           //Se lo stato del treno nello slot di indice ind è uguale a -1 (quindi slot vuoto), quindi non è presente nessun treno,
            startEditActivity(view, ind);            //Richiamo la "startEditActivity" per inserire il numero treno e tempo di polling
        else                                         //Altrimenti, essendo uno slot non vuoto,
            startSettingActivity(view, ind);         //Richiamo la "EditTrainSettingsActivity" per aggiornare il tempo di polling o per poter cancellare il treno
    }

    /**********_ON_EVENTS_**********/
    @Override                                        //Metodo per tornare indietro cliccando il tasto "indietro", che porta all'activity precedente
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void onReopening() {           //Metodo per gestire la riapertura dell'app dalla fase background
        for (int i = 0; i < ThreadHive.threadUnits.length; i++) {                       //Per ogni thread presente nel vettore threadUnits,
            if (DataHolder.tStatus[i] != -1 && DataHolder.tStatus[i] != 0) {            //Controllo lo stato del treno, se diverso da non monitorato e diverso da monitorato ma polling stop
                if (ThreadHive.threadUnits[i].getStatus() != AsyncTask.Status.RUNNING)  //e se lo stato del thread i è diverso da "In esecuzione", allora
                    ThreadHive.executeUnit(i);                                          //Tramite classe ThreadHive effettuo l'esecuzione del thread i
            }
        }
    }

    /**********_UTILITIES_**********/
    private int getIndexLine(View view) {           //Metodo per ottenere l'indice della linea interessata
        String tmp = view.getResources().getResourceName(view.getId());  //"getResourceName" restituisce il nome della risorsa, in relazione quindi all'id della view cliccata ("id" copiato in "tmp")
        int ind = Character.getNumericValue(tmp.charAt(tmp.length()-1)); //Variabile "indice" -> converto il valore della "lunghezza stringa tmp - 1" in char e
        return --ind;   //Ritorno il giusto indice "ind"                //successivamente converto tale valore in valore numerico e lo inserisco in "ind"
    }                   //(6 slot da 0 a 5)

    private void resetToDefault() {                 //Metodo per resettare le preferenze
        SharedPreferences sharedPreferences = getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit(); //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.clear().commit(); //clear -> utilizzata per rimuovere tutti i valori dalle preferenze.
    }                           //commit -> configura preferenze modificate da questo Editor, all'oggetto SharedPreferences che sta modificando.
}
