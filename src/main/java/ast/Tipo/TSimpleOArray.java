package ast.Tipo;

import ast.Aux.Corchetes;
import ast.Aux.IdCorchTemp;
import ast.Codigo;

//No solo es tipo de declaracion, sino tb es el tipo que escribes tras el new.
public class TSimpleOArray extends Tipo {
    private Tipo tipoBase;
    private Corchetes corchetes;

    public TSimpleOArray(Tipo tipoBase, Corchetes corchetes) {
        this.tipoBase = tipoBase;
        this.corchetes = corchetes;
    }

    public TSimpleOArray(IdCorchTemp idCorchTemp) {
        this.tipoBase = (new TClase(idCorchTemp.getId())).setFila(idCorchTemp.getFila());
        this.corchetes= idCorchTemp.getCorchetes();
    }

    public Corchetes getCorchetes() {
        return corchetes;
    }

    public Tipo getTipoBase() {
        return tipoBase;
    }

    @Override
    public Codigo code() {
        return Codigo.TIPO_ARRAY;
    }

    @Override
    public String toString(String prefix) {
        if(!corchetes.isEmpty()){
            String newPrefix = newPrefix(prefix, nombreNodo().length());
            return prefix + nombreNodo() + "\n" +
                    corchetes.toString(newPrefix) + //no es un nodo
                    tipoBase.toString(newPrefix);
        }
        else {
            return tipoBase.toString(prefix);
        }
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoArray:";
    }

    @Override
    public TSimpleOArray setFila(int fila) {
        return (TSimpleOArray) super.setFila(fila);
    }
}
