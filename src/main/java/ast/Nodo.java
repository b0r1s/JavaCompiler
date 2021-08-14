package ast;

import ast.Tipo.Tipo;

import java.util.Collections;

public abstract class Nodo {
    public abstract Codigo code();
    public abstract String nombreNodo();

    public abstract String toString(String prefix);
    public static String newPrefix(String prefix, int length) {
        return prefix + "|" + String.join("", Collections.nCopies(length-1, " "));
    }

    //Para el análisis de tipos
    private Tipo tipoCalculado;
    public Tipo getTipoCalculado() {
        return tipoCalculado;
    }
    public void setTipoCalculado(Tipo tipo) {
        this.tipoCalculado = tipo;
    }

    //Para mostrar los errores con fila
    private int fila = 0;
    public int getFila() {
        return fila;
    }
    public Nodo setFila(int fila) {
        this.fila = fila;
        return this;
    }
}
