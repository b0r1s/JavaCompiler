package ast.General;

import ast.Codigo;
import ast.Nodo;
import ast.Tipo.TSimpleOArray;
import ast.Tipo.Tipo;

public class Met extends Nodo {
    private Tipo ttipo;
    private final String nombre;
    private final Params params;
    private final BloqueIns bloque;

    public Met(String nombre, Params params, BloqueIns bloque) {
        this.nombre = nombre;
        this.params = params;
        this.bloque = bloque;
    }

    public Tipo getTtipo() {
        return ttipo;
    }

    public String getNombre() {
        return nombre;
    }

    public Params getParams() {
        return params;
    }

    public BloqueIns getBloque() {
        return bloque;
    }

    public void setTtipo(Tipo ttipo) {
        this.ttipo = ttipo;
        if(ttipo!=null && Codigo.TIPO_ARRAY.equals(ttipo.code()) &&
                ((TSimpleOArray) ttipo).getCorchetes().isEmpty()) {
            this.ttipo = ((TSimpleOArray)ttipo).getTipoBase();
        }
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
        return Codigo.MET;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                newPrefix + "\\__Nombre: " + nombre + "\n" +
                (ttipo !=null ? ttipo.toString(newPrefix) : "") +
                params.toString(newPrefix) +
                bloque.toString(newPrefix);
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Metodo:";
    }

    @Override
    public Met setFila(int fila) {
        return (Met) super.setFila(fila);
    }
}
