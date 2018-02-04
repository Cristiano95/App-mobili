package com.teocri.trainmonitor;


import android.content.SharedPreferences;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class DataReader {                          //Questa classe contiene i metodi per connessione e lettura dei dati dalla pagina HTML
                                                   // da cui vengono presele informazioni sui treni.

    static URL               url;                           //Variabile url che indica il link dal quale leggere i dati
    static HttpURLConnection hUC;                           //Variabile di tipo "HttpURLConnection" utilizzata per effettuare la connessione
    static InputStream       inStream;                      //Variabile di tipo "InputStream" per gestione del flusso dati in input
    static BufferedReader[]  br = new BufferedReader[6];    //Variabile di tipo "BufferedReader[]" per lettura e memorizzazione caratteri
    static Boolean           trainExistence = true;         //Variabile booleana per indicare l'esistenza o meno del treno


    public DataReader() {}

    /**********_SETTERS/GETTERS_**********/
    public static void setJourney(int ind) throws IOException {          //Metodo per settare partenza e destinazione del treno interessato
        String journey = "";                                                            //Dichiarazione e inizializzazione stringa "viaggio"
        trainExistence = true;                                                          //Variabile "treno esistente" true

        if (readLinesTo(ind, "<!-- ORIGINE -->") == 0) { return; } skipLine(ind);    //Se non trovo la linea con scritto "<!-- ORIGINE -->" eseguo la return, se no vado avanti e leggo la linea successiva skipLine(ind)
        if (readCharsTo(ind, '>') == 0) { return; }                                  //Leggo fino al ">" e se non trovo il carattere ">" eseguo la return, se no vado avanti

        String departure = copyCharsTo(ind, "</h2>");                  //Stinga che identifica la partenza -> effettuo la lettura da dove sono fino a "</h2>"
        if (departure.length() == 0){                                                   //Se la lunghezza della stringa partenza è 0
            trainExistence = false;                                                     //Il treno non esiste -> variabile booleana "trainExistence" falsa
            return;                                                                     //e return
        }

        if (readLinesTo(ind, "<!-- DESTINAZIONE -->") == 0) { return; } skipLine(ind);//Se non trovo la linea con scritto "<!-- DESTINAZIONE -->" eseguo la return, se no vado avanti e leggo la linea successiva skipLine(ind)
        if (readCharsTo(ind, '>') == 0) { return; }                                   //Leggo fino al ">" e se non trovo il carattere ">" eseguo la return, se no vado avanti

        String destination = copyCharsTo(ind, "</h2>");                //Stinga che identifica la destinazione -> effettuo la lettura da dove sono fino a "</h2>"
        if (destination.length() == 0){                                                 //Se la lunghezza della stringa destinazione è 0
            trainExistence = false;                                                     //Il treno non esiste -> variabile booleana "trainExistence" falsa
            return;                                                                     //e return
        }

        journey = departure + "\n" + destination;     //Variabile viaggio -> partenza e destinazione
        writeJourney(ind, journey);                   //Scrivo il viaggio grazie alla funzione "writeJourney", passandogli indice e variabile viaggio
    }

    public static void setSituation(int ind) throws IOException {                  //Metodo per settare la situazione del treno interessato
        if (readLinesTo(ind, "<!-- SITUAZIONE -->") == 0) {  //Se non trovo la linea con scritto "<!-- SITUAZIONE -->"
            br[ind].close();                                   //Chiusura bufferReader di quell'indice e return
            return;
        }                                                      //Altrimenti vado avanti e
        String tmp_sit = "";                           //Stringa temporanea situazione
        String ret_sit = "";                           //Stringa di return situazione
        tmp_sit = copyLinesTo(ind, "<br>");                 //Stringa temporanea -> copio fino a che non trovo "<br>"
        ret_sit = analiseSituation(ind, tmp_sit);              //Stringa di return -> uso la "analiseSituation" per arrivare ad avere la situazione in relazione alla stringa temporanea interessata
        writeSituation(ind, ret_sit);                          //Scrivo la situazione grazie alla funzione "writeSituation", passandogli indice e "Stringa di return"
        br[ind].close();                                       //Chiusura bufferReader di quell'indice
    }

    private static String analiseSituation(int ind, String sit) {//Metodo per analizzare la situazione interessata (indice ind) e ritornare lo stato del treno
        String tmp = sit;                //Variabile stringa temporanea uguale alla situazione interessata che passo al metodo

        if (trainExistence == false){                   //Se il treno non esiste
            DataHolder.set_tStatus(ind, 99);      //Setto, tramite il DataHolder, lo stato del treno al caso 99 -> "il treno non esiste"
            return String.valueOf(DataHolder.context.getText(R.string.dont_exist));    //e ritorno la stringa "non esiste" (values/strings.xml)
        }

        if (tmp.contains("arrived")) {                  //Se la variabile temporanea contiene "arrivato"
            DataHolder.set_tStatus(ind, 6);       //Setto, tramite il DataHolder, lo stato del treno al caso 6 -> "il treno è arrivato"
            return String.valueOf(DataHolder.context.getText(R.string.arrived));       //e ritorno la stringa "arrivato" (values/strings.xml)
        }

        if (tmp.contains("not left")) {                 //Se la variabile temporanea contiene "non sta viaggiando"
            DataHolder.set_tStatus(ind, 1);       //Setto, tramite il DataHolder, lo stato del treno al caso 1 -> "il treno non sta viaggiando"
            return String.valueOf(DataHolder.context.getText(R.string.not_travelling));   //e ritorno la stringa "non sta viaggiando" (values/strings.xml)
        }

        if (tmp.contains("on time")) {                 //Se la variabile temporanea contiene "in orario"
            DataHolder.set_tStatus(ind, 2);      //Setto, tramite il DataHolder, lo stato del treno al caso 2 -> "il treno è in orario"
            return String.valueOf(DataHolder.context.getText(R.string.on_time));          //e ritorno la stringa "in orario" (values/strings.xml)
        }

        if (tmp.contains("delay")){                    //Se la variabile temporanea contiene "ritardo"
            int i = Integer.parseInt(tmp.replaceAll("\\D", ""));    //Variabile i è uguale al contenuto della variabile temporanea (mettendo "vuoto" al posto di tutti elementi della punteggiatura non essenziali)
            if (i < 10)                                         //Se il valore di i (ritardo) < 10
                DataHolder.set_tStatus(ind, 3);           //Setto, tramite il DataHolder, lo stato del treno al caso 3 -> "ritardo minore di 10 minuti"
            else                                               //Altrimenti
                DataHolder.set_tStatus(ind, 4);          //Setto, tramite il DataHolder, lo stato del treno al caso 4 -> "ritardo maggiore di 10 minuti"
            return i + String.valueOf(DataHolder.context.getText(R.string.mintes_delay));                             //Ritorno il numero di minuti e messaggio "minuti di ritardo"
        }

        DataHolder.set_tStatus(ind, 99);         //Setto, tramite il DataHolder, lo stato del treno al caso 99 -> "il treno non esiste"
        return String.valueOf(DataHolder.context.getText(R.string.not_found));           //e ritorno la stringa "non esiste" (values/strings.xml)
    }

    public static void writeSituation(int ind, String sit) {        //Metodo per settaggio viaggio del treno, in relazione all'indice "ind"
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit();   //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.putString("Situation" + ind , sit);      //Settaggio situazione treno di indice "ind", inserendo la situazione contenuta nella stringa "sit"
        editor.apply();                                   //Per mezzo dell'editor applico le modifiche
    }

    public static void resetSituation(int ind) {      //Metodo per resettare la situazione del treno, in relazione all'indice "ind"
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit();    //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.remove("Situation" + ind).commit();    //Tramite l'editor effettuo la rimozione della situazione "ind"
    }                                                 //commit -> configura preferenze modificate da questo Editor, all'oggetto SharedPreferences che sta modificando.

    public static void writeJourney(int ind, String journey) {      //Metodo per settaggio viaggio del treno, in relazione all'indice "ind"
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit();    //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.putString("Journey" + ind , journey);      //Settaggio viaggio treno di indice "ind", inserendo il viaggio contenuto nella stringa "journey"
        editor.apply();                                     //Per mezzo dell'editor applico le modifiche
    }

    public static void resetJourney(int ind) {      //Metodo per resettare il viaggio del treno, in relazione all'indice "ind"
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit();   //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.remove("Journey" + ind).commit();    //Tramite l'editor effettuo la rimozione del viaggio "ind"
    }                                               //commit -> configura preferenze modificate da questo Editor, all'oggetto SharedPreferences che sta modificando.

    /**********_CONNECTION_**********/
    public static void connect(int ind, String tNumb) throws IOException {
        url = new URL("http://mobile.viaggiatreno.it/vt_pax_internet/mobile/numero?lang=EN&numeroTreno=" + tNumb); //Url dal quale leggere i dati (tNumb -> numero del treno)
        hUC = (HttpURLConnection) url.openConnection(); //Effettua la connessione il relazione all'url completo (link con incluso il numero treno inserito)
        inStream = new BufferedInputStream(hUC.getInputStream()); //"BufferedInputStream" gestisce il flusso di input -> capacità di bufferizzare l'input(dati di hUC). Appena creato, viene creato un array di buffer interno.
        br[ind] = new BufferedReader(new InputStreamReader(inStream)); //Effettua la lettura da un flusso di input(inStream), memorizzando i caratteri in modo da fornire una lettura efficiente. (Nell'indice interessato)
    }

    /**********_UTILITIES_**********/
    private static void skipLine(int ind) throws IOException { br[ind].readLine(); } //Metodo per leggere la linea interessata (indice ind del buffer reader)

    private static int readLinesTo(int ind, String s) throws IOException {//Metodo per leggere linea per linea fino ad arrivare ad una determinata stringa s ricercata in input
        String sRead = "";                  //Stringa "sRead" utilizzata per il confronto, per effettuare i vari controlli di ricerca della stringa
        while (!sRead.contains(s)) {                    //Fin quando "sRead" non contiene la stringa "s" data in input
            sRead = br[ind].readLine();                 //La stringa "sRead" la pongo uguale alla riga letta nell'indice ind del buffer reader
            if (sRead == null){                         //Se sRead è una stringa nulla
                return 0;                               //Ritorno 0
            }
        }return 1;                                      //Nel caso in cui esco dal ciclo ritorno 1, quindi ho trovato la stringa
        //1 = found
        //0 = EOF
    }

    private static int readCharsTo(int ind, Character c) throws IOException {//Metodo per leggere carattere per carattere fino ad arrivare ad un determinato carattere c ricercato in input
        Character cRead = '#';              //Carattere "cRead" utilizzato per il confronto, per effettuare i vari controlli di ricerca del carattere
        while (cRead != c) {                            //Fin quando "cRead" non contiene il carattere "c" dato in input
            cRead = (char)br[ind].read();               //Il carattere "cRead" lo pongo uguale al carattere letto nell'indice ind del buffer reader
            if (cRead == null){                         //Se cRead è un carattere nullo
                return 0;                               //Ritorno 0
            }
        }return 1;                                      //Nel caso in cui esco dal ciclo ritorno 1, quindi ho trovato il carattere
        //1 = found
        //0 = EOF
    }

    private static String copyLinesTo(int ind, String s) throws IOException {//Metodo per effettuare la lettura e copia fino ad una determinata stringa in input
        String sRead = "";                  //Stringa "sRead" come valore di ritorno
        String sup = "";                    //Stringa "sup" come valore per effettuare ogni controllo
        while (!sup.contains(s) && !sup.equals(null) ) {    //Fin quando stringa "sup" non contiene la stringa "s" in input e fin quando "sup" non è nulla
            if (sup.length() > 0)                           //Controllo se la lunghezza di "sup" è maggiore di 0
                sRead += sup;                               //In tal caso aggiungo stringa "sup" a "sRead"
            sup = br[ind].readLine();                       //Copio nella stringa "sup" la linea interessata nell'indice ind del buffer reader
        }
        return sRead;                       //Al termine ritorno la stringa sRead contenente le linee, fino alla stringa s (se presente)
    }

    private static String copyCharsTo(int ind, String s) throws IOException {//Metodo per effettuare la lettura e copia fino ad un determinato carattere in input
        String cRead = "";                  //Stringa "cRead" come valore di ritorno
        String sup = "";                    //Stringa "sup" come valore per effettuare ogni controllo
        while (!sup.contains(s) && !sup.equals(null) ) {    //Fin quando stringa "sup" non contiene la stringa "s" in input e fin quando "sup" non è nulla
            if (sup.length() > 0)                           //Controllo se la lunghezza di "sup" è maggiore di 0
                cRead = sup;                                //In tal caso copio in "cRead" la stringa "sup"
            sup += (char)br[ind].read();                    //Sommo alla stringa "sup" il carattere interessato nell'indice ind del buffer reader
        }
        return cRead.substring(0, cRead.length() - (s.length() -1));    //Al termine ritorno la stringa "cRead" contenente la stringa (partenza/destinazione se presente)
    }
}
