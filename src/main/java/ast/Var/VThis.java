package ast.Var;

import ast.Codigo;
import ast.Expr.Expr;
import ast.Nodo;

public class VThis extends Expr {

    public VThis() {}

    //ten presente que hay un getExtra heredado (como en todas las expresiones)

    //Vinculo sirve para el análisis semántico
    private Nodo obj;

    public Nodo getObj() {
        return obj;
    }

    public void setObj(Nodo obj) {
        this.obj = obj;
    }

    @Override
    public Codigo code() {
        return Codigo.THIS;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "This";
    }

    @Override
    public VThis setFila(int fila) {
        return (VThis) super.setFila(fila);
    }
}
