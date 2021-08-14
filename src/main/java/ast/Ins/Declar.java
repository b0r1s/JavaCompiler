package ast.Ins;

import ast.Aux.IdCorchTemp;
import ast.Codigo;
import ast.Extra.VValorExtraArray;
import ast.Nodo;
import ast.Tipo.TSimpleOArray;
import ast.Tipo.Tipo;

//Siempre esta dentro de un IAsig
public class Declar extends Ins {
    private Tipo ttipo;
    private final String id;
    private VValorExtraArray corch;

    public Declar(Tipo ttipo, String id) {
        this.ttipo = ttipo;
        if(ttipo!=null && Codigo.TIPO_ARRAY.equals(ttipo.code()) &&
                ((TSimpleOArray) ttipo).getCorchetes().isEmpty()) {
            this.ttipo = ((TSimpleOArray)ttipo).getTipoBase();
        }
        this.id = id;
        this.corch = null;
    }

    public Declar(IdCorchTemp idCorchTemp) {
        this.ttipo = null;
        this.id = idCorchTemp.getId();
        if(!idCorchTemp.getCorchetes().isEmpty()) {
            this.corch = new VValorExtraArray(idCorchTemp.getCorchetes());
            this.corch.setFila(idCorchTemp.getFila());
        }
    }

    public Tipo getTtipo() {
        return ttipo;
    }

    public String getId() {
        return id;
    }

    public VValorExtraArray getCorch() {
        return corch;
    }

    //Vinculo sirve para el análisis semántico
    private Nodo vinculo;

    public Nodo getVinculo() {
        return vinculo;
    }

    public void setVinculo(Nodo vinculo) {
        this.vinculo = vinculo;
    }

    //DirMem sirve para la generacion de codigo
    //pos del programa que almacena la direccion que usaremos

    private int dirMem = -1;

    public int getDirMem() {
        return dirMem;
    }

    public void setDirMem(int dirMem) {
        this.dirMem = dirMem;
    }

    //esLocal sirve para indicar (cuando ttipo!=null) si es una declaracion local o es un atributo

    private boolean local = true;

    public boolean esLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    public Codigo code() {
        return Codigo.DECLAR;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                (ttipo !=null ? ttipo.toString(newPrefix) : "") +
                newPrefix + "\\__Nombre: "+ id + "\n" +
                (corch !=null ? corch.toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Declar:";
    }

    @Override
    public Declar setFila(int fila) {
        return (Declar) super.setFila(fila);
    }
}
