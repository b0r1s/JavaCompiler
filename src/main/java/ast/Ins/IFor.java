package ast.Ins;

import ast.Codigo;
import ast.Expr.Expr;
import ast.General.BloqueIns;
import ast.Nodo;

public class IFor extends Ins {
    private final Ins ini;
    private final Expr cond;
    private final Ins iter;
    private final BloqueIns bloque;

    public IFor(Ins ini, Expr cond, Ins iter, BloqueIns bloque) {
        this.ini = ini;
        this.cond = cond;
        this.iter = iter;
        this.bloque = bloque;
    }

    public Ins getIni() {
        return ini;
    }

    public Expr getCond() {
        return cond;
    }

    public Ins getIter() {
        return iter;
    }

    public BloqueIns getBloque() {
        return bloque;
    }

    @Override
    public Codigo code() {
        return Codigo.FOR;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                (ini!=null ? ini.toString(newPrefix) : "") +
                cond.toString(newPrefix) +
                (iter!=null ? iter.toString(newPrefix) : "") +
                bloque.toString(newPrefix);
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "For:";
    }

    @Override
    public IFor setFila(int fila) {
        return (IFor) super.setFila(fila);
    }
}
