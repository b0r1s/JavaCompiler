package ast.General;

import ast.Codigo;
import ast.Ins.IAsig;
import ast.Nodo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Clase extends Nodo {
    private String nombre;
    private final List<IAsig> listaAtrib;
    private final List<Met> listaMet;

    public Clase() {
        this.listaAtrib = new ArrayList<>();
        this.listaMet = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Met> getListaMet() {
        return listaMet;
    }

    public List<IAsig> getListaAtrib() {
        return listaAtrib;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void add(Met met) {
        listaMet.add(0,met);
    }

    public void add(IAsig ins) {
        listaAtrib.add(0,ins);
    }

    //El atributo campos es para ganar eficiencia en el análisis semántico
    private Map<String,Nodo> campos;

    public Map<String, Nodo> getCampos() {
        return campos;
    }

    public void setCampos(Map<String, Nodo> campos) {
        this.campos = campos;
    }

    @Override
    public Codigo code() {
        return Codigo.CLASS;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        StringBuilder s = new StringBuilder(prefix + nombreNodo() + "\n");
        s.append(newPrefix + "\\__Nombre: " + nombre + "\n");
        for(IAsig asig: listaAtrib) {
            s.append(asig.toString(newPrefix));
        }
        for(Met met: listaMet) {
            s.append(met.toString(newPrefix));
        }
        return s.toString();
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Clase:";
    }

    @Override
    public Clase setFila(int fila) {
        return (Clase) super.setFila(fila);
    }
}
