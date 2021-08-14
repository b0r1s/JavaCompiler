package ast.Tipo;

import ast.Codigo;

public class TDouble extends Tipo {
    public TDouble() {}

    @Override
    public Codigo code() {
        return Codigo.TIPO_DOUBLE;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoDouble";
    }

    @Override
    public TDouble setFila(int fila) {
        return (TDouble) super.setFila(fila);
    }
}
