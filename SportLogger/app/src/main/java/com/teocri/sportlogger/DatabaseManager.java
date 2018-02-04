package com.teocri.sportlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
//Questa classe serve semplicemente a gestire il database per creazione, inserimento, cancellazione e query.
public class DatabaseManager extends SQLiteOpenHelper {

    public static final String TAG = "LOD";                                //Variabile "TAG" utilizzata nei diversi "Log" (Di solito identifica la classe o l'attività in cui si verifica la chiamata)
    public static final int DATABASE_VERSION = 1;                          //Variabile versione database
    public static final String DATABASE_NAME = "sportLogger_DB.db";        //Variabile nome database


    public static final String TABLE_NAME = "locations";                            //Varibile nome tabella -> luoghi
    public static final String COLUMN_NAME_LATITUDE  = "latitude";         //Varibile nome colonna latitude -> latitudine
    public static final String COLUMN_NAME_LONGITUDE = "longitude";        //Varibile nome colonna longitudine -> longitude
    public static final String COLUMN_NAME_DATE      = "date";             //Varibile nome colonna date -> data
    public static final String COLUMN_NAME_TIME      = "time";             //Varibile nome colonna time -> ora
    public static final String COLUMN_NAME_ACCURACY  = "accuracy";         //Varibile nome colonna accuracy -> accuratezza (priority -> "MyForegroundService")


    public static final String SQL_CREATE = "create table " + TABLE_NAME + " (_id integer primary key autoincrement, "      //Query di creazione tabella
            + COLUMN_NAME_LATITUDE  + " real, "                                                                      //Colonne latitudine
            + COLUMN_NAME_LONGITUDE + " real, "                                                                      //Colonne longitudine
            + COLUMN_NAME_DATE      + " text, "                                                                      //Colonne data
            + COLUMN_NAME_TIME      + " text, "                                                                      //Colonne ora
            + COLUMN_NAME_ACCURACY  + " real) ";                                                                     //Colonne accuratezza

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;     //Query di cancellazione tabella (se esiste)

    public DatabaseManager(Context context) {           //Costruttore del database
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {       //onCreate() -> per inizializzare l'activity
        Log.w(TAG,"onCreate");     //Messaggio di avviso -> contiene il TAG e il messaggio interessato, in tal caso "Messaggio di creazione"
        db.execSQL(SQL_CREATE);         //Effettuo, sul db, l'operazione di creazione della tabella
    }   //Con "execSQL" eseguo una singola istruzione SQL che NON è SELECT o altra istruzione SQL che restituisce dati

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {      //Metodo per aggiornare il database
        db.execSQL(SQL_DROP);  //Operazione di cancellazione (Esegui una singola istruzione SQL che NON è SELECT o altra istruzione SQL che restituisce dati)
        onCreate(db);          //Richiamo il metodo "onCreate" per ricreare il db
    }

    void addLocation(double lat, double lon, String date, String time, double acc) {    //Metodo per aggiungere luogo al database
        ContentValues values = new ContentValues();             //Crea un insieme vuoto di valori "values"(usando la dimensione iniziale predefinita)
        values.put(COLUMN_NAME_LATITUDE,    lat);       //Aggiungo in values, nella colonna relativa a latitudine, la latitudine passata al metodo
        values.put(COLUMN_NAME_LONGITUDE,   lon);       //Aggiungo in values, nella colonna relativa a longitudine, la longitudine passata al metodo
        values.put(COLUMN_NAME_DATE,        date);      //Aggiungo in values, nella colonna relativa a data, la data passata al metodo
        values.put(COLUMN_NAME_TIME,        time);      //Aggiungo in values, nella colonna relativa a ora, l'ora passata al metodo
        values.put(COLUMN_NAME_ACCURACY,    acc);       //Aggiungo in values, nella colonna relativa a accuratezza, l'accuratezza passata al metodo
        // Insert the new row, returning the primary key value of the new row
        SQLiteDatabase db = getWritableDatabase();                                  //Creazione e/o apertura di un database "db" (verrà utilizzato per la lettura e la scrittura)
        long id = db.insert(DatabaseManager.TABLE_NAME, null, values); //Effettuo l'inserimento della riga "values" nella tabella, con la funzione di "insert" (ritorno e copio in "id", l'id della riga appena inserita o -1 se si è verificato un errore)
        db.close();                                                                 //Effettua la chiusura del database
        Log.i(TAG,"Database insertion returned:" + id);                        //Messaggio di informazione -> contiene il TAG e il messaggio interessato, in tal caso "L'inserimento nel db ritorna: (id)"
    }

    /**********_QUERIES_**********/
    ArrayList<String> getRows() {         //Metodo per ottenere un array di stringhe contenente tutti i luoghi
        String [] columns = {COLUMN_NAME_LATITUDE, COLUMN_NAME_LONGITUDE, COLUMN_NAME_DATE, COLUMN_NAME_TIME, COLUMN_NAME_ACCURACY}; //Vettore di stringhe "colonne"
        ArrayList<String> a = new ArrayList<>(); //Array di stringhe "a" utilizzato come valore di ritorno
        try {
            SQLiteDatabase db = getReadableDatabase();   //Utilizzo db per creare e/o aprire un database.
            Cursor c = db.query(TABLE_NAME, columns, null, null, null, null, null); //Query al db prendendo solo tabella e colonne, inserisco il risultato in "c"
            Log.i(TAG, "Locations returned: " + c.getCount());  //Messaggio di informazione -> contiene il TAG e il messaggio interessato, in tal caso "Posizione restituita: "
            while (c.moveToNext()) {        //Fin quando "c" ha elementi successivi (righe successive)
                double lat  = c.getDouble(c.getColumnIndexOrThrow(COLUMN_NAME_LATITUDE));   //Settaggio "lat" in relazione al valore preso dalla riga interessata e colonna "latitudine" | ("getColumnIndexOnThrow" -> Restituisce l'indice a base zero per il nome di colonna specificato)
                double lon  = c.getDouble(c.getColumnIndexOrThrow(COLUMN_NAME_LONGITUDE));  //Settaggio "lon" in relazione al valore preso dalla riga interessata e colonna "longitudine"
                String date = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME_DATE));       //Settaggio "date" in relazione al valore preso dalla riga interessata e colonna "data"
                String time = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME_TIME));       //Settaggio "time" in relazione al valore preso dalla riga interessata e colonna "ora"
                double acc  = c.getDouble(c.getColumnIndexOrThrow(COLUMN_NAME_ACCURACY));   //Settaggio "acc" in relazione al valore preso dalla riga interessata e colonna "accuratezza"

