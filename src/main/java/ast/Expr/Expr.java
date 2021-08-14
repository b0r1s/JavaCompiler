package ast.Expr;

import ast.Codigo;
import ast.Extra.ExtraValor;
import ast.Extra.VValorExtraArray;
import ast.Nodo;

public abstract class Expr extends Nodo {
    private ExtraValor extra;

    public ExtraValor getExtra() {
        return extra;
    }

    public void setExtra(ExtraValor extra) {
        if(extra!=null && Codigo.EXTRA_VAL_POS.equals(extra.code()) &&
                ((VValorExtraArray)extra).getCorch().isEmpty()) {
            return; //Es un falso extra
        }
        if(this.extra==null){
            this.extra = extra;
        }
        else {
            this.extra.setExtra(extra);
        }
    }

    @Override
    public Expr setFila(int fila) {
        return (Expr) super.setFila(fila);
    }
}
