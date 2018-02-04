package com.teocri.trainmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;
//Questa è una classe statica che abbiamo usato per tenere ordinate tutte le informazioni e i riferimenti ai
//dati dei treni così che da ogni altra activity sia possibile accedere a tali dati in modo semplice e chiaro.
public class DataHolder {

    public static final int TRAIN_SLOTS = 6;                                //Dichiarazione e inizializzazione slot dei treni

    static TextView[]     trainNumberTW = new TextView[TRAIN_SLOTS];        //trainNumbersTextView - Textview riferita al numero treno
    static TextView[]     trainTimeTW   = new TextView[TRAIN_SLOTS];        //trainTimeTextView - Textview riferita alla situazione treno (in orario, ritardo ..)
    static TextView[]     trainDD       = new TextView[TRAIN_SLOTS];        //trainDepartureDestination - Textview riferita alle indicazioni partenza-destinazione
    static ProgressBar[]  progressBars  = new ProgressBar[TRAIN_SLOTS];     //Dichiarazione e inizializzazione ProgressBar
    static LinearLayout[] linearLayouts = new LinearLayout[TRAIN_SLOTS];    //Dichiarazione e inizializzazione LinearLayout riferiti a ogni blocco (line)
    static Button[]       buttons       = new Button[TRAIN_SLOTS];          //Dichiarazione e inizializzazione bottoni
    static int[]          tStatus       = new int[TRAIN_SLOTS];             //Dichiarazione e inizializzazione train status
    static Boolean[]      pStatus       = new Boolean[TRAIN_SLOTS];         //Dichiarazione e inizializzazione polling status
    static int[]          pTime         = new int[TRAIN_SLOTS];             //Dichiarazione e inizializzazione polling time
    static Context        context;                                          //Dichiarazione e inizializzazione variabile contesto

    public DataHolder(TextView[] tN, TextView[] tT, TextView[] tDD, ProgressBar[] pgBars, LinearLayout[] ll, Button[] btns, Context cntx) {
        trainNumberTW = tN;      //Inizializzazione di tutte le variabili in relazione ai dati che arrivano dal MainActivity
        trainTimeTW = tT;
        trainDD = tDD;
        progressBars = pgBars;
        linearLayouts = ll;
        buttons = btns;
        context = cntx;
        loadAllStatus();         //Richiamo la "loadAllStatus()" per il caricamento degli stati -> stato dei treni, polling dei treni, tempo di polling di ciascuno
        updateAllMainScreen();   //Richiamo la "updateAllMainScreen()" per aggiornare la situazione a video di tutti gli slot in relazione ai nuovi dati
        updateAllButtonsText();  //Richiamo la "updateAllButtonsText()" per aggiornare i bottoni (edit-start)
    }

    /**********_LOAD_METHODS_**********/
    public static void loadAllStatus() {            //Metodo per il caricamento dello stato treni, polling dei treni e tempo di polling
        for (int i = 0; i < tStatus.length; i++)            //Per ogni slot
            load_tStatus(i);                                //eseguo il caricamento dello stato del treno
        for (int i = 0; i < pStatus.length; i++)            //Per ogni slot
            load_pStatus(i);                                //eseguo il caricamento dello stato di polling del treno
        for (int i = 0; i < pTime.length; i++)              //Per ogni slot
            load_pTime(i);                                  //eseguo il caricamento del tempo di polling del treno
    }

    public static void loadStatusIndex(int i) {     //Metodo per il caricamento dello stato di un treno, polling di un treno e tempo di polling del treno stesso
        load_tStatus(i);                                    //Eseguo il caricamento dello stato del treno
        load_pStatus(i);                                    //Eseguo il caricamento dello stato di polling del treno
        load_pTime(i);                                      //Eseguo il caricamento del tempo di polling del treno
    }

    private static void load_tStatus(int i) {       //Metodo per il settaggio dello stato del treno
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        int stat = sharedPreferences.getInt("TrainStatus" + i, -1); //Variabile "stato" settata in relazione a "i"(slot treno) e con -1 (stato treno -> non monitorato/slot vuoto)
        tStatus[i] = stat; //Caricamento stato del treno, passando "stat" allo stato del treno nello slot "i"
    }
    private static void load_pStatus(int i) {       //Metodo per il settaggio dello stato di polling del treno
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        boolean stat = sharedPreferences.getBoolean("PollingStatus" + i, true) ; //Variabile "stato" settata in relazione a "i"(slot treno) e con "true" (polling attivo)
        pStatus[i] = stat; //Caricamento polling del treno, passando "stat" allo stato del polling del treno nello slot "i"
    }
    private static void load_pTime(int i) {         //Metodo per il settaggio del tempo di polling
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        int pT = sharedPreferences.getInt("PollingTime" + i, 60000); //Variabile "pollingTime" settata in relazione a "i"(slot treno) e con 60000(60 secondi tempo polling)
        pTime[i] = pT;    //Caricamento polling time del treno, passando "pT" al tempo di polling del treno nello slot "i"
    }

