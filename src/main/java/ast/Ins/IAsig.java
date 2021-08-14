package ast.Ins;

import ast.Codigo;
import ast.Expr.Expr;
import ast.Nodo;

public class IAsig extends Ins {
    private final Declar declar;
    private final Expr expr; //Puede ser null;

    public IAsig(Declar declar, Expr expr) {
        this.declar = declar;
        this.expr = expr;
    }

    public Declar getDeclar() {
        return declar;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Codigo code() {
        return Codigo.ASIG;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                declar.toString(newPrefix) +
                (expr!=null ? expr.toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Asig:";
    }

    @Override
    public IAsig setFila(int fila) {
        return (IAsig) super.setFila(fila);
    }
}
