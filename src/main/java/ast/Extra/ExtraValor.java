package ast.Extra;

import ast.Nodo;
import ast.Tipo.Tipo;

public abstract class ExtraValor extends Nodo {

    public abstract void setExtra(ExtraValor extra);
    public abstract ExtraValor getExtra();

    //Esto es para generacion de codigo
    private Tipo tipoIzq;
    public Tipo getTipoIzq() {
        return tipoIzq;
    }
    public void setTipoIzq(Tipo tipoIzq) {
        this.tipoIzq = tipoIzq;
    }

    @Override
    public ExtraValor setFila(int fila) {
        return (ExtraValor) super.setFila(fila);
    }
}
