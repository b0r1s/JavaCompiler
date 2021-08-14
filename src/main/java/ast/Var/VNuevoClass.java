package ast.Var;

import ast.Codigo;
import ast.Expr.Expr;
import ast.General.Argumentos;
import ast.Tipo.TSimpleOArray;
import ast.Tipo.Tipo;

public class VNuevoClass extends Expr {
    private Tipo ttipo;
    private Argumentos argumentos;

    public VNuevoClass(Tipo ttipo, Argumentos argumentos) {
        this.ttipo = ttipo;
        if(ttipo!=null && Codigo.TIPO_ARRAY.equals(ttipo.code()) &&
                ((TSimpleOArray) ttipo).getCorchetes().isEmpty()) {
            this.ttipo = ((TSimpleOArray)ttipo).getTipoBase();
        }
        this.argumentos = argumentos;
    }

    public Tipo getTtipo() {
        return ttipo;
    }

    public Argumentos getArgumentos() {
        return argumentos;
    }

    @Override
    public Codigo code() {
        return Codigo.NEW_CLASE;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                ttipo.toString(newPrefix) +
                argumentos.toString(newPrefix) +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "NuevoObj:";
    }

    @Override
    public VNuevoClass setFila(int fila) {
        return (VNuevoClass) super.setFila(fila);
    }
}
