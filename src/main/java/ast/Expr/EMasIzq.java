package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EMasIzq extends ExprUni {
    public EMasIzq(Expr op1) {
        super(op1);
    }

    @Override
    public Codigo code() {
        return Codigo.MAS_IZQ;
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
        return "\\__" + "Expr: " + "+var";
    }

    @Override
    public EMasIzq setFila(int fila) {
        return (EMasIzq) super.setFila(fila);
    }
}
