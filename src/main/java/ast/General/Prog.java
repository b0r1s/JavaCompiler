package ast.General;

import ast.Codigo;
import ast.Nodo;

import java.util.ArrayList;
import java.util.List;

public class Prog extends Nodo {
    private final List<Clase> lista;

    public Prog() {
        this.lista = new ArrayList<>();
    }

    public List<Clase> getLista() {
        return lista;
    }

    public void add(Clase c) {
        lista.add(0,c);
    }

    @Override
    public Codigo code() {
        return Codigo.PROG;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        StringBuilder s = new StringBuilder(prefix + nombreNodo() + "\n");
        for(Clase c:lista) {
            s.append(c.toString(newPrefix));
        }
        return s.toString();
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Prog";
    }

    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public Prog setFila(int fila) {
        return (Prog) super.setFila(fila);
    }
}