    /**********_STATUS_SETTERS_**********/
    public static void set_tStatus(int i, int value) {  //Metodo per settare lo stato del treno in relazione all'indice "i" e al "value" dello stato (esempio: stato treno -> -1 -> non monitorato/slot vuoto)
        DataHolder.tStatus[i] = value;      //Tramite questa classe, setto il "tStatus" nell'indice in questione passando il valore "value" passato al metodo
        updateSP_tStatus(i, value);         //Tramite "updateSP_tStatus" aggiorno lo stato del treno in relazione alle modifiche fatte
    }
    private static void updateSP_tStatus(int i, int value) { //Metodo per effettuare le modifiche allo stato del treno (grazie alle shared preferences)
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit(); //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.putInt("TrainStatus" + i, value); //Per mezzo dell'editor effettuo la modifica dello stato del treno nello slot (i) col valore di stato "value"
        editor.apply();                            //Per mezzo dell'editor applico le modifiche
    }

    public static void set_pStatus(int i, boolean value) { //Metodo per settare lo stato di polling del treno in relazione all'indice "i" e al "value" dello stato
        DataHolder.pStatus[i] = value;      //Tramite questa classe, setto il "pStatus" nell'indice in questione passando il valore "value" passato al metodo
        updateSP_pStatus(i, value);         //Tramite "updateSP_pStatus" aggiorno lo stato di polling del treno in relazione alle modifiche fatte
    }
    private static void updateSP_pStatus(int i, boolean value) { //Metodo per effettuare le modifiche allo stato di polling del treno (grazie alle shared preferences)
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit(); //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.putBoolean("PollingStatus" + i, value); //Per mezzo dell'editor effettuo la modifica dello stato di polling del treno nello slot (i) col valore di stato "value"
        editor.apply();                                  //Per mezzo dell'editor applico le modifiche
    }

    public static void set_pTime(int i, int value) { //Metodo per settare il tempo di polling del treno in relazione all'indice "i" e al "value" del tempo
        DataHolder.pTime[i] = value;        //Tramite questa classe, setto il "pStatus" nell'indice in questione passando il valore "value" passato al metodo
        updateSP_pTime(i, value);           //Tramite "updateSP_pTime" aggiorno lo stato del tempo di polling del treno in relazione alle modifiche fatte
    }
    private static void updateSP_pTime(int i, int value) { //Metodo per effettuare le modifiche allo stato di polling del treno (grazie alle shared preferences)
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit(); //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.putInt("PollingTime" + i, value); //Per mezzo dell'editor effettuo la modifica del tempo di polling del treno nello slot (i) col valore di stato "value"
        editor.apply();                            //Per mezzo dell'editor applico le modifiche
    }

    /**********_SET/GET_TRAIN_TO/FROM_SHARED_PREFERENCES_**********/
    public static String getTrainNumber(int ind) {          //Metodo per ottenere il numero del treno, in relazione all'indice "ind"
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        String sNumber = sharedPreferences.getString("TrainNumber" + ind, "Not monitored"); //Ottengo la stringa "s1" del treno "ind" e inserisco il risultato in "sNumber"
        return sNumber;             //Ritorna variabile numero treno
    }

    public static void setTrainNumber(int i, String s) {    //Metodo per settare il numero del treno, in relazione all'indice "ind" e la stringa "s" (contenente il numero del treno)
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        SharedPreferences.Editor editor = sharedPreferences.edit(); //Crea un editor per queste preferenze, attraverso il quale è possibile modificare i dati delle preferenze
        editor.putString("TrainNumber" + i , s);  //Settaggio numero treno di indice "i", inserendo il numero contenuto nella stringa "s"
        editor.apply();                             //Per mezzo dell'editor applico le modifiche
    }

    private static String getTrainSituation(int ind) {      //Metodo per ottenere la situazione del treno, in relazione all'indice "ind"
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        String sit = sharedPreferences.getString("Situation" + ind, ""); //Ottengo la stringa "s1" della situazione "ind" e inserisco il risultato in "sit"
        return sit;                 //Ritorna variabile situazione treno
    }

