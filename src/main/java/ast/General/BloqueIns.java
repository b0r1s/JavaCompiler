package ast.General;

import ast.Codigo;
import ast.Ins.Ins;
import ast.Nodo;

import java.util.ArrayList;
import java.util.List;

public class BloqueIns extends Nodo {
    private final List<Ins> lista;

    public BloqueIns() {
        this.lista = new ArrayList<>();
    }

    public List<Ins> getLista() {
        return lista;
    }

    public void add(Ins ins) {
        lista.add(0,ins);
    }

    @Override
    public Codigo code() {
        return Codigo.BLOQUE_INS;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = newPrefix(prefix, nombreNodo().length());
        StringBuilder s = new StringBuilder(prefix + nombreNodo() + "\n");
        for(Ins ins:lista) {
            s.append(ins.toString(newPrefix));
        }
        return s.toString();
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Bloque:";
    }

    @Override
    public BloqueIns setFila(int fila) {
        return (BloqueIns) super.setFila(fila);
    }
}
