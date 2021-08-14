package ast.Var;

import ast.Aux.IdCorchTemp;
import ast.Codigo;
import ast.Extra.VValorExtraArray;
import ast.Nodo;

public class VValor extends ExprValor {
    private String id;

    public VValor() {}

    public VValor(String id) {
        this.id = id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setId(IdCorchTemp idCorchTemp) {
        this.id = idCorchTemp.getId();
        if(!idCorchTemp.getCorchetes().isEmpty()) {
            setExtra((new VValorExtraArray(idCorchTemp.getCorchetes())).setFila(idCorchTemp.getFila()));
        }
    }

    //ten presente que hay un getExtra heredado

    public String getId() {
        return id;
    }

    //Vinculo sirve para el análisis semántico
    private Nodo vinculo;

    public Nodo getVinculo() {
        return vinculo;
    }

    public void setVinculo(Nodo vinculo) {
        this.vinculo = vinculo;
    }

    @Override
    public Codigo code() {
        return Codigo.VAL;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Var: " + getId();
    }

    @Override
    public VValor setFila(int fila) {
        return (VValor) super.setFila(fila);
    }
}
