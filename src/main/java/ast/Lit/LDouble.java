package ast.Lit;

import ast.Codigo;

public class LDouble extends Lit {
    public LDouble(String valor) {
        super(valor);
    }

    @Override
    public Codigo code() {
        return Codigo.LIT_DOUBLE;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "LitDouble: " + getValor();
    }

    @Override
    public LDouble setFila(int fila) {
        return (LDouble) super.setFila(fila);
    }
}
