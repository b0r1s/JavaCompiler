package ast.Ins;

import ast.Codigo;
import ast.Nodo;

public class IReturnVoid extends Ins {

    public IReturnVoid() {}

    //Esto servirá para comprobar tipos en el análisis de tipos
    private Nodo refMet;

    public Nodo getRefMet() {
        return refMet;
    }

    public void setRefMet(Nodo refMet) {
        this.refMet = refMet;
    }

    @Override
    public Codigo code() {
        return Codigo.RETURN_VOID;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Return Void";
    }

    @Override
    public IReturnVoid setFila(int fila) {
        return (IReturnVoid) super.setFila(fila);
    }
}
