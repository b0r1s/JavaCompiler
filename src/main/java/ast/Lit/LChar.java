package ast.Lit;

import ast.Codigo;

public class LChar extends Lit {
    public LChar(String valor) {
        super(valor);
    }

    @Override
    public Codigo code() {
        return Codigo.LIT_CHAR;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "LitChar: " + getValor();
    }

    @Override
    public LChar setFila(int fila) {
        return (LChar) super.setFila(fila);
    }
}
