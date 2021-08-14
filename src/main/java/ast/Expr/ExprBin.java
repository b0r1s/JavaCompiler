package ast.Expr;

public abstract class ExprBin extends Expr {
    private final Expr op1;
    private final Expr op2;

    public ExprBin(Expr op1, Expr op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    public Expr getOp1() {
        return op1;
    }

    public Expr getOp2() {
        return op2;
    }

    @Override
    public ExprBin setFila(int fila) {
        return (ExprBin) super.setFila(fila);
    }
}
