package ast.Var;

import ast.Codigo;
import ast.Expr.Expr;
import ast.General.ArrayInit;
import ast.Tipo.TSimpleOArray;
import ast.Tipo.Tipo;

public class VNuevoArray extends Expr {
    private Tipo ttipo;
    private ArrayInit arrayInit;

    public VNuevoArray(Tipo ttipo, ArrayInit arrayInit) {
        this.ttipo = ttipo;
        if(ttipo!=null && Codigo.TIPO_ARRAY.equals(ttipo.code()) &&
                ((TSimpleOArray) ttipo).getCorchetes().isEmpty()) {
            this.ttipo = ((TSimpleOArray)ttipo).getTipoBase();
        }
        this.arrayInit = arrayInit;
    }

    public Tipo getTtipo() {
        return ttipo;
    }

    public ArrayInit getArrayInit() {
        return arrayInit;
    }

    @Override
    public Codigo code() {
        return Codigo.NEW_ARRAY;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                ttipo.toString(newPrefix) +
                (arrayInit!=null ? arrayInit.toString(newPrefix) :"") +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");

    }

    @Override
    public String nombreNodo() {
        return "\\__" + "NuevoArray:";
    }

    @Override
    public VNuevoArray setFila(int fila) {
        return (VNuevoArray) super.setFila(fila);
    }
}
