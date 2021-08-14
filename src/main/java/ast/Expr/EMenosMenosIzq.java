package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EMenosMenosIzq extends ExprUni {
    public EMenosMenosIzq(Expr op1) {
        super(op1);
    }

    @Override
    public Codigo code() {
        return Codigo.MENOS_MENOS_IZQ;
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
        return "\\__" + "Expr: " + "--var";
    }

    @Override
    public EMenosMenosIzq setFila(int fila) {
        return (EMenosMenosIzq) super.setFila(fila);
    }
}
