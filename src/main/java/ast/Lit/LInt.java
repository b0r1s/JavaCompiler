package ast.Lit;

import ast.Codigo;

public class LInt extends Lit {
    public LInt(String valor) {
        super(valor);
    }

    @Override
    public Codigo code() {
        return Codigo.LIT_INT;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "LitInt: " + getValor();
    }

    @Override
    public LInt setFila(int fila) {
        return (LInt) super.setFila(fila);
    }
}
