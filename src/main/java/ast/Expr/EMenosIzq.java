package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EMenosIzq extends ExprUni {
    public EMenosIzq(Expr op1) {
        super(op1);
    }

    @Override
    public Codigo code() {
        return Codigo.MENOS_IZQ;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                getOp1().toString(newPrefix) +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Expr: " + "-var";
    }

    @Override
    public EMenosIzq setFila(int fila) {
        return (EMenosIzq) super.setFila(fila);
    }
}
