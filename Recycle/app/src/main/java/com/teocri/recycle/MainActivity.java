package com.teocri.recycle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
//Il MainActivity conterrà semplicemente i metodi dei bottoni e il SectionsPagerAdapter per la gestione del viewPager grazie al quale si può fare swipe tra le 2 pagine.
public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter; //Dichiarazione variabile di tipo "SectionsPagerAdapter"
    private ViewPager mViewPager;                       //Dichiarazione variabile di tipo "ViewPager"

    InputStream ins;            //Variabile di tipo "InputStream" per la gestione della lettura
    static ExcelReader eReader; //Variabile statica per la lettura del file Excel elenco.xlsx (della cartella raw)

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //onCreate() -> per inizializzare l'activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                 //Tramite setContentView collego all'activity il file xml relativo

        getSupportActionBar().setDisplayShowHomeEnabled(true);  //Settaggio per includere l'accessibilità alla home nella barra delle azioni.
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);    //Settaggio icona dell'applicazione

        //Inizializzazione "mSectionsPagerAdapter" per la gestione del "ViewPager"
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);  //All'interno del relativo file xml linkato(layout/activity_main.xml), ricerco la view da uilizzare in base all'id
        mViewPager.setAdapter(mSectionsPagerAdapter);           //Settaggio del mViewPager per mezzo del "mSectionsPagerAdapter"
                                                                //mViewPager -> grazie al quale si può fare swipe tra le 2 pagine

        ins = getResources().openRawResource(getResources().getIdentifier("elenco", "raw", getPackageName()));
        try {                                //ins -> variabile InputStream nel quale viene inserito il file elenco.xlsx (della cartella raw)
            eReader = new ExcelReader(ins);  //Lettura del file Excel -> effettuata tramite la classe "ExcelReader" su InputStream "ins" contenente il file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //on Methods
    @Override             //Metodo per tornare indietro cliccando il tasto "indietro", che porta all'activity precedente
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override             //Metodo per quando l'activity entra nello stato "Resumed". Si trova in primo piano e quindi il sistema richiama il callback onResume().
    protected void onResume() { super.onResume(); }
                          //viene chiamato ogni volta che l'activity entra in primo piano, anche quando viene creata per la prima volta.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//Creazione menù
        MenuInflater inflater = getMenuInflater();           //Variabile inflater di tipo MenuInflater per la gestione del menù
        inflater.inflate(R.menu.main_menu, menu);            //Funzione inflate per la creazione di menù, gestione parte grafica menù -> menu/main_menu.xml
        return super.onCreateOptionsMenu(menu);              //true -> crea menù - false -> non crea menù/errore
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Selezionando il menù
        switch (item.getItemId()) {         //In relazione all'id di "item"
            case R.id.item_fragment_1:                  //Caso primo fragment
                mViewPager.setCurrentItem(0);           //Imposto l'elemento corrente riferito a pagina 0(Imposta la pagina attualmente selezionata)
                return true;                            //Ritorno true

            case R.id.item_fragment_2:                  //Caso secondo fragment
                mViewPager.setCurrentItem(1);           //Imposto l'elemento corrente riferito a pagina 1(Imposta la pagina attualmente selezionata)
                return true;                            //Ritorno true

            default:
                return super.onOptionsItemSelected(item);
        }//true -> mostra menù - false -> non mostrare menù | Per prima cosa viene sempre istanziato un oggetto della classe madre; ma per istanziare un oggetto della classe madre devo passare degli argomenti al costruttore, allora sarà la classe figlia che si prenderà in carico l'onere di passarglieli.
    }

    //PageAdapter
    private class SectionsPagerAdapter extends FragmentPagerAdapter {       //Classe "SectionsPagerAdapter" per la gestione del "viewPager"
        //Restituisce la classe che rappresenta la superclasse dell'entità rappresentata da questa classe.
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//Metodo per sapere in quale "fragment" stiamo per effettuare l'esecuzione
            if (position == 0)                        //Se la posizione è 0
                return Fragment_cities.newInstance(); //Siamo sul primo fragment(quello della gestione città) e richiamo il costruttore
            return Fragment_garbage.newInstance();    //In caso contrario siamo sul secondo fragment(quello della gestione rifiuti) e richiamo il costruttore
        }

        @Override
        public int getCount() {         //Metodo per ottenere il conteggio delle pagine che è possibile "swappare"
            // Show 2 total pages.
            return 2;                   //Numero pagine
        }

        @Override
        public CharSequence getPageTitle(int position) {//Metodo per ottenere il "titolo della pagina" -> di tipo "CharSequence" = sequenza leggibile di valori char.
            switch (position) {             //In relazione alla posizione,
                case 0:                             //Se siamo nel caso 0
                    return "SECTION 1";             //Siamo nella prima sezione (città)
                case 1:                             //Se siamo nel caso 1
                    return "SECTION 2";             //Siamo nella seconda sezione (rifiuti)
            }
            return null;                    //Altrimenti valore nullo di return
        }
    }

    //Button
    public void getSchedule(View view) {//Metodo per la gestione e stampa dei valori(in relazione alla città inserita) letti dal file elenco.xlsx
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); // Trova la vista focalizzata, in modo da prendere il token della finestra
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //Nasconde la tastiera virtuale utilizzando InputMethodManager, chiamando hideSoftInputFromWindow, passando il token della finestra che contiene la vista focalizzata.

        TextView textCity = (TextView) findViewById(R.id.textCity);         //textCity - Textview riferita alla città
        TextView textDetails = (TextView) findViewById(R.id.detailsCity);   //textDetails - Textview riferita ai dettagli della città
        //All'interno del relativo file xml linkato(layout/fragment_cities.xml), ricerco la view da uilizzare in base all'id
        textDetails.setText("");        //Settaggio testo dettagli città a "vuoto"

        if (textCity.getText().length() == 0) {     //Se la lunghezza del testo della città è uguale a 0
            Toast.makeText(this, R.string.textView_city_empty, Toast.LENGTH_SHORT).show();
            return;                                 //Stampo tramite Toast il messaggio "textview, riferito alla città, vuota" e faccio la return
        }                                           //In caso contrario procedo con il metodo

        String city = String.valueOf(textCity.getText());           //"city" settata in base al testo inserito nella textview "textCity"
        String[] s = eReader.readSchedulePerCity(1, city); //"s" contiene il risultato della lettura del file tramite la classe "eReader"
                                                                    //per mezzo della funzione "readSchedulePerCity" e dando in input città e numero pagina

        if (s[0] == null)//Se il vettore di stringhe "s[]" è vuoto(ricerca senza risultati) stampo tramite Toast il messaggio "Città non disponibile"
            Toast.makeText(this, R.string.city_not_available, Toast.LENGTH_LONG).show();
        else             //Altrimenti
            textDetails.setText(city + "\n\n" +
                "- Raccolta umido: "           + s[0] + "\n" +
                "- Raccolta indifferenziata: " + s[1] + "\n" +
                "- Raccolta ingombranti: "     + s[2]); //Settaggio testo della "textDetails" in base ai risultati del vettore di stringhe s[]
        textCity.setText("");           //Settaggio testo città a "vuoto"
    }

    public void getRecycleBin(View view) {//Metodo per la gestione e stampa dei valori(in relazione al rifiuto inserito) letti dal file elenco.xlsx
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); // Trova la vista focalizzata, in modo da prendere il token della finestra
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //Nasconde la tastiera virtuale utilizzando InputMethodManager, chiamando hideSoftInputFromWindow, passando il token della finestra che contiene la vista focalizzata.

        TextView textGarbage = (TextView) findViewById(R.id.textGarbage);       //textCity - Textview riferita al rifiuto
        TextView textDetails = (TextView) findViewById(R.id.detailsGarbage);    //textDetails - Textview riferita ai dettagli del rifiuto
        //All'interno del relativo file xml linkato(layout/fragment_garbage.xml), ricerco la view da uilizzare in base all'id
        textDetails.setText("");        //Settaggio testo dettagli rifiuto a "vuoto"

        if (textGarbage.getText().length() == 0) {    //Se la lunghezza del testo del rifiuto è uguale a 0
            Toast.makeText(this, R.string.textView_garbage_empty, Toast.LENGTH_SHORT).show();
            return;                                   //Stampo tramite Toast il messaggio "textview, riferita al rifiuto, vuota" e faccio la return
        }

        String garbage = String.valueOf(textGarbage.getText());   //"garbage" settata in base al testo inserito nella textview "textGarbage"
        String s = eReader.readRecycleBin(0, garbage);   //"s" contiene il risultato della lettura del file tramite la classe "eReader"
                                                                  //per mezzo della funzione "readScheduleBin" e dando in input rifiuto e numero pagina

        if (s ==  null)//Se la stringa "s" è vuota(ricerca senza risultati) stampo tramite Toast il messaggio "Rifiuto non disponibile"
            Toast.makeText(this, R.string.cannot_find_bin, Toast.LENGTH_LONG).show();
        else           //Altrimenti
            textDetails.setText(garbage + "\n\n- " + s); //Settaggio testo della "textDetails" in base al risultato della stringa
        textGarbage.setText("");        //Settaggio testo rifiuto a "vuoto"
    }
}
