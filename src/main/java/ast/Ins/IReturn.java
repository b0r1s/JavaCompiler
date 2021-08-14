package ast.Ins;

import ast.Codigo;
import ast.Expr.Expr;
import ast.Nodo;

public class IReturn extends Ins {
    private final Expr opnd;

    public IReturn(Expr opnd) {
        this.opnd = opnd;
    }

    public Expr getOpnd() {
        return opnd;
    }

    //Esto servirá para comprobar tipos en el análisis de tipos
    private Nodo refMet;

    public Nodo getRefMet() {
        return refMet;
    }

    public void setRefMet(Nodo refMet) {
        this.refMet = refMet;
    }

    @Override
    public Codigo code() {
        return Codigo.RETURN;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                opnd.toString(newPrefix);
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Return:";
    }

    @Override
    public IReturn setFila(int fila) {
        return (IReturn) super.setFila(fila);
    }
}
