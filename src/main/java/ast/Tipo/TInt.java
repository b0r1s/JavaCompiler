package ast.Tipo;

import ast.Codigo;

public class TInt extends Tipo {
    public TInt() {}

    @Override
    public Codigo code() {
        return Codigo.TIPO_INT;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoInt";
    }

    @Override
    public TInt setFila(int fila) {
        return (TInt) super.setFila(fila);
    }
}
