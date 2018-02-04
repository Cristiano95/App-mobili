package com.teocri.trainmonitor;


import android.os.AsyncTask;
import android.widget.Toast;

public class ThreadHive {                       //Questa è la classe per creare, eliminare, eseguire, restartare e fermare i ThreadUnit.

    static ThreadUnit[] threadUnits;                                    //Dichiarazione vettore di thread (threadUnits), di tipo ThreadUnit

    public ThreadHive(int hiveSize) {                                   //Metodo per gestione struttura dei Thread e richiamo metodo creazione
        threadUnits = new ThreadUnit[hiveSize];                                 //Inizializzazione vettore di thread (hiveSize -> numero thread)
        buildUnits(hiveSize);                                                   //Richiamo la "buildUnits" per creare numero "hiveSize" di thread
    }
                                                                        //Metodo per la creazione di tutti i thread
    public static void buildUnits(int hiveSize) { for (int i = 0; i < hiveSize; i++) { createUnit(i); } }

    public static void createUnit(int ind) {                            //Metodo per la creazione del thread (thread nell'indice ind)
        if(threadUnits[ind] == null)                                            //Se il thread di indice ind è nullo
            threadUnits[ind] = new ThreadUnit(ind);                             //Creo il thread nell'indice ind
        else                                                                    //Altrimenti
            Toast.makeText(DataHolder.context, R.string.toast_unit + ind + R.string.toast_in_hive_yet, Toast.LENGTH_SHORT).show();
    }                                                                           //Stampo a video messaggio di errore "thread già esistente"

    public static void executeUnit(int ind) {                           //Metodo per l'esecuzione del thread (thread nell'indice ind)
        if(threadUnits[ind] != null)                                            //Se il thread di indice ind non è nullo
            threadUnits[ind].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); //Eseguo il thread nell'indice ind
        else                                                                    //Altrimenti
            Toast.makeText(DataHolder.context, R.string.toast_unit + ind + R.string.toast_cannot_be_execute, Toast.LENGTH_SHORT).show();
    }                                                                           //Stampo a video messaggio di errore "non è possibile eseguire il thread"

    public static void restartUnit(int ind) {                           //Metodo per restartare il thread (thread nell'indice ind)
        killUnit(ind);                                                          //"Distruggo" il thread in posizione ind
        executeUnit(ind);                                                       //"Eseguo" il thread in posizione ind
    }

    public static void killUnit(int ind) {                              //Metodo per distruggere il thread (thread nell'indice ind)
        if (threadUnits[ind] != null) {                                         //Se il thread di indice ind non è nullo
            threadUnits[ind].cancel(true);                    //Cancello l'esecuzione del thread
            threadUnits[ind] = null;                                            //Settare il thread a valore nullo
            createUnit(ind);                                                    //Creo il thread nell'indice ind
        }else                                                                   //Altrimenti
            Toast.makeText(DataHolder.context, R.string.toast_unit + ind + R.string.toast_dead_yet, Toast.LENGTH_SHORT).show(); //Stampo messaggio "thread già distrutto"
    }

    public static void DestroyHive() {                                  //Metodo per distruggere tutti i thread
        for (int i = 0; i < threadUnits.length; i++) { killUnit(i); }
    }
}
