package errors;

import alex.UnidadLexica;
import ast.Aux.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GestionErroresTiny {

    //Utilizo este indicador para evitarme, entre otras cosas, la recursion de Boolean en cup
    private int numErrores;
    private final List<Pair<Integer,String>> listErroresSem;

    public GestionErroresTiny() {
        this.numErrores = 0;
        this.listErroresSem = new ArrayList<>();
    }

    public int getNumErrores() {
        return numErrores;
    }

    public void printErrSem() {
        listErroresSem.sort(Comparator.comparingInt(a -> a.first));
        for(Pair<Integer,String> p : listErroresSem) {
            System.out.println(p.second);
        }
    }

    // ERRORES LEXICOS

    public void errorLexico(int fila, String lexema) {
        System.out.println("ERROR LEX -> Fila "+fila+": Caracter inesperado: "+lexema);
        numErrores++;
    }

    //ERRORES SINTÁCTICOS

    public void errorSintactico(UnidadLexica unidadLexica) {
        System.out.print("ERROR SINTACT -> Fila "+unidadLexica.fila()+": Elemento inesperado '"+unidadLexica.lexema());
        System.out.print("', Clase nº" + unidadLexica.clase()+" --> ");
        numErrores++;
        //System.exit(1); //Queremos poder recuperarnos de error
    }

    //ERRORES SEMÁNTICOS
    //Ámbito

    public void errorSemanticoAmbito(int fila, boolean yaDefONoDef, String id) { //true=yaDef,false=noDef
        String s = "ERROR SEM (de ámbito) Fila "+fila+" -> Identificador "+id+(yaDefONoDef ? " ya " : " no ")+"definido!";
        listErroresSem.add(new Pair<>(fila,s));
        numErrores++;
    }

    public void errorSemanticoMiembroDuplic(int fila, String id) { //true=yaDef,false=noDef
        String s = "ERROR SEM (de ámbito) Fila "+fila+" -> Miembro "+id+" duplicado en la clase";
        listErroresSem.add(new Pair<>(fila,s));
        numErrores++;
    }

    public void errorSemanticoAmbitoErroneo(int fila, String id) {
        String s = "ERROR SEM (de ámbito) Fila "+fila+" -> Ámbito erróneo: "+id+" escrito fuera de una clase";
        listErroresSem.add(new Pair<>(fila,s));
        numErrores++;
    }

    //Tipos

    public void errorSemanticoTipo(int fila, String mensaje) {
        String s = "ERROR SEM (de tipos) Fila "+fila+" -> "+mensaje;
        listErroresSem.add(new Pair<>(fila,s));
        numErrores++;
    }

}
