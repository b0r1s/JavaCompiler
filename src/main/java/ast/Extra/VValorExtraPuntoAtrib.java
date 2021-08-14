package ast.Extra;

import ast.Codigo;
import ast.Nodo;
import ast.Var.VValor;

public class VValorExtraPuntoAtrib extends ExtraValorPunto {
    private VValor id;
    private ExtraValor extra; //Puede ser nulo

    public VValorExtraPuntoAtrib() {}

    @Override
    public void setId(VValor id) {
        this.id = id;
    }

    public VValor getId() {
        return id;
    }

    public ExtraValor getExtra() {
        return extra;
    }

    @Override
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
        return Codigo.EXTRA_VAL_PUNTO_ATRIB;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                id.toString(newPrefix) +
                (extra!=null ? extra.toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Acceso atributo:";
    }

    @Override
    public VValorExtraPuntoAtrib setFila(int fila) {
        return (VValorExtraPuntoAtrib) super.setFila(fila);
    }
}
