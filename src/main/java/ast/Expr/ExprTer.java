package ast.Expr;

public abstract class ExprTer extends ExprBin {
    private final Expr cond;

    public ExprTer(Expr cond, Expr op1, Expr op2) {
        super(op1, op2);
        this.cond = cond;
    }

    public Expr getCond() {
        return cond;
    }

    @Override
    public ExprTer setFila(int fila) {
        return (ExprTer) super.setFila(fila);
    }
}
