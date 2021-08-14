package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EMayor extends ExprBin {
    public EMayor(Expr op1, Expr op2) {
        super(op1, op2);
    }

    @Override
    public Codigo code() {
        return Codigo.MAYOR;
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
        return "\\__" + "Expr: " + ">";
    }

    @Override
    public EMayor setFila(int fila) {
        return (EMayor) super.setFila(fila);
    }
}
