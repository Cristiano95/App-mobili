package com.teocri.conference;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
//Classe per gestire l’utilizzo del ExpandableListView, per mostrare a video le diverse conferenze
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;                                //Dichiarazione context
    private List<String> listGroup;                         //Dichiarazione listGroup -> lista di stringhe per la gestione delle date
    private HashMap<String, List<Element>> listChild;       //Dichiarazione listChild -> hashMap contenente due campi: il primo che indica il listGroup interessato (-> data),
                                                            //e il secondo contenente la lista di conferenze per quella determinata data

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<Element>> listChildData) {//Costruttore
        this.context = context;
        this.listGroup = listDataHeader;
        this.listChild = listChildData;
    }

    @Override                       //Metodo per ottenere i dati, associati al child specificato all'interno del gruppo specificato.
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listChild.get(this.listGroup.get(groupPosition)).get(childPosititon);
    }

    @Override                       //Metodo per ottenere l'id della conferenza interessata ("position")
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override                       //Metodo per ottenere una vista per visualizzare i dati, per il child specificato all'interno del gruppo specificato.
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Element childText = (Element) getChild(groupPosition, childPosition); //Variabile "childText" di tipo "Element" uguale alla "getchild"
                                                                                    //per ottenere i dati, associati a "childPosition" del "groupPosition" specificato.
        if (convertView == null) {          //Se variabile "convertView" nulla
            LayoutInflater infalInflaterTitle = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//Creo e setto la variabile di tipo "LayoutInflater"
            convertView = infalInflaterTitle.inflate(R.layout.list_item, null); //Salvo in "convertView" -> mi servo della variabile "infalInflaterTitle"
        }                                                                           //gestione parte grafica delle conferenze -> layout/list_item.xml

        TextView childItem =    (TextView) convertView.findViewById(R.id.title);    //childItem - Textview riferita al titolo della conferenza
        TextView childSubItem = (TextView) convertView.findViewById(R.id.time);     //childSubItem - Textview riferita all'orario della conferenza
                                                                                    //(list_item.xml)
        childItem.setText(childText.getTitle());        //Settaggio testo titolo conferenza
        childSubItem.setText(childText.getTime());      //Settaggio testo orario conferenza
        return convertView;     //Ritorno "convertView"
    }

    @Override
    public int getChildrenCount(int groupPosition) {    //Metodo per ritornare il numero delle conferenze
        return this.listChild.get(this.listGroup.get(groupPosition)).size();    //Ritorno il numero di "listChild" di un determinato "listgroup"
    }

    @Override                                           //Metodo per ottenere i dati associati al "listGroup" specificato -> sulla base di position
    public Object getGroup(int groupPosition) {
        return this.listGroup.get(groupPosition);
    }

    @Override                                           //Metodo per ritornare il numero delle date (size di listGroup)
    public int getGroupCount() {
        return this.listGroup.size();
    }

    @Override                                           //Metodo per ottenere l'id della data interessata ("position")
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override                       //Metodo per ottenere una vista per visualizzare i dati, per il group specificato.
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);          //Variabile "headerTitle" uguale all'id del getGroup (determinata data)

        if (convertView == null) {                                      //Se variabile "convertView" nulla
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Creo e setto la variabile di tipo "LayoutInflater"
            convertView = infalInflater.inflate(R.layout.list_group, null); //Salvo in "convertView" -> mi servo della variabile "infalInflater"
        }                                                                       //gestione parte grafica delle date -> layout/list_group.xml

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.date);    //lblListHeader - Textview riferita alla data in cui è presente almeno una conferenza
        lblListHeader.setTypeface(null, Typeface.BOLD);                          //Settaggio tipo di testo in grassetto ("BOLD")
        lblListHeader.setText(headerTitle);                                         //Settaggio testo data ("listGroup")

        return convertView;     //Ritorno "convertView"
    }

    //Metodo che restituisce true se questo adattatore pubblica un valore long univoco che può fungere da chiave per l'articolo in una data posizione nel set di dati.
    @Override                                       //In tal caso ritorna false
    public boolean hasStableIds() {
        return false;
    }

    //Se il "Child" è selezionabile nella posizione specificata ritorno true
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}