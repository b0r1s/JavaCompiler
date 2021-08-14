package ast.General;

import ast.Codigo;
import ast.Expr.Expr;
import ast.Nodo;

import java.util.ArrayList;
import java.util.List;

public class ArrayInit extends Nodo {
    private final List<ArrayInit> listaRecur;
    private final List<Expr> lista;

    public ArrayInit() {
        this.listaRecur = new ArrayList<>();
        this.lista = null;
    }

    public ArrayInit(Argumentos args) {
        this.lista = args.getLista();
        this.listaRecur = null;
    }

    public boolean isRecur() {
        return listaRecur!=null;
    }

    public List<Expr> getLista() {
        return lista;
    }

    public List<ArrayInit> getListaRecur() {
        return listaRecur;
    }

    //Solo para el caso recur
    public void add(ArrayInit e) {
        listaRecur.add(0,e);
    }

    @Override
    public Codigo code() {
        return Codigo.ARRAY_INIT;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        StringBuilder s = new StringBuilder(prefix + nombreNodo() + "\n");
        if(isRecur()) {
            for(ArrayInit e:listaRecur) {
                s.append(e.toString(newPrefix));
            }
        }
        else {
            for(Expr e:lista) {
                s.append(e.toString(newPrefix));
            }
        }
        return s.toString();
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "ArrayInit:";
    }

    @Override
    public ArrayInit setFila(int fila) {
        return (ArrayInit) super.setFila(fila);
    }
}
