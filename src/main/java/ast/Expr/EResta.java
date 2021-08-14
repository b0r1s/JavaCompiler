package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EResta extends ExprBin {
    public EResta(Expr op1, Expr op2) {
        super(op1, op2);
    }

    @Override
    public Codigo code() {
        return Codigo.RESTA;
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
        return "\\__" + "Expr: " + "-";
    }

    @Override
    public EResta setFila(int fila) {
        return (EResta) super.setFila(fila);
    }
}
