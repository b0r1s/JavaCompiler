package ast.Tipo;

import ast.Codigo;
import ast.Nodo;

public class TClase extends Tipo {
    private final String id;

    public TClase(String id) {
        this.id = id;
    }

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
        return Codigo.TIPO_CLASE;
    }

    @Override
    public String toString(String prefix) {
        return prefix + nombreNodo() + "\n";
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "TipoClase: " + id;
    }

    @Override
    public TClase setFila(int fila) {
        return (TClase) super.setFila(fila);
    }
}
