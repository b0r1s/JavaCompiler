package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EMul extends ExprBin {
    public EMul(Expr op1, Expr op2) {
        super(op1, op2);
    }

    @Override
    public Codigo code() {
        return Codigo.MUL;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                getOp1().toString(newPrefix) +
                getOp2().toString(newPrefix) +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Expr: " + "*";
    }

    @Override
    public EMul setFila(int fila) {
        return (EMul) super.setFila(fila);
    }
}
