package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EOpTernario extends ExprTer {

    public EOpTernario(Expr cond, Expr op1, Expr op2) {
        super(cond, op1, op2);
    }

    @Override
    public Codigo code() {
        return Codigo.OP_TER;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                getCond().toString(newPrefix) +
                getOp1().toString(newPrefix) +
                getOp2().toString(newPrefix) +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Expr: " + "_?_:_";
    }

    @Override
    public EOpTernario setFila(int fila) {
        return (EOpTernario) super.setFila(fila);
    }
}
