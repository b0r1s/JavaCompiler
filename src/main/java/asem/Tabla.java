package asem;

import ast.Codigo;
import ast.General.Clase;
import ast.General.Met;
import ast.Nodo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tabla {
    private final List<Map<String, Nodo>> cola;

    private Clase main; //null si no hay Main
    private boolean soloUnMain;

    public Tabla() {
        this.cola = new ArrayList<>();
        this.main = null;
        this.soloUnMain = true;
    }

    public void abreBloque() {
        Map<String, Nodo> bloque = new HashMap<>();
        cola.add(bloque);
    }

    public void abreBloque(Clase clase, Met metodo) {
        abreBloque();
        //Uso palabras reservadas porque CUP impedira que sean variables
        cola.get(cola.size()-1).put("this",clase);
        cola.get(cola.size()-1).put("return",metodo);

        if("main".equals(metodo.getNombre()) &&
                Codigo.TIPO_VOID.equals(metodo.getTtipo().code()) &&
                metodo.getParams().getLista().size()==0) {
            if(main!=null) {
                soloUnMain = false;
            } else {
                main = clase;
            }
        }
    }

    public boolean tieneMainCorrecto() {
        return main!=null && soloUnMain;
    }

    public Clase getMain() {
        return main;
    }

    public void cierraBloque() {
        cola.remove(cola.size()-1);
    }

    //Inserta en el bloque actual
    public boolean insertaId(String id, Nodo nodo) {
        Map<String, Nodo> bloqueActual = cola.get(cola.size()-1);
        if(bloqueActual.containsKey(id)) {
            return false;
        }
        else {
            bloqueActual.put(id,nodo);
            return true;
        }
    }

    //Se usa para ver si está declarado en algún bloque padre, si no devuelve null
    public Nodo declaracionDe(String id) {
        Nodo nodo = null;
        for (int i = cola.size()-1; i >=0 && nodo==null; i--) {
            nodo = cola.get(i).get(id);
        }
        return nodo;
    }

    public Nodo getNodoClase() {
        return cola.get(cola.size()-1).get("this");
    }

    public Nodo getNodoMetodo() {
        return cola.get(cola.size()-1).get("return");
    }
}