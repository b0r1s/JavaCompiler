package ast.Tipo;

import ast.Codigo;

public class TNull extends Tipo {
    public TNull() {}

    @Override
    public Codigo code() {
        return Codigo.TIPO_NULL;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoNull";
    }

    @Override
    public TNull setFila(int fila) {
        return (TNull) super.setFila(fila);
    }
}
