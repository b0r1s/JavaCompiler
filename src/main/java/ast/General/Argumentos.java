package ast.General;

import ast.Codigo;
import ast.Expr.Expr;
import ast.Nodo;

import java.util.ArrayList;
import java.util.List;

public class Argumentos extends Nodo {
    private final List<Expr> lista;

    public Argumentos() {
        this.lista = new ArrayList<>();
    }

    public List<Expr> getLista() {
        return lista;
    }

    public void add(Expr e) {
        lista.add(0,e);
    }

    @Override
    public Codigo code() {
        return Codigo.ARGUMENTOS;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        StringBuilder s = new StringBuilder(prefix + nombreNodo() + "\n");
        for(Expr e:lista) {
            s.append(e.toString(newPrefix));
        }
        return s.toString();
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Argumentos:";
    }

    @Override
    public Argumentos setFila(int fila) {
        return (Argumentos) super.setFila(fila);
    }
}
