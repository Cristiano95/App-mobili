package com.teocri.conference;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
//Questa classe ha il compito di creare la connessione tramite url e effettuare la GET del file JSON da cui leggiamo i dati nel MainActivity.
public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName(); //Scrivo nella stringa "TAG" -> semplicemente il nome della classe dell'activity (in tal caso "HttpHandler") ( Di solito identifica la classe o l'attività in cui si verifica la chiamata)
    public HttpHandler() {}

    public String makeServiceCall(String reqUrl) {       //Metodo per la richiesta all'url, connessione e get del file json
        String response = null;                         //Stringa da ritornare
        try {
            URL url = new URL(reqUrl);                                         //Variabile url -> uguale al reqUrl passato alla funzione (dal MainActivity)
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //Variabile di tipo "HttpURLConnection" per effettuare la connessione all'url in questione
            conn.setRequestMethod("GET");                                      //Tramite la variabile "conn", effettuo la GET del file json

            InputStream in = new BufferedInputStream(conn.getInputStream());//Creazione variabile di tipo "InputStream", sulla base dell'input preso dalla connessione
            response = convertStreamToString(in);                           //Inserisco in response il risultato della funzione "convertStreamToString" al quale passo "in" (InputStream)
                                                     //Casi di errore
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());  //Eccezione URL non valida
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());      //Protocollo di eccezione
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());            //Eccezione di I/O
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());              //Eccezione
        }
        return response;                                //Ritorno la stringa "response"
    }

    private String convertStreamToString(InputStream is) {       //Metodo per effettuare la conversione in stringa
        BufferedReader reader = new BufferedReader(new InputStreamReader(is)); //Crea un flusso di input di caratteri buffering, che utilizza un buffer di input di dimensioni predefinite (is)
        StringBuilder sb = new StringBuilder();                                //Creazione variabile di tipo "StringBuilder" (Costruisce un generatore di stringhe senza caratteri e una capacità iniziale di 16 caratteri)
        String line;                                                           //Stringa "linea"

        try {
            while ((line = reader.readLine()) != null) {       //Metto in "line" la lettura effettuata sulla riga e fin quando è diversa da "null"
                sb.append(line).append('\n');                  //allego ad "sb" la linea e vado a capo
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();                  //Effettuo la chiusura dell'inputStream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();                //Ritorno "sb"
    }
}