package ast.Tipo;

import ast.Codigo;

public class TBoolean extends Tipo {
    public TBoolean() {}

    @Override
    public Codigo code() {
        return Codigo.TIPO_BOOLEAN;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoBoolean";
    }

    @Override
    public TBoolean setFila(int fila) {
        return (TBoolean) super.setFila(fila);
    }
}