    private static String getTrainJourney(int ind) {        //Metodo per ottenere i dettagli del viaggio del treno, in relazione all'indice "ind"
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE); //Recupera e mantieni il contenuto del file delle preferenze 'train_data', restituendo un oggetto SharedPreferences attraverso il quale è possibile recuperare e modificare valori.
        String jou = sharedPreferences.getString("Journey" + ind, ""); //Ottengo la stringa "s1" del viaggio "ind" e inserisco il risultato in "jou"
        return jou;                 //Ritorna variabile viaggio treno
    }

    /**********_UPDATES_**********/
    public static void updateAllMainScreen(){           //Metodo per aggiornare a video tutti gli slot
        for (int i = 0; i < TRAIN_SLOTS; i++) {                 //Per ogni slot i,
            updateMainScreen(i);                                //richiamo il metodo di aggiornamento dello slot
        }
    }

    public static void updateMainScreen(int i){         //Metodo per aggiornare a video lo slot i
        loadStatusIndex(i); //Per il caricamento dello stato di un treno, polling di un treno e tempo di polling del treno stesso
        switch (tStatus[i]) {     //In relazione allo stato del treno
            case 0:                                  //Se caso "0"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_0)));       //Settaggio colore del testo del numero treno (72014a-viola scuro)
                trainTimeTW[i]  .setText(R.string.mainscreen_unknown_delay);                               //Settaggio testo situazione treno a "Sconosciuto"
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_0)));       //Settaggio colore del testo situazione treno (72014a-viola scuro)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_0)));       //Settaggio colore "partenza-destinazione" del treno (72014a-viola scuro)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_0_layout)));       //Settaggio colore background dello slot
                break;                                                                                                  //case_0_layout (ff49fb - fucsia chiaro)
            case 1:                                  //Se caso "1"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_1)));       //Settaggio colore del testo del numero treno (000000-nero)
                trainTimeTW[i]  .setText(getTrainSituation(i));                                            //Settaggio testo situazione treno a "Non in viaggio"
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_1)));       //Settaggio colore del testo situazione treno (000000-nero)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_1)));       //Settaggio colore "partenza-destinazione" del treno (000000-nero)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_1_layout)));       //Settaggio colore background dello slot
                break;                                                                                                  //case_1_layout (808080 - grigio)
            case 2:                                  //Se caso "2"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_2)));       //Settaggio colore del testo del numero treno (006600-verde)
                trainTimeTW[i]  .setText(getTrainSituation(i));                                            //Settaggio testo situazione treno a "In tempo"
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_2)));       //Settaggio colore del testo situazione treno (006600-verde)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_2)));       //Settaggio colore "partenza-destinazione" del treno (006600-verde)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_2_layout)));       //Settaggio colore background dello slot
                break;                                                                                                  //case_2_layout (00cd00 - fucsia)
            case 3:                                  //Se caso "3"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_3)));       //Settaggio colore del testo del numero treno (722d00-marrone scuro)
                trainTimeTW[i]  .setText(getTrainSituation(i));                                            //Settaggio testo situazione treno a "minuti ritardo" (<10)
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_3)));       //Settaggio colore del testo situazione treno (722d00-marrone scuro)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_3)));       //Settaggio colore "partenza-destinazione" del treno (722d00-marrone scuro)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_3_layout)));       //Settaggio colore background dello slot
                break;                                                                                                  //case_3_layout (00cd00 - fucsia)
            case 4:                                  //Se caso "4"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_4)));       //Settaggio colore del testo del numero treno (dd0000-rosso scuro)
                trainTimeTW[i]  .setText(getTrainSituation(i));                                            //Settaggio testo situazione treno a "minuti ritardo" (>10)
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_4)));       //Settaggio colore del testo situazione treno (dd0000-rosso scuro)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_4)));       //Settaggio colore "partenza-destinazione" del treno (dd0000-rosso scuro)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_4_layout)));       //Settaggio colore background dello slot
                break;                                                                                                  //case_4_layout (ff8080 - rosso chiaro)
            case 6:                                  //Se caso "6"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_6)));       //Settaggio colore del testo del numero treno (ee0000-rosso)
                trainTimeTW[i]  .setText(getTrainSituation(i));                                            //Settaggio testo situazione treno a "arrivato"
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_6)));       //Settaggio colore del testo situazione treno (ee0000-rosso)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_6)));       //Settaggio colore "partenza-destinazione" del treno (ee0000-rosso)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_6_layout)));       //Settaggio colore background dello slot
                break;                                                                                                  //case_6_layout (ffdfdf - rosa)
            case 8:                                  //Se caso "8"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_8)));       //Settaggio colore del testo del numero treno (0000ff-blu)
                trainTimeTW[i]  .setText(R.string.mainscreen_updating);                                    //Settaggio testo situazione treno a "aggiornamento dati"
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_8)));       //Settaggio colore del testo situazione treno (0000ff-blu)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_8)));       //Settaggio colore "partenza-destinazione" del treno (0000ff-blu)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_8_layout)));       //Settaggio colore background dello slot
                break;                                                                                                  //case_8_layout (adadff - azzurro)
            case 99:                                 //Se caso "99"
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));  //Settaggio testo del numero treno a "train: (numero)"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_99)));      //Settaggio colore del testo del numero treno (eeeeff-grigio chiaro)
                trainTimeTW[i]  .setText(getTrainSituation(i));                                            //Settaggio testo situazione treno a "non esiste"
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_99)));      //Settaggio colore del testo situazione treno (eeeeff-grigio chiaro)
                trainDD[i]      .setText("");                                                              //Settaggio testo "partenza-destinazione" del treno (caso vuoto)
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_99_layout)));      //Settaggio colore background dello slot
                break;                                                                                                  //case_99_layout (aa22ff - viola)
            case -1:                                 //Se caso "-1" (caso default)
            default:
                trainNumberTW[i].setText(R.string.mainscreen_empty_slot);                                  //Settaggio testo del numero treno a "slot vuoto"
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_default))); //Settaggio colore del testo del numero treno (111111-nero)
                trainTimeTW[i]  .setText(R.string.mainscreen_not_monitored);                               //Settaggio testo situazione treno a "non monitorato"
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_default))); //Settaggio colore del testo situazione treno (111111-nero)
                trainDD[i]      .setText(getTrainJourney(i));                                              //Settaggio testo "partenza-destinazione" del treno
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_default_layout))); //Settaggio colore background dello slot
        }                                                                                                               //case_default_layout (ffc65c - arancione chiaro)
        updateButton(i);    //Aggiorno bottone dello slot i
    }

    public static void updateButton(int i) {        //Metodo per aggiornare bottone di una singola riga
        if (pStatus[i])                                                //Se stato di polling dello slot i è attivo (true)
            buttons[i].setText(R.string.mainscreen_button_stop);                                //Settaggio testo del bottone a "stop"
        else                                                           //Altrimenti
            buttons[i].setText(R.string.mainscreen_button_start);                               //Settaggio testo del bottone a "start"
    }

    public static void updateAllButtonsText() {     //Metodo per l'aggiornamento dei bottoni
        for (int i = 0; i < TRAIN_SLOTS; i++){ updateButton(i); }       //Aggiorno il bottone di ogni slot i
    }

    /**********_RESETS_**********/
    public static void resetData() {                //Metodo di reset dei dati di tutti gli slot
        resetAll_tStatus();             //Richiamo la "resetAll_tStatus()" per resettare lo stato di tutti i treni
        resetAll_pStatus();             //Richiamo la "resetAll_pStatus()" per resettare lo stato di polling di tutti i treni
        resetAllProgressBars();         //Richiamo la "resetAll_ProgressBars()" per resettare lo stato di tutte le progressBar
        updateAllMainScreen();          //Aggiorno la situazione a video di tutti gli slot in relazione ai nuovi dati (dati di default - slot vuoti)
    }

    public static void resetDataIndex(int i) {      //Metodo di reset dei dati dello slot i
        reset_tStatus(i);                              //Resetto lo stato del treno nello slot i
        reset_pStatus(i);                              //Resetto lo stato di polling del treno nello slot i
        reset_ProgressBar(i);                          //Resetto la progressBar del treno nello slot i
        DataReader.writeJourney(i, "");         //Tramite la funzione "writeJourney" del DataReader scrivo il viaggio "" (vuoto) nello slot i
        updateMainScreen(i);                           //Aggiorno la situazione a video del treno nello slot i (in relazione alle nuove informazioni)
    }

    private static void resetAll_tStatus() { for (int i = 0; i < tStatus.length; i++) reset_tStatus(i); } //Metodo per resettare tutti gli stati dei treni
    private static void reset_tStatus(int i) { set_tStatus(i, -1); }    //Metodo per resettare lo stato di un treno, di quello in posizione i

    private static void resetAll_pStatus() { for (int i = 0; i < pStatus.length; i++) reset_tStatus(i); } //Metodo per resettare tutti gli stati di polling dei treni
    private static void reset_pStatus(int i) { set_pStatus(i, true); }  //Metodo per resettare lo stato di polling di un treno, di quello in posizione i

    private static void resetAllProgressBars() { for (int i = 0; i < TRAIN_SLOTS; i++) reset_ProgressBar(i); } //Metodo per resettare tutte le progressBar
    private static void reset_ProgressBar(int i) { DataHolder.progressBars[i].setProgress(0); }   //Metodo per resettare una progressBar, quella in posizione i

}

/***********_STATUS_VALUES_***********/
//-1 not monitored / empty slot
// 0 monitored but polling "stop"
// 1 not travelling
// 2 on time
// 3 less than 10 minutes late
// 4 more than 10 minutes late
// 6 arrived
// 8 updating
// 99 cant define train status
