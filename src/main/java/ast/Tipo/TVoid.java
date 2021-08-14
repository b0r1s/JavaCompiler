package ast.Tipo;

import ast.Codigo;

public class TVoid extends Tipo {
    public TVoid() {}

    @Override
    public Codigo code() {
        return Codigo.TIPO_VOID;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoVoid";
    }

    @Override
    public TVoid setFila(int fila) {
        return (TVoid) super.setFila(fila);
    }
}
