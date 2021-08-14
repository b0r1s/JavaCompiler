package ast.Extra;

import ast.Var.VValor;

public abstract class ExtraValorPunto extends ExtraValor {

    public abstract void setId(VValor id);

    @Override
    public ExtraValorPunto setFila(int fila) {
        return (ExtraValorPunto) super.setFila(fila);
    }
}
