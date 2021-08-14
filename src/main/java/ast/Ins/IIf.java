package ast.Ins;

import ast.Codigo;
import ast.Expr.Expr;
import ast.General.BloqueIns;
import ast.Nodo;

public class IIf extends Ins {
    private final Expr cond;
    private final BloqueIns bloqueIf;
    private final BloqueIns bloqueElse;

    public IIf(Expr cond, BloqueIns bloqueIf, BloqueIns bloqueElse) {
        this.cond = cond;
        this.bloqueIf = bloqueIf;
        this.bloqueElse = bloqueElse;
    }

    public Expr getCond() {
        return cond;
    }

    public BloqueIns getBloqueIf() {
        return bloqueIf;
    }

    public BloqueIns getBloqueElse() {
        return bloqueElse;
    }

    @Override
    public Codigo code() {
        return Codigo.IF;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                cond.toString(newPrefix) +
                bloqueIf.toString(newPrefix) +
                (bloqueElse!=null ? bloqueElse.toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "If:";
    }

    @Override
    public IIf setFila(int fila) {
        return (IIf) super.setFila(fila);
    }
}
