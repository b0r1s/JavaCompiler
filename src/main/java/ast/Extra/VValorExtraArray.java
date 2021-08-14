package ast.Extra;

import ast.Aux.Corchetes;
import ast.Codigo;
import ast.Nodo;

public class VValorExtraArray extends ExtraValor {
    private Corchetes corch;
    private ExtraValor extra;

    public VValorExtraArray(Corchetes corch) {
        this.corch = corch;
    }

    public Corchetes getCorch() {
        return corch;
    }

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
    public Codigo code() {
        return Codigo.EXTRA_VAL_POS;
    }

    @Override
    public String toString(String prefix) {
        if(corch.isEmpty()) {
            return (extra!=null ? extra.toString(prefix) : "");
        }
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                corch.toString(newPrefix) + //no es un nodo
                (extra!=null ? extra.toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Acceso array:";
    }

    @Override
    public VValorExtraArray setFila(int fila) {
        return (VValorExtraArray) super.setFila(fila);
    }
}
