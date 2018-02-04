package com.teocri.sportlogger;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.teocri.sportlogger.MainActivity.db;
    //Questa è la classe con cui l’utente si interfaccerà con mappa, streetView e tutte le funzionalità offerte dal menu.
    //Effettua anche tutte le intent per accedere alle altre 2 activities PreferencesRequestActivity e ReportLocation.
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback, GoogleMap.OnCameraMoveListener, StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener, View.OnTouchListener {

    public static StreetViewPanorama mStreet;                        //Dichiarazione variabile StreetView
    public static GoogleMap mMap;                                    //Dichiarazione variabile Mappa
    SupportStreetViewPanoramaFragment streetFragment;                //Dichiarazione variabile fragment per la "StreetView"
    SupportMapFragment mapFragment;                                  //Dichiarazione variabile fragment per la "Mappa"
    public static ArrayList<Marker> markers = new ArrayList<>();     //Dichiarazione lista di markers
    FrameLayout fl;                                                  //Dichiarazione "FrameLayout" fl -> invisibile, per gestire l'evento di touch ("onTouch") (sopra a tutto)
    boolean upDownSide = true;                                       //Variabile booleana per cattuarare e gestire l'evento di touch ("onTouch")
    public static boolean newOptions = false;                        //Variabile booleana per sapere se, al momento di effettuare "onResume", sono cambiate delle opzioni

    private static Intent logger;                                    //Dichiarazione variabile "intent" per effettuare la connessione (MyForegroundService)

    @Override
    protected void onCreate(final Bundle savedInstanceState) {    //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);                             //Tramite setContentView collego all'activity il file xml relativo

        //  MENU
        getSupportActionBar().setDisplayShowHomeEnabled(true);              //Settaggio per includere l'accessibilità alla home nella barra delle azioni.
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_gps_off);        //Settaggio icona dell'applicazione

        //  MAPS
        streetFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetView); //Inizializzazione fragment della streetView in relazione all'id nel file activity_maps.xml
        streetFragment.getStreetViewPanoramaAsync(this); //"getStreetViewPanoramaAsync" inizializza automaticamente il sistema "streetview" e la view

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map); //Inizializzazione fragment della mappa in relazione all'id nel file activity_maps.xml
        mapFragment.getMapAsync(this);  //"getMapAsync" inizializza automaticamente il sistema "mappa" e la view

        fl = (FrameLayout) findViewById(R.id.frameLayout);  //Inizializzazione FrameLayout "fl" in relazione a quello inserito (activity_maps.xml)
        fl.setOnTouchListener(this);                        //Settato in relazione all'evento di touch effettuato

    }


    /**********_MENU_**********/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {   //Creazione menù
        MenuInflater inflater = getMenuInflater();                    //Variabile "inflater" di tipo "MenuInflater" per la gestione del menù
        inflater.inflate(R.menu.main_menu, menu);                     //Funzione inflate per la creazione di menù, gestione parte grafica menù -> menu/main_menu.xml
        return super.onCreateOptionsMenu(menu);                       //true -> crea menù - false -> non crea menù/errore
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {           //Selezionando il menù
        switch (item.getItemId()) {                                 //Switch in relazione all'id dell'item cliccato nel menù
            case R.id.item_1:                       //Caso 1
                startPreferencesRequest(getCurrentFocus());   //Richiama la funzione "startPreferencesRequest" per la scelta dei parametri di gestione,
                return true;                                  //per l'inserimento dei marker, in relazione a tempo trascorso e metri/chilometri percorsi

            case R.id.item_2:                       //Caso 2
                if (!isMyServiceRunning(MyForegroundService.class)) {           //Se non sono connesso (tramite la classe "MyForegroundService")
                    logger = new Intent(this, MyForegroundService.class);   //Creazione intent "logger" (descrizione astratta di un'operazione) -> mi servo della classe "MyForegroundService" per effettuare la connessione
                    startService(logger);                                                //Richiede l'avvio di un determinato servizio dell'applicazione. (gli passo l'intent "logger")
                    Toast.makeText(this, "connecting", Toast.LENGTH_SHORT).show();            //Stampo il messaggio "Mi sto connettendo"
                    getSupportActionBar().setIcon(R.mipmap.ic_launcher_gps_on);                          //Settaggio icona "ic_launcher_gps_on" (icona verde)
                } else                                                          //Altrimenti
                    Toast.makeText(this, R.string.toast_alrady_connected, Toast.LENGTH_SHORT).show();         //Stampo il messaggio "Sono già connesso"
                return true;

            case R.id.item_3:                       //Caso 3
                if (isMyServiceRunning(MyForegroundService.class)) {            //Se sono connesso (tramite la classe "MyForegroundService")
                    stopService(logger);                                                 //Richiede lo stop di un determinato servizio dell'applicazione. (gli passo l'intent "logger")
                    Toast.makeText(getApplicationContext(), R.string.toast_disconnected, Toast.LENGTH_SHORT).show();  //Stampo il messaggio "Disconnesso"
                    getSupportActionBar().setIcon(R.mipmap.ic_launcher_gps_off);                         //Settaggio icona "ic_launcher_gps_off" (icona rossa)
                } else                                                          //Altrimenti
                    Toast.makeText(getApplicationContext(), R.string.toast_disconnected, Toast.LENGTH_SHORT).show();  //Stampo il messaggio "Disconnesso"
                return true;

            case R.id.item_4:                       //Caso 4
                startReportLocations(getCurrentFocus());      //Richiama la funzione "startReportLocations" per mostrare, in una nuova activity, i giorni
                return true;                                  //in cui è stato inserito almeno un marker e il numero di markers per quel dato giorno.

            case R.id.item_5:                       //Caso 5
                showAlert();                                  //Richiama la funzione "showAlert" per decidere se eliminare o meno i dati dal database
                return true;

            case R.id.item_6:                       //Caso 6
                streetViewVisibility(0);                    //Chiusura streeView (Rimango solo con la mappa a schermo intero)
                return true;

            default:
                return super.onOptionsItemSelected(item);   //true -> mostra menù - false -> non mostrare menù | Per prima cosa viene sempre istanziato un oggetto della classe madre; ma se per istanziare un oggetto della classe madre devo passare degli argomenti al costruttore, allora sarà la classe figlia che si prenderà in carico l'onere di passarglieli.
        }
    }

    private void showAlert() {          //Metodo per eliminare i dati presenti all'interno del database
        AlertDialog.Builder builder;            //Variabile per la gestione del messaggio di allerta
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {        //Se la versione dell'"SDK" è superiore o uguale a "LOLLIPOP"
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);   //Costruzione builder (Tema per finestre di dialogo di avviso, utilizzate dalla classe AlertDialog)
        } else {                                                            //Altrimenti
            builder = new AlertDialog.Builder(this);                                                //Costruzione builder
        }
        builder.setTitle(R.string.delete_db)                                               //Settaggio titolo "Cancellazione database"
                .setMessage(R.string.message_sure_to_delete)        //Messaggio di conferma dell'eliminazione dei dati del database
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {                        //Nel caso in cui viene cliccato sul tasto "conferma"
                        streetViewVisibility(0);                                                          //Chiusura streeView(Rimane la mappa a schermo intero)
                        db.onUpgrade(db.getWritableDatabase(), 1, 1);                    //Aggiornamento al db -> Cancellazione tramite "onUpgrade". "getWritableDatabase" = Creare e / o aprire un database che verrà utilizzato per la lettura e la scrittura.
                        removeMarkers();                                                                    //Tramite il metodo "removemarkers" rimuovo tutti i markers
                        Toast.makeText(getApplicationContext(), R.string.toast_db_cleared, Toast.LENGTH_SHORT).show();   //Stampo il messaggio "Database liberato"
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {     //Nel caso in cui viene cliccato sul tasto "annulla"
                    public void onClick(DialogInterface dialog, int which) {  }                             //Eseguo il codice di "onClick" e quindi non faccio nulla
                })
                .setIcon(android.R.drawable.ic_dialog_alert)                        //Settaggio icona di allerta (nel messaggio di eliminazione db)
                .show();                                                            //".show" per mostrare a video
    }

    /**********_INITIAL_SETUP_**********/
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) { //Metodo utilizzato quando "StreetView" è pronta per essere utilizzata.
        mStreet = streetViewPanorama;                                //Variabile "mStreet" uguale alla "streetViewPanorama" -> è un'immagine (o insieme di immagini) che fornisce una vista a 360 gradi da una singola posizione
        mStreet.setOnStreetViewPanoramaCameraChangeListener(this);  //Settaggio "mStreet" in base al punto in cui clicco
        streetViewVisibility(0);                                 //Settaggio streeView a "chiusa" (Rimane la mappa a schermo intero)
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {   //Metodo utilizzato quando "Mappa" è pronta per essere utilizzata.
        mMap = googleMap;                 //Variabile "mMap" uguale alla "googleMap" -> immagine che fornisce una vista sulla mappa del mondo

        setUpZoom();                      //Richiamo la funzione per settare lo Zoom
        loadDB();                         //Richiamo la funzione per il caricamento del database

        mMap.setOnCameraMoveListener(this);                     //Settaggio movimento mappa in relazione al tocco (sul mMap)
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {             //Settaggio visibilità marker in relazione a quando lo tocco
                moveCameraPos(marker);                         //In quel caso muovo la camera al marker stesso (selezionato)
                return false;
            }
        });
    }

    private int loadDB() {     //Richiamo la funzione per il caricamento del database
        ArrayList<String> locations = db.getRows();               //Variabile "locations" come lista di luoghi, richiamo la "getRows" che ritorna un ArrayList contenente i luoghi
        setMarkers(locations);                                    //Aggiungo i makers al database in relazione ai luoghi visitati
        return locations.size();                                  //Ritorno il numero di luoghi
    }

    public static void initialPos(double lat, double lon) { //Metodo per settaggio della posizione iniziale
        LatLng position = new LatLng(lat, lon);                   //Variabile posizione -> creazione variabile di tipo "LatLng" in relazione a latitudine e longitudine
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));    //Tramite "CameraUpdateFactory", per modificare la posizione della camera, setto lo zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position)); //Tramite "CameraUpdateFactory", per modificare la posizione della camera, setto
    }                                                             //la nuova posizione tramite funzione "newLatLng"

    private void setUpZoom() { //Metodo per settaggio dello Zoom
        mMap.setMinZoomPreference(0.0f);                            //Zoom minimo 0 (valore float)
        mMap.setMaxZoomPreference(30.0f);                           //Zoom massimo 30 (valore float)
    }

    /**********_START_ACTIVITIES_**********/
    public void startReportLocations(View v) {      //Metodo per passaggio da MapsActivity a ReportLocations
        Intent intent = new Intent(this, ReportLocations.class);   //Un intent è una descrizione astratta di un'operazione. (Passaggio da MapsActivity a ReportLocations)
        startActivity(intent);                                                 //In tal caso usato con startActivity per avviare la nuova activity
    }

    public void startPreferencesRequest(View v) {   //Metodo per passaggio da MapsActivity a PreferencesRequestActivity
        Intent intent = new Intent(this, PreferencesRequestActivity.class); //Un intent è una descrizione astratta di un'operazione. (Passaggio da MapsActivity a PreferencesRequestActivity)
        startActivity(intent);                                                          //In tal caso usato con startActivity per avviare la nuova activity
    }

    /**********_ON_EVENTS_**********/
    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) { //Questo metodo gestisce il movimento della mappa (sto cliccando la streetView)
        if (upDownSide == false)                                       //Controllo che "upDownSide" sia false, e in tal caso
            mMap.moveCamera(                                           //mi servo della funzione "moveCamera"
                    CameraUpdateFactory.newCameraPosition              //e sposto la mappa in relazione alla nuova posizione
                            (new CameraPosition(mMap.getCameraPosition().target,        //Settaggio "target" -> (latitude/longitude location)
                                                mMap.getCameraPosition().zoom,          //Settaggio "zoom"
                                                mMap.getCameraPosition().tilt,          //Settaggio "tilt" -> Inclinazione (angolo di visione). L'inclinazione definisce la posizione della telecamera su un arco tra posizione centrale della mappa e superficie della Terra
                                                mStreet.getPanoramaCamera().bearing))); //Settaggio "bearing" -> Orientamento. Il rilevamento della videocamera è la direzione in cui punta una linea verticale sulla mappa, misurata in gradi in senso orario da nord.
    }

    @Override
    public void onCameraMove() {        //Questo metodo gestisce il movimento della streetview (sto cliccando la mappa)
        if (upDownSide == true)                                        //Controllo che "upDownSide" sia true, e in tal caso
            mStreet.animateTo(                                         //mi servo della funzione "animateTo"
                    new StreetViewPanoramaCamera(                      //e sposto la streetview in relazione alla nuova posizione
                            mStreet.getPanoramaCamera().zoom,                           //Settaggio zoom
                            mStreet.getPanoramaCamera().tilt,                           //Settaggio "tilt" -> Inclinazione (angolo di visione). L'inclinazione definisce la posizione della telecamera su un arco tra posizione centrale della mappa e superficie della Terra
                            mMap.getCameraPosition().bearing), 0);                    //Settaggio "bearing" -> Orientamento. Il rilevamento della videocamera è la direzione in cui punta una linea verticale sulla mappa, misurata in gradi in senso orario da nord.
    }                                                                  //"animatedTo" -> primo campo: "camera" | secondo campo: "Duration" uguale a 0

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {  //Metodo per cattuarare e gestire l'evento di touch
        if (motionEvent.getY() > streetFragment.getView().getHeight()) {  //Se la posizione dove ho cliccato sullo schermo è maggiore in altezza rispetto allo "streetFragment"
            upDownSide = true;                                            //Allora setto la variabile "upDownSide" a true (Tocco la mappa e quindi gestisco il movimento della streetview)
            return false;                                                 //e ritorno false
        }
        upDownSide = false;                                               //Allora setto la variabile "upDownSide" a false (Tocco la streetview e quindi gestisco il movimento della mappa)
        return false;                                                     //e ritorno false
    }

    @Override                                                   //Metodo per tornare indietro cliccando il tasto "indietro", che porta all'activity precedente
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void onResume() {     //Metodo per far riprendere all'activity l'interazione con l'utente
        super.onResume();
        if (newOptions == true) {   //Variabile "newOptions" true -> cambiate opzioni quindi devo effettuare la "reconnect"
            reconnect();
            newOptions = false;     //e successivamente settare la variabile a false (ora non ci sono nuove opzioni)
        }
    }

    /**********_CAMERA_METHODS_**********/
    private void moveCameraPos(Marker marker) {     //Metodo per muovere la posizione della camera in relazione al marker interessato
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition())); //Setto la posizione della mappa, creando la nuova posizione "newLatLng" e in relazione alla posizione del marker
        mStreet.setPosition(marker.getPosition());  //Setto la posizione della streetview, in relazione alla posizione del marker
        streetViewVisibility(1);    //Faccio si che la StreetView sia visibile
    }

    static public void moveCamLatLon(Double lat, Double lon) {  //Metodo per muovere la camera in relazione alla latitudine e longitudine
        LatLng pos = new LatLng(lat, lon);                  //Creazione "pos" che identifica la nuova posizione "newLatLng" in relazione ai valori di lat e lon passati al metodo
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));//Setto la posizione della mappa, creando la nuova posizione "newLatLng" in relazione a pos
        mStreet.setPosition(pos);                           //Setto la posizione della streetview, in relazione alla posizione
    }

    /**********_MARKER_METHODS_**********/
    static public Marker addMarkers(double lat, double lon, String name) {  //Metodo per l'aggiunta dei Markers
        return mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name));
    }   //Aggiungo per mezzo della "addMarker", alla quale passo come "nuova opzione" la posizione

    private int removeMarkers() {   //Metodo per la rimozione dei Markers
        int ret = markers.size();                   //Variabile "ret" formata dal numero di markers
        for (int i = 0; i < markers.size(); i++)        //Per ogni marker presente,
            markers.remove(i);                          //Eseguo la remove, con la quale lo rimuovo
        mMap.clear();                               //Faccio una "pulizia" della mappa
        return ret;                                 //Ritorno "ret"
    }

    private void setMarkers(ArrayList<String> locations) {                                          //Metodo per settare i markers
        //Se l'array di liste di luoghi ha almeno un luogo, gestisco il primo elemento (zero)
        if (locations.size() > 0) { //Effettuo l'aggiunta del marker 0 all'interno dell'array di marker "markers" e setto la posizione iniziale a questo marker grazie alla funzione "initialPos"
            markers.add(0, addMarkers(Double.parseDouble(db.readLatitude(locations.get(0))), Double.parseDouble(db.readLongitude(locations.get(0))), db.readDate(locations.get(0)) + " - " + db.readTime(locations.get(0))));
            initialPos(Double.parseDouble(db.readLatitude(locations.get(0))), Double.parseDouble(db.readLongitude(locations.get(0))));
            for (int i = 1; i < locations.size(); i++)                  //Per ogni altro elemento della lista "locations" (dal secondo [i=1] all'ultimo)
                markers.add(i, addMarkers(Double.parseDouble(db.readLatitude(locations.get(i))), Double.parseDouble(db.readLongitude(locations.get(i))), db.readDate(locations.get(i)) + " - " + db.readTime(locations.get(i))));
        }       //Effettuo l'aggiunta del marker "i" all'interno dell'array di marker "markers". Quindi passando alla "add" l'indice e la funzione "addMarkers"
    }           //La funzione "addMarkers" crea il "marker", passandogli latitudine, longitudine e nome (formato da data e ora)

    /**********_UTILITIES_**********/
    private void streetViewVisibility(int i) { //Metodo per mostrare o meno la StreetView
        mapFragment.getView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, i));
    }   //In relazione alla funzione getView(), gestisco la visibilità della StreetView, passando diversi parametri di gestione del "Layout" e passando i
        //i = 1 -> mostrare StreetView | i = 0 -> non mostrare StreetView

    private void reconnect() {              //Metodo per effettuare la riconnessione
        if (!isMyServiceRunning(MyForegroundService.class))     //Se non son connesso e quindi il mio servizio non è in stato di running eseguo la return
            return;
                                                                                                  //Stampo messaggio
        Toast.makeText(this, R.string.toast_reconnectiong_for_changes, Toast.LENGTH_SHORT).show();  //"Mi sto riconnettendo per applicare le modifiche"
        stopService(logger);                                                //Richiede lo stop di un determinato servizio dell'applicazione. (gli passo l'intent "logger")
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_gps_off);        //Settaggio icona "ic_launcher_gps_off" (icona rossa)
        logger = new Intent(this, MyForegroundService.class);  //Creazione intent "logger" (descrizione astratta di un'operazione) -> mi servo della classe "MyForegroundService" per effettuare la connessione
        startService(logger);                                               //Richiede l'avvio di un determinato servizio dell'applicazione. (gli passo l'intent "logger")
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_gps_on);         //Settaggio icona "ic_launcher_gps_on" (icona verde)
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) { //Metodo per controllare se sono o meno connesso, e quindi se il "Service" è in stato di "Running"
        //Creo variabile "manager", "getSystemService" per recuperare un "ActivityManager" per l'interazione con lo stato globale del sistema.
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) { //Scorro ogni Service "Running" presente
            if (serviceClass.getName().equals(service.service.getClassName())) { //Se il nome del "Service" dato al metodo è uguale a quello che sto analizzando
                return true;    //Ritorno true
            }
        }
        return false;           //Se esco dal ciclo ritorno false
    }

}
