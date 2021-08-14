package ast.Var;

import ast.Expr.Expr;
import ast.Aux.IdCorchTemp;

public abstract class ExprValor extends Expr {

    public abstract void setId(String id);
    public abstract void setId(IdCorchTemp idCorchTemp);

}
