package ast.Expr;

import ast.Codigo;
import ast.Nodo;

public class EMasMasDer extends ExprUni {
    public EMasMasDer(Expr op1) {
        super(op1);
    }

    @Override
    public Codigo code() {
        return Codigo.MAS_MAS_DER;
    }

    @Override
    public String toString(String prefix) {
        String newPrefix = Nodo.newPrefix(prefix, nombreNodo().length());
        return prefix + nombreNodo() + "\n" +
                getOp1().toString(newPrefix) +
                (getExtra()!=null ? getExtra().toString(newPrefix) : "");
    }

    @Override
    public String nombreNodo() {
        return "\\__" + "Expr: " + "var++";
    }

    @Override
    public EMasMasDer setFila(int fila) {
        return (EMasMasDer) super.setFila(fila);
    }
}
