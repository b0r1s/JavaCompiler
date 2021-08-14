package ast.Aux;

import ast.Expr.Expr;

import java.util.ArrayList;
import java.util.List;

import static ast.Nodo.newPrefix;

public class Corchetes {
    private final List<Expr> lista;

    public Corchetes() {
        this.lista = new ArrayList<>();
    }

    public Corchetes(List<Expr> lista) {
        this.lista = lista;
    }

    public List<Expr> getLista() {
        return lista;
    }

    public void add(Expr e) {
        lista.add(0,e);
    }

    public boolean isEmpty() {
        return lista.size()==0;
    }

    //Tiene esto para facilitar la impresion, pero son TSimpleOArray y VValorExtraArray
    //Los que gestionan su contenido
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        StringBuilder s = new StringBuilder(prefix + nombreNodo() + "\n");
        for(Expr e:lista) {
            s.append(e.toString(newPrefix));
        }
        return s.toString();
    }
    public String nombreNodo() {
        return "\\__" + "Corchetes:";
    }
}
