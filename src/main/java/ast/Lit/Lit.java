package ast.Lit;

import ast.Expr.Expr;

public abstract class Lit extends Expr {
    private final String valor;

    //En los LInt si escribes (5).X() llega aquí pero con 5.X() no porque considera que 5. es un double
    //Hago esta comprobacion en el analisis de tipos
    /*@Override
    public void setExtra(ExtraValor extra) {
        throw new IllegalStateException();
    }*/

    public Lit(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
