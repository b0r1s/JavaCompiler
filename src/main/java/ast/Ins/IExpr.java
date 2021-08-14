package ast.Ins;

import ast.Codigo;
import ast.Expr.Expr;
import ast.Nodo;

public class IExpr extends Ins {
    private final Expr expr;

    public IExpr(Expr expr) {
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Codigo code() {
        return Codigo.INS_EXPR;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                expr.toString(newPrefix);
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "InsExpr";
    }

    @Override
    public IExpr setFila(int fila) {
        return (IExpr) super.setFila(fila);
    }
}
