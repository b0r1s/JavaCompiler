package ast.Tipo;

import ast.Codigo;

public class TChar extends Tipo {
    public TChar() {}

    @Override
    public Codigo code() {
        return Codigo.TIPO_CHAR;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoChar";
    }

    @Override
    public TChar setFila(int fila) {
        return (TChar) super.setFila(fila);
    }
}
