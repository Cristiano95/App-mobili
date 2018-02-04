package com.teocri.sportlogger;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//Questa classe effettua le operazioni di connessione, disconnessione, riconnessione e controllo dei permessi per la localizzazione del dispositivo.
// Setta in oltre alcuni parametri iniziali come tempo e spostamento che potranno essere modificati dalla apposita activity di impostazioni/preferences
public class MyForegroundService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    static final int CODE = 1993;          //Variabile intera codice

    static public long updateTime = 60000; //1 minuto - Settaggio variabile per aggiornamenti in relazione al tempo
    static public long updateGap = 50;     //50 metri - Settaggio variabile per aggiornamenti in relazione allo spostamento

    static final int[] priority = {     //Vettore di priorità
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, //Utilizzato per richiedere la precisione del livello di "blocco".
            LocationRequest.PRIORITY_HIGH_ACCURACY,           //Utilizzato per richiedere le posizioni più accurate disponibili.
            LocationRequest.PRIORITY_LOW_POWER,               //Utilizzato per richiedere la precisione del livello "città".
            LocationRequest.PRIORITY_NO_POWER};               //Utilizzato per richiedere la migliore precisione possibile con zero consumo di energia aggiuntivo.


    SharedPreferences prefs;         //Variabile per gestione preferenze
    DatabaseManager db;              //Variabile per gestione database
    GoogleApiClient googleApiClient; //"GoogleApiClient" viene utilizzato con una varietà di metodi statici. Alcuni richiedono che GoogleApiClient sia connesso, altri accodano le chiamate prima che sia connesso (utilizziamo per connect, disconnect e Builder)
    LocationRequest locationRequest; //Gli oggetti LocationRequest vengono utilizzati per richiedere una qualità del servizio per gli aggiornamenti di posizione (a "FusedLocationProviderApi")

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  //Metodo chiamato ogni volta che si avvia esplicitamente il servizio chiamando startService (Intent)
        //Crea notifica di avvio
        Intent notificationIntent = new Intent(this, MainActivity.class); //Creazione "notificationIntent", come nuovo intent, per visualizzare la notifica nel MainActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); //Recupera un PendingIntent che avvierà una nuova activity (ad esso passo la notifica)
        Notification notification = new Notification.Builder(this)  //Creazione nuovo oggetto di notifica
                .setContentTitle(getString(R.string.start_log_msg))            //Imposta la prima riga di testo della notifica (titolo)
                .setContentText(getString(R.string.start_trace))               //Imposta la seconda riga di testo della notifica (testo notifica)
                .setSmallIcon(R.mipmap.ic_launcher)                            //Settaggio icona "ic_launcher_gps_on" (icona verde)
                .setContentIntent(pendingIntent)                               //Settaggio contenuto dell'intent -> passando "pendingIntent"
                .build();                                                      //Combina tutte le opzioni impostate e restituisce un nuovo oggetto di notifica
        startForeground(CODE, notification);  //Servizio avviato -> Quindi rendi questo servizio eseguito in primo piano, fornendo il codice e la notifica

        // Controllo google play services
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance(); //Restituisce l'istanza singleton di "GoogleApiAvailability" e la inserisce in "googleAPI"
        int check = googleAPI.isGooglePlayServicesAvailable(this);      //"isGooglePlayServicesAvailable" -> Verifica che i servizi Google Play siano installati e abilitati su questo dispositivo e che la versione installata su questo dispositivo non sia precedente a quella richiesta da questo client.
        if (check != ConnectionResult.SUCCESS) {             //A tal proposito, dopo la creazione di "check", verifico se questa non è connessa (non è uguale a SUCCESS della "ConnectionResult")
            googleAPI.showErrorNotification(this, check);                                    //Allora visualizzo una notifica col codice di errore restituito (check)
            Toast.makeText(getApplicationContext(), getString(R.string.service_error) + check, //e stampo messaggio "Errore nel Service"
                    Toast.LENGTH_SHORT).show();
        }
        googleApiClient = new GoogleApiClient.Builder(this) //Effettuo la "Builder", del "GoogleApiClient", per configurare un GoogleApiClient.
                .addConnectionCallbacks(this)         //Fornisce i "callback" che vengono chiamati quando il client è connesso o disconnesso dal servizio.
                .addOnConnectionFailedListener(this)  //Fornisce i "callback" per gli scenari che comportano un tentativo fallito di connettere il client al servizio
                .addApi(LocationServices.API)         //Specifica quali Api sono richiesti dalla app
                .build();                             //Combina tutte le opzioni impostate e restituisce un nuovo "googleApiClient"
        db = new DatabaseManager(getApplicationContext());         //Creazione nuova variabile "DatabaseManager" per la gestione del database in relazione al singolo oggetto "Application" globale del processo corrente
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //Creazione nuova variabile "PreferenceManager" per la gestione delle preferenze
        //"getDefaultSharedPreferences" -> In tal caso ottiene un'istanza SharedPreferences in relazione al singolo oggetto "Application" globale del processo corrente

        //Settaggio dei parametri GPS
        locationRequest = new LocationRequest(); //Creazione variabile "locationRequest" utilizzata per richiedere una qualità del servizio per gli aggiornamenti di posizione
        locationRequest.setPriority(priority[1])      //Settaggio priorità della richiesta (vettore di priorità impostato in alto -> priority[])
                .setInterval(updateTime)              //Settaggio dell'intervallo desiderato per gli aggiornamenti di posizione (variabile aggiornamento tempo)
                .setFastestInterval(updateTime)       //Settaggio dell'intervallo più veloce per gli aggiornamenti di posizione (variabile aggiornamento tempo)
                .setSmallestDisplacement(updateGap);  //Settaggio dello spostamento minimo tra gli aggiornamenti di posizione (variabile aggiornamento spostamento)

        googleApiClient.connect();          //Connessione "googleApiClient" -> Apre la connessione ai Google Services
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override                       //Metodo per restituire il canale di comunicazione al servizio. (null se i client non possono associarsi al servizio)
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {                  //Metodo utilizzato (una volta connessi) per iniziare a fare richieste.
        if(!checkPermission())          //Se, tramite il metodo di controllo dei permessi, verifico che i permessi non sono garantiti effettuo la return
            return;

        Toast.makeText(this, R.string.toast_connected, Toast.LENGTH_SHORT).show();  //Se vado avanti nel metodo, stampo messaggio "Connesso"

        LocationServices.FusedLocationApi.getLastLocation(googleApiClient); //Restituisce la migliore e più recente posizione attualmente disponibile

        locationRequest.setPriority(priority[1])      //Settaggio priorità della richiesta (vettore di priorità impostato in alto -> priority[])
                .setInterval(updateTime)              //Settaggio dell'intervallo desiderato per gli aggiornamenti di posizione (variabile aggiornamento tempo)
                .setFastestInterval(updateTime)       //Settaggio dell'intervallo più veloce per gli aggiornamenti di posizione (variabile aggiornamento tempo)
                .setSmallestDisplacement(updateGap);  //Settaggio dello spostamento minimo tra gli aggiornamenti di posizione (variabile aggiornamento spostamento)

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }   //Richiede aggiornamenti relativi alla posizione.

    @Override
    public void onConnectionSuspended(int cause) {                      //Metodo chiamato quando la connessione è sospesa
        Toast.makeText(this, getString(R.string.toast_connection_suspended) + cause, Toast.LENGTH_LONG).show();
    }                                                                   //Stampo messaggio "connessione sospesa"

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {  //Metodo chiamato quando la connessione è fallita
        Toast.makeText(this, getString(R.string.toast_connection_failed) + result, Toast.LENGTH_LONG).show();
    }                                                                   //Stampo messaggio "connessione fallita"

    @Override
    public void onLocationChanged(Location location) {          //Metodo chiamato quando la posizione è cambiata.
        double lat = location.getLatitude();        //Variabile di tipo "double" che identifica la latitudine della location "interessata"
        double lon = location.getLongitude();       //Variabile di tipo "double" che identifica la longitudine della location "interessata"

        Date date = new Date(location.getTime());                           //Variabile "date" che identifica la data della location "interessata"
        DateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");   //Creazione variabile di tipo "DateFormat" per gestire il formato della data
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");       //Creazione variabile di tipo "DateFormat" per gestire il formato dell'ora
        String day = dayFormat.format(date);                                //Settaggio stringa "giorno", passando la variabile "date" al "dayFormat" appena inizializzato
        String time = timeFormat.format(date);                              //Settaggio stringa "ora", passando la variabile "date" al "timeFormat" appena inizializzato

        db.addLocation(lat, lon, day, time, priority[1]);               //Aggiungo al database il nuovo luogo visitato tramite la "addLocation" della classe "DatabaseManager" (alla quale passo latitudine, longitudine, data, ora, vettore priority con le request location)
        MapsActivity.addMarkers(lat, lon, day + " - " + time);    //Aggiunta marker per mezzo della funzione "addMarkers" del "MapsActivity" alla quale passo "lat", "lon" e "day - time"
        MapsActivity.moveCamLatLon(lat, lon);                          //Muovo la camera in relazione a "lat" e "lon"
        Toast.makeText(this, R.string.toast_new_marker_placed, Toast.LENGTH_SHORT).show(); //Stampo messaggio "nuovo marker aggiunto"
    }

    private boolean checkPermission() {     //Metodo utilizzato per effettuare il controllo dei permessi
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), R.string.toast_permission_error, Toast.LENGTH_SHORT).show(); //Se i due permessi contenuti nel "manifest" non sono "garantiti"
            return  false;  //Stampo messaggio "Errore nel controllo dei permessi" e ritorno "false"
        }
        return true;        //Nel caso in cui non si verifichi un errore (non entro nell' "if") ritorno "true"
    }

    @Override
    public void onDestroy() {           //Metodo per eseguire qualsiasi pulizia finale prima che l'activity venga "distrutta"
        stopForeground(true);    //Servizio fermato -> inoltre rimuovo la notifica
        googleApiClient.disconnect();           //Disconnessione "googleApiClient" -> Chiude la connessione ai Google Services
        db.close();                             //Chiusura database
        super.onDestroy();
    }

}
