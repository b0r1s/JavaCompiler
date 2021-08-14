package ast.Var;

import ast.Aux.IdCorchTemp;
import ast.Codigo;
import ast.Extra.VValorExtraArray;
import ast.General.Argumentos;
import ast.Nodo;

public class VValorMet extends ExprValor {
    private String id;
    private Argumentos args;

    public VValorMet(Argumentos args) {
        this.args = args;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setId(IdCorchTemp idCorchTemp) {
        this.id = idCorchTemp.getId();
        if(idCorchTemp.getCorchetes().getLista().size()!=0) {
            setExtra((new VValorExtraArray(idCorchTemp.getCorchetes())).setFila(idCorchTemp.getFila()));
        }
    }

    public String getId() {
        return id;
    }

    //ten presente que hay un getExtra heredado

    public Argumentos getArgs() {
        return args;
    }

    //Vinculo sirve para el análisis semántico
    private Nodo obj;

    public Nodo getObj() {
        return obj;
    }

    public void setObj(Nodo obj) {
        this.obj = obj;
    }

    @Override
    public Codigo code() {
        return Codigo.VAL_MET;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                newPrefix + "\\__Id: " + id + "\n" +
                args.toString(newPrefix) +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Método:";
    }

    @Override
    public VValorMet setFila(int fila) {
        return (VValorMet) super.setFila(fila);
    }
}
