package ast.Lit;

import ast.Codigo;
import ast.Expr.Expr;

public class VNulo extends Expr {

    public VNulo() {}

    //Hago esta comprobacion en el analisis de tipos
    /*@Override
    public void setExtra(ExtraValor extra) {
        throw new IllegalStateException();
    }*/

    @Override
    public Codigo code() {
        return Codigo.NULL;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "ValorNull";
    }

    @Override
    public VNulo setFila(int fila) {
        return (VNulo) super.setFila(fila);
    }
}
