package ast.Expr;

public abstract class ExprUni extends Expr {
    private final Expr op1;

    public ExprUni(Expr op1) {
        this.op1 = op1;
    }

    public Expr getOp1() {
        return op1;
    }

    @Override
    public ExprUni setFila(int fila) {
        return (ExprUni) super.setFila(fila);
    }
}
