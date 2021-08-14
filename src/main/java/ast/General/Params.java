package ast.General;

import ast.Codigo;
import ast.Nodo;

import java.util.ArrayList;
import java.util.List;

public class Params extends Nodo {
    private final List<Param> lista;

    public Params() {
        this.lista = new ArrayList<>();
    }

    public List<Param> getLista() {
        return lista;
    }

    public void add(Param p) {
        lista.add(0,p);
    }

    @Override
    public Codigo code() {
        return Codigo.PARAMETROS;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        StringBuilder s = new StringBuilder(prefix + nombreNodo() + "\n");
        for(Param p:lista) {
            s.append(p.toString(newPrefix));
        }
        return s.toString();
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Params:";
    }

    @Override
    public Params setFila(int fila) {
        return (Params) super.setFila(fila);
    }
}
