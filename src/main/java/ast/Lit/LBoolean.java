package ast.Lit;

import ast.Codigo;

public class LBoolean extends Lit {
    public LBoolean(String valor) {
        super(valor);
    }

    @Override
    public Codigo code() {
        return Codigo.LIT_BOOLEAN;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "LitBoolean: " + getValor();
    }

    @Override
    public LBoolean setFila(int fila) {
        return (LBoolean) super.setFila(fila);
    }
}
