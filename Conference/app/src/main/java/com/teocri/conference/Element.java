package com.teocri.conference;

import java.io.Serializable;

/*Abbiamo creato questa classe per poter avere tutte le informazioni in una sola struttura da passare dal
        MainActivity alla ConferenceDetails in modo semplice tramite: intent.putExtra() (permesso dal fatto che
        Element implementa Serializable).*/

public class Element implements Serializable {
    private String id;
    private String title;
    private String date;
    private String time;
    private String description;
    private String speakers;

    public Element() {}

    public Element(String id, String title, String date, String time, String description, String speakers) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
        this.speakers = speakers;
    }

    public String getId() {
        return id;
    }//Metodo per ottenere l'id

    public void setId(String id) {      //Metodo per settare l'id
        this.id = id;
    }//Metodo per settare l'id

    //Metodo per ottenere il titolo della conferenza
    public String getTitle() {
        return title;
    }
    //Metodo per settare il titolo della conferenza
    public void setTitle(String title) {
        this.title = title;
    }

    //Metodo per ottenere la data della conferenza
    public String getDate() { return date; }
    //Metodo per settare la data della conferenza
    public void setDate(String date) { this.date = date;  }

    //Metodo per ottenere il tempo della conferenza
    public String getTime() {
        return time;
    }
    //Metodo per ottenere il tempo di inizio della conferenza -> "getStartHour" e "getStartMins" -> ore e minuti
    public String getStartTime() {
        return this.time.substring(0, 5);
    }
    //Metodo per ottenere il tempo di fine della conferenza -> "getEndHour" e "getEndMins" -> ore e minuti
    public String getEndTime() {
        return this.time.substring(8, this.time.length());
    }
    //Metodo per settare il tempo della conferenza
    public void setTime(String time) {
        this.time = time;
    }


    public String getDescription() {//Metodo per ottenere la descrizione della conferenza
        if (description.length() == 0)                      //Se la lunghezza della descrizione è uguale a 0
            description = "no description for this event";  //Setto descrizione a "nessuna descrizione per questo evento" e ritorna tale valore
        return description;
    }
    //Metodo per settare la descrizione della conferenza
    public void setDescription(String description) {
        this.description = description;
    }


    public String getSpeakers() {//Metodo per ottenere lo speakers della conferenza
        if (speakers.length() == 0 )                        //Se la lunghezza dello speaker è uguale a 0
            speakers = "no speakers for this event";        //Setto speaker a "nessuno speaker per questo evento" e ritorna tale valore
        return speakers;
    }
    //Metodo per settare lo speakers della conferenza
    public void setSpeakers(String speakers) {
        this.speakers = speakers;
    }


    //Metodo per ottenere l'anno della conferenza
    public int getYear() {
        return Integer.parseInt(this.date.substring(0, 4));
    }

    //Metodo per ottenere il mese della conferenza
    public int getMonth() {
        return Integer.parseInt(this.date.substring(5, 7));
    }

    //Metodo per ottenere il giorno della conferenza
    public int getDay() {
        return Integer.parseInt(this.date.substring(8, 10));
    }

    //Metodo per ottenere l'ora di inizio della conferenza
    public int getStartHour() {
        return Integer.parseInt(getStartTime().substring(0, 2));
    }

    //Metodo per ottenere i minuti di inizio della conferenza
    public int getStartMins() {
        return Integer.parseInt(getStartTime().substring(3, 5));
    }

    //Metodo per ottenere l'ora di fine della conferenza
    public int getEndHour() {
        return Integer.parseInt(getEndTime().substring(0, 2));
    }

    //Metodo per ottenere i minuti di fine della conferenza
    public int getEndMins() {
        return Integer.parseInt(getEndTime().substring(3, 5));
    }
}
