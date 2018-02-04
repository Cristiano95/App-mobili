package com.teocri.trainmonitor;

import android.os.AsyncTask;

import java.io.IOException;
//Questa classe estende la classe AsyncTask e serve per effettuare la connessione
// tramite il DataReader e gestire l’aggiornamento dei dati su monitor ogni tot polling time scelto dall’utente.
public class ThreadUnit extends AsyncTask<Void, Integer, Void> {

    int ind;    //Dichiarazione variabile indice per identificare la posizione di ogni thread
    int j;      //Dichiarazione variabile indice per gestione del polling

    public ThreadUnit(int i) {  //Per il thread in posizione i
        ind = i;                //Setto l'indice "ind" in relazione all'indice del thread stesso (i)
        j = 0;                  //Setto l'indice "j" per gestione del polling
    }

    @Override
    protected void onPreExecute() {                            //Metodo effettuato prima dell'effettiva esecuzione
        DataHolder.progressBars[ind].setProgress(0);                    //Settaggio progressBar, di indice ind, a 0
        DataHolder.progressBars[ind].setMax(DataHolder.pTime[ind]);     //Settaggio massimo progressBar, di indice ind, in relazione al tempo di polling scelto
    }

    @Override
    protected Void doInBackground(Void... voids) {      //Metodo che invoca i thread in background immediatamente dopo l'esecuzione di onPreExecute()
                                                        //Questo metodo viene utilizzato per eseguire calcoli di background che possono richiedere molto tempo
        try {
            DataReader.connect(ind, DataHolder.getTrainNumber(ind)); //Per mezzo del "DataReader" eseguo la connessione passando indice e numero treno(calcolabile con la "getTrainNumber" in base all'indice interessato)
        } catch (IOException e) {
            e.printStackTrace();
        }
        for ( j = 0; j < DataHolder.pTime[ind] && !isCancelled(); j += 1000) { //Per ogni valore di j, ogni 1000(millisecondi), controllo se l'indice "j" è minore del pollingTime nell'indice interessato e controllo se il thread non è cancellato
            publishProgress(j);                    //Serve per pubblicare un'unità di progresso. Questi valori sono pubblicati sul thread dell'interfaccia utente, in onProgressUpdate
            try {
                Thread.sleep(1000);           //Fa in modo che il thread attualmente in esecuzione si interrompa (interrompendo temporaneamente l'esecuzione) per un secondo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!isCancelled()) {           //Se non è stato cancellato
            publishProgress(j + 1000);          //Serve per pubblicare un'unità di progresso in relazione all'indice j più un secondo.
            try {
                DataReader.setJourney(ind);            //Tramite il "DataReader" setto il viaggio in relazione all'indice
                DataReader.setSituation(ind);          //Tramite il "DataReader" setto la situazione in relazione all'indice
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {    //Metodo effettuato durante l'effettiva esecuzione, per gli aggiornamenti
        DataHolder.progressBars[ind].setProgress(progress[0]);      //Per mezzo del DataHolder, settiamo i progressi della progressBar durante l'esecuzione
    }

    @Override
    protected void onPostExecute(Void aVoid) {                //Metodo effettuato dopo l'effettiva esecuzione
        DataHolder.updateMainScreen(ind);                           //Aggiorno la situazione a video dello slot ind in relazione ai nuovi dati
        ThreadHive.restartUnit(ind);                                //Tramite la classe ThreadHive faccio la "restartUnit" del thread in posizione ind
    }
                                                              //Metodo per cancellazione, quindi settaggio della progressBar, di indice ind, a 0
    @Override                                                 //dopo doInBackground()
    protected void onCancelled() {
        DataHolder.progressBars[this.ind].setProgress(0);
    }

}