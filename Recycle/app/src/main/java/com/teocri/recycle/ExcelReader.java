package com.teocri.recycle;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
//Metodo per effettuare la lettura dal file elenco.xlsx
public class ExcelReader {

    static XSSFWorkbook wb;                     //Dichiarazione variabile statica utilizzata per gestire le diverse pagine del file elenco.xlsx

    public ExcelReader(InputStream is) throws IOException {
        this.wb = new XSSFWorkbook(is);   //Costruisce un oggetto XSSFWorkbook, bufferizzando l'intero flusso in memoria.
    }

    static  String[] readSchedulePerCity(int sheetPage, String city) {//Metodo per effettuare la lettura in relazione alla pagina 0 e alla città inserita
        XSSFSheet sheet = wb.getSheetAt(sheetPage); //Setto "sheet" in base alla funzione "getSheetAt" con la quale vado alla pagina del file, in relazione a "sheetPage" passato dal MainActivity

        int lastRow = sheet.getLastRowNum(); //Variabile che identifica l'ultima riga, che è settata data la pagina sheet e la funzione "getLastRowNum"
        Row row;                             //Dichiarazione riga

        String[] schedule = new String[3];   //Variabile vettore di stringhe "schedule", all'interno delle quali inserisco i risultati della lettura

        for (int i = 1; i <= lastRow; i++){  //Per ogni riga (dalla prima all'ultima)
            row = sheet.getRow(i);                                              //Setto la riga uguale alla riga i della pagina stessa
            if (String.valueOf(row.getCell(0)).trim().equals(city.trim())) { //Se la stringa all'interno della riga interessata e colonna 0
                schedule[0] = String.valueOf(row.getCell(1));                //è uguale alla città passata al metodo "readSchedulePerCity"
                schedule[1] = String.valueOf(row.getCell(2));
                schedule[2] = String.valueOf(row.getCell(3));    //Scrivo nel vettore di stringhe i risultati della lettura
                return schedule;                                   //Le tre stringhe hanno al loro interno, rispettivamente, le colonne 1-2-3 della riga interessata
            }                                                      //e ritorno il vettore di stringhe "schedule"
        }
        return schedule;                     //Ritorno il vettore di stringhe
    }

    static  String readRecycleBin(int sheetPage, String garbage) {//Metodo per effettuare la lettura in relazione alla pagina 1 e al rifiuto inserita
        XSSFSheet sheet = wb.getSheetAt(sheetPage); //Setto "sheet" in base alla funzione "getSheetAt" con la quale vado alla pagina del file, in relazione a "sheetPage" passato dal MainActivity

        int lastRow = sheet.getLastRowNum(); //Variabile che identifica l'ultima riga, che è settata data la pagina sheet e la funzione "getLastRowNum"
        Row row;                             //Dichiarazione riga

        for (int i = 1; i <= lastRow; i++){ //Controllo ogni riga (dalla prima all'ultima)
            row = sheet.getRow(i);                                              //Setto la riga uguale alla riga i della pagina stessa
            if (String.valueOf(row.getCell(0)).trim().equals(garbage.trim())) //Se la stringa all'interno della riga interessata e colonna 0
                return String.valueOf(row.getCell(2));                        //è uguale al rifiuto passato al metodo "readRecycleBin"
        }                                                                       //Ritorno il valore presente nella riga interessata e colonna 2

        for (int i = 1; i <= lastRow; i++){ //Controllo ogni riga (dalla prima all'ultima)
            row = sheet.getRow(i);                                                   //Setto la riga uguale alla riga i della pagina stessa
            String[] block = row.getCell(3).getStringCellValue().split(","); //"block" -> riga interessata e colonna 3, prendo la cella splittando le virgole
            for (int j = 0; j < block.length; j++)              //Effettuo il controllo su tutto il blocco
                if (garbage.trim().equals(block[j].trim()))     //Se il rifiuto passato al metodo è uguale a quello che sto controllando (in indice j della cella)
                    return String.valueOf(row.getCell(2));    //Ritorno il contenuto della cella 2 della riga interessata
        }
        return null;                //Valore di ritorno nullo qualora non ci siano risultati nella lettura
    }
}