                String s = new String("");                              //Creazione stringa "s" vuota
                s += lat + "A" + lon + "O" + date + "D" + time + "T" + acc;    //Aggiungo ad "s" la riga appena gestita

                a.add(s);       //Aggiungere all'array di stringhe "a" la riga "s"
            }
            c.close();  db.close();         //Chiusura "cursore" e "database"
        } catch (Throwable t) {Log.e(TAG,"getRows: " + t.toString(),t);}  //Messaggio di errore -> contiene il TAG e il messaggio interessato, in tal caso "Ottiene le righe: (t)"
        return a;               //Ritorno l'array di stringhe "a"
    }

    ArrayList<String> getSingleDates() {      //Metodo per ottenere una lista di stringhe con le singole date
        ArrayList<String> a = new ArrayList<>();            //Array di stringhe "a" utilizzato come valore di ritorno
        try {
            String SINGLE_DATES_QUERY = String.format("SELECT %s FROM %s GROUP BY %s",      //Query di selezione delle date
                            COLUMN_NAME_DATE,                                                          //Seleziono la colonna delle date
                            TABLE_NAME,                                                                //nella tabella interessata
                            COLUMN_NAME_DATE,                                                          //raggruppando per data
                            COLUMN_NAME_DATE);
            SQLiteDatabase db = getReadableDatabase();   //Utilizzo db per creare e/o aprire un database.
            Cursor c = db.rawQuery(SINGLE_DATES_QUERY, null);   //Variabile "cursor" di nome "c", in esso salvo il risultato della "rawQuery"
                                                                          //che esegue l'SQL fornito e restituisce un cursore sul set dei risultati.
            while (c.moveToNext()) {        //Fin quando "c" ha elementi successivi (righe successive)
                String date = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME_DATE));   //Settaggio "date" in relazione al valore preso dalla riga interessata e colonna "data"
                a.add(date);                                                            //Aggiungere all'array di stringhe "a" la data "date"
            }
            c.close();  db.close();         //Chiusura "cursore" e "database"
        } catch (Throwable t) {Log.e(TAG,"getSingleDates: " + t.toString(),t);} //Messaggio di errore -> contiene il TAG e il messaggio interessato, in tal caso "Ottiene le date: (t)"
        return a;               //Ritorno l'array di stringhe "a"
    }

    String getDateIstances(String date) {      //Metodo per ritornare da una singola data i relativi elementi aggiunti per la data stessa (passata al metodo)
        String n = "9999";           //Stringa "n" per la conta degli elementi (come valore di ritorno)
        try {
            String SINGLE_DATES_QUERY = String.format("SELECT %s FROM %s WHERE %s = '%s'",      //Query di selezione di una singola data
                            COLUMN_NAME_DATE,                                                           //Seleziono la colonna delle date
                            TABLE_NAME,                                                                 //nella tabella interessata
                            COLUMN_NAME_DATE,                                                           //dove la data è uguale a "date" passata al metodo
                            date);
            SQLiteDatabase db = getReadableDatabase();  //Utilizzo db per creare e/o aprire un database.
            Cursor c = db.rawQuery(SINGLE_DATES_QUERY, null);   //Variabile "cursor" di nome "c", in esso salvo il risultato della "rawQuery"
                                                                          //che esegue l'SQL fornito e restituisce un cursore sul set dei risultati.
            n = String.valueOf(c.getCount());   //Salvo in "n" il numero di numero di risultati della query, contenuti nel cursore "c"
            c.close();  db.close();             //Chiusura "cursore" e "database"
        } catch (Throwable t) {Log.e(TAG,"getDateIstances: " + t.toString(),t);} //Messaggio di errore -> contiene il TAG e il messaggio interessato, in tal caso "Ottiene la data: (t)"
        return n;               //Ritorno stringa "n"
    }

    String getDateLatitude(String date) {       //Metodo per ritornare da una singola data la "latitudine" della data stessa (passata al metodo) (primo marker)
        String lat = "36.1640305";   //Stringa "lat" (come valore di ritorno)
        try {
            String DATE_LATITUDE_QUERY = String.format("SELECT %s FROM %s WHERE %s = '%s' ORDER BY %s ASC",  //Query di selezione della latitudine (di una singola data in input)
                            COLUMN_NAME_LATITUDE,                                                         //Seleziono la colonna delle latitudini
                            TABLE_NAME,                                                                   //nella tabella interessata
                            COLUMN_NAME_DATE,                                                             //dove la data è uguale a "date" passata al metodo
                            date,                                                                         //ordinate in relazione all'ora
                            COLUMN_NAME_TIME);
            SQLiteDatabase db = getReadableDatabase();  //Utilizzo db per creare e/o aprire un database.
            Cursor c = db.rawQuery(DATE_LATITUDE_QUERY, null);  //Variabile "cursor" di nome "c", in esso salvo il risultato della "rawQuery"
                                                                          //che esegue l'SQL fornito e restituisce un cursore sul set dei risultati.
            c.moveToFirst();         //Passo al primo elemento trovato dalla query (quindi primo elemento all'interno del cursore)
            lat = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME_LATITUDE));  //Settaggio "lat" in relazione al primo valore nella colonna "latitude"
            c.close();  db.close();             //Chiusura "cursore" e "database"
        } catch (Throwable t) {Log.e(TAG,"getDateLatitude: " + t.toString(),t);} //Messaggio di errore -> contiene il TAG e il messaggio interessato, in tal caso "Ottiene la latitudine della data: (t)"
        return lat;             //Ritorno stringa "lat"
    }

    String getDateLongitude(String date) {      //Metodo per ritornare da una singola data la "longitudine" della data stessa (passata al metodo)
        String lon = "-115.1382751";  //Stringa "lon" (come valore di ritorno)
        try {
            String DATE_LONGITUDE_QUERY = String.format("SELECT %s FROM %s WHERE %s = '%s' ORDER BY %s ASC",  //Query di selezione della longitudine (di una singola data in input)
                            COLUMN_NAME_LONGITUDE,                                                         //Seleziono la colonna delle longitudini
                            TABLE_NAME,                                                                    //nella tabella interessata
                            COLUMN_NAME_DATE,                                                              //dove la data è uguale a "date" passata al metodo
                            date,                                                                          //ordinate in relazione all'ora
                            COLUMN_NAME_TIME);
            SQLiteDatabase db = getReadableDatabase();  //Utilizzo db per creare e/o aprire un database.
            Cursor c = db.rawQuery(DATE_LONGITUDE_QUERY, null);  //Variabile "cursor" di nome "c", in esso salvo il risultato della "rawQuery"
                                                                           //che esegue l'SQL fornito e restituisce un cursore sul set dei risultati.
            c.moveToFirst();         //Passo al primo elemento trovato dalla query (quindi primo elemento all'interno del cursore)
            lon = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME_LONGITUDE)); //Settaggio "lon" in relazione al primo valore nella colonna "longitude"
            c.close();  db.close();             //Chiusura "cursore" e "database"
        } catch (Throwable t) {Log.e(TAG,"getDatelongitude: " + t.toString(),t);} //Messaggio di errore -> contiene il TAG e il messaggio interessato, in tal caso "Ottiene la longitudine della data: (t)"
        return lon;             //Ritorno stringa "lon"
    }

    /**********_READ_METHODS_**********/                    //Metodi per la gestione della stringa "s" (riga 80 -> formato stringa)
    public String readLatitude (String s) { //Metodo per la lettura della latitudine
        if (s.length() == 0)                           //Se la stringa è vuota
            return "";                                 //ritorno latitudine vuota

        String lat = "";                          //Variabile latitudine
        for (int i = 0; s.charAt(i) != 'A'  &&  i < s.length(); i++) //Per ogni valore di "i", fino a che "i" è minore della lunghezza della stringa "s", e fino a che il carattere è diverso da "A"
            lat += s.charAt(i);                   //Aggiungo a "lat" il carattere "i" della stringa "s"

        return lat;              //Ritorno la latitudine
    }

    public String readLongitude (String s) { //Metodo per la lettura della longitudine
        if (s.length() == 0)                            //Se la stringa è vuota
            return "";                                  //ritorno latitudine vuota

        int start; //Variabile start per controllare e scorrere dall'inizio, carattere per carattere, la stringa (Finchè non leggo il carattere "A" o non supero la lunghezza della stringa)
        for (start = 0; s.charAt(start) != 'A'  &&  start < s.length(); start++) {} start++; //Quindi fin quando è vera la condizione incremento la variabile start

        String lon = "";                          //Variabile longitudine
        for (int i = start; s.charAt(i) != 'O'  &&  i < s.length(); i++) //Per ogni valore di "i", fino a che "i" è minore della lunghezza della stringa "s", e fino a che il carattere è diverso da "O"
            lon += s.charAt(i);                   //Aggiungo a "lon" il carattere "i" della stringa "s"

        return lon;             //Ritorno la longitudine
    }

    public  String readDate (String s) { //Metodo per la lettura della data
        if (s.length() == 0)                            //Se la stringa è vuota
            return "";                                  //ritorno latitudine vuota

        int start; //Variabile start per controllare e scorrere dall'inizio, carattere per carattere, la stringa (Finchè non leggo il carattere "O" o non supero la lunghezza della stringa)
        for (start = 0; s.charAt(start) != 'O'  &&  start < s.length(); start++) {} start++; //Quindi fin quando è vera la condizione incremento la variabile start

        String date = "";                         //Variabile data
        for (int i = start; s.charAt(i) != 'D' &&  i < s.length(); i++) //Per ogni valore di "i", fino a che "i" è minore della lunghezza della stringa "s", e fino a che il carattere è diverso da "D"
            date += s.charAt(i);                  //Aggiungo a "date" il carattere "i" della stringa "s"

        return date;             //Ritorno la data
    }

    public String readTime (String s) { //Metodo per la lettura dell'ora
        if (s.length() == 0)                            //Se la stringa è vuota
            return "";                                  //ritorno latitudine vuota

        int start; //Variabile start per controllare e scorrere dall'inizio, carattere per carattere, la stringa (Finchè non leggo il carattere "D" o non supero la lunghezza della stringa)
        for (start = 0; s.charAt(start) != 'D'  &&  start < s.length(); start++) {} start++; //Quindi fin quando è vera la condizione incremento la variabile start

        String time = "";                         //Variabile ora
        for (int i = start; s.charAt(i) != 'T' &&  i < s.length(); i++) //Per ogni valore di "i", fino a che "i" è minore della lunghezza della stringa "s", e fino a che il carattere è diverso da "T"
            time += s.charAt(i);                  //Aggiungo a "time" il carattere "i" della stringa "s"

        return time;             //Ritorno l'ora
    }

    public String readAccuracy (String s) { //Metodo per la lettura della accuratezza
        if (s.length() == 0)                            //Se la stringa è vuota
            return "";                                  //ritorno latitudine vuota

        int start; //Variabile start per controllare e scorrere dall'inizio, carattere per carattere, la stringa (Finchè non leggo il carattere "D" o non supero la lunghezza della stringa)
        for (start = 0; s.charAt(start) != 'T'  &&  start < s.length(); start++) {} start++; //Quindi fin quando è vera la condizione incremento la variabile start

        String acc = "";                          //Variabile accuratezza
        for (int i = start; i < s.length(); i++)  //Per ogni valore di "i", fino a che "i" è minore della lunghezza della stringa "s"
            acc += s.charAt(i);                   //Aggiungo a "acc" il carattere "i" della stringa "s"

        return acc;             //Ritorno l'accuratezza
    }
}
