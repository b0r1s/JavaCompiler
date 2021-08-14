package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class ENegado extends ExprUni {
    public ENegado(Expr op1) {
        super(op1);
    }

    @Override
    public Codigo code() {
        return Codigo.NEG;
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
        return "\\__" + "Expr: " + "!";
    }

    @Override
    public ENegado setFila(int fila) {
        return (ENegado) super.setFila(fila);
    }
}
