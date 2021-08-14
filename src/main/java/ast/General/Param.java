package ast.General;

import ast.Codigo;
import ast.Nodo;
import ast.Tipo.TSimpleOArray;
import ast.Tipo.Tipo;

public class Param extends Nodo {
    private Tipo ttipo;
    private final String id;

    public Param(Tipo ttipo, String id) {
        this.ttipo = ttipo;
        if(ttipo!=null && Codigo.TIPO_ARRAY.equals(ttipo.code()) &&
                ((TSimpleOArray) ttipo).getCorchetes().isEmpty()) {
            this.ttipo = ((TSimpleOArray)ttipo).getTipoBase();
        }
        this.id = id;
    }

    public Tipo getTtipo() {
        return ttipo;
    }

    public String getId() {
        return id;
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

    @Override
    public Codigo code() {
        return Codigo.PARAM;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                ttipo.toString(newPrefix) +
                newPrefix + "\\__Nombre: "+ id + "\n" ;
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Param:";
    }

    @Override
    public Param setFila(int fila) {
        return (Param) super.setFila(fila);
    }
}
