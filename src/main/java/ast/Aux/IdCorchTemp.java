package ast.Aux;

public class IdCorchTemp {
    private String id;
    private Corchetes corchetes;

    public IdCorchTemp() {}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Corchetes getCorchetes() {
        return corchetes;
    }

    public void setCorchetes(Corchetes corchetes) {
        this.corchetes = corchetes;
    }

    //Para mostrar los errores con fila
    private int fila = -1;
    public int getFila() {
        return fila;
    }
    public IdCorchTemp setFila(int fila) {
        this.fila = fila;
        return this;
    }
}
