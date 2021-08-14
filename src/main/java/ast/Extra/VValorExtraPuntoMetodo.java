package ast.Extra;

import ast.Codigo;
import ast.General.Argumentos;
import ast.Nodo;
import ast.Var.VValor;

public class VValorExtraPuntoMetodo extends ExtraValorPunto {
    private VValor id;
    private Argumentos argumentos;
    private ExtraValor extra; //Puede ser nulo

    public VValorExtraPuntoMetodo(Argumentos argumentos) {
        this.argumentos = argumentos;
    }

    public VValor getId() {
        return id;
    }

    @Override
    public void setId(VValor id) {
        this.id = id;
    }

    public Argumentos getArgumentos() {
        return argumentos;
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
        return Codigo.EXTRA_VAL_PUNTO_MET;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                id.toString(newPrefix) +
                argumentos.toString(newPrefix) +
                (extra!=null ? extra.toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Acceso metodo:";
    }

    @Override
    public VValorExtraPuntoMetodo setFila(int fila) {
        return (VValorExtraPuntoMetodo) super.setFila(fila);
    }
}
