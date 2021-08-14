package ast;

import alex.TokenValue;
import ast.Aux.Corchetes;
import ast.Aux.IdCorchTemp;
import ast.Expr.*;
import ast.Extra.VValorExtraArray;
import ast.Extra.VValorExtraPuntoAtrib;
import ast.Extra.VValorExtraPuntoMetodo;
import ast.General.*;
import ast.Ins.*;
import ast.Lit.*;
import ast.Tipo.*;
import ast.Var.*;

public class ASTiny {
  //PROG, se le añaden clases con un .add
  public Prog programa() {return new Prog();}

  //DECLARACIONES
  public Declar declaracion(Tipo tipo, String id) {return new Declar(tipo,id);}
  public Declar declaracion(IdCorchTemp id) {return new Declar(id);}
  public Met metodo(String nombre, Params params, BloqueIns bloque) {
    return new Met(nombre, params, bloque);
  }
  //Parametros (no hay procedimientos)
  public Params parametros() {return new Params();}
  public Param parametro(Tipo tipo, String id) {return new Param(tipo,id);}

  //TIPOS
  public TInt tipoInt() {return new TInt();}
  public TDouble tipoDouble() {return new TDouble();}
  public TBoolean tipoBoolean() {return new TBoolean();}
  public TChar tipoChar() {return new TChar();}
  public TSimpleOArray tipoSimpleOArray(Tipo tipoBase, Corchetes corchetes) {return new TSimpleOArray(tipoBase, corchetes);}
  public TSimpleOArray tipoSimpleOArray(IdCorchTemp idCorchTemp) {return new TSimpleOArray(idCorchTemp);}
  public TVoid tipoVoid() {return new TVoid();}
  //Ref a clase
  public TClase tipoClase(String id) {return new TClase(id);}
  //Creacion tipo clase
  public Clase clase() {
    return new Clase();
  }

  //INSTRUCCIONES
  public IIf iif(Expr cond, BloqueIns bloqueIf, BloqueIns bloqueElse) {return new IIf(cond,bloqueIf,bloqueElse);}
  public IFor ffor(Ins ini, Expr cond, Ins iter, BloqueIns bloque) {
    return new IFor(ini,cond,iter,bloque);
  }
  public IReturn returnNoVoid(Expr opnd) {return new IReturn(opnd);}
  public IReturnVoid returnVoid() {return new IReturnVoid();}
  public IExpr expresion(Expr expr) {return new IExpr(expr);}
  public IAsig asignacion(Declar declar, Expr expr) {return new IAsig(declar,expr);}
  //Creacion de variables
  public VNuevoClass nuevoObjClase(Tipo tipo, Argumentos argumentos) {return new VNuevoClass(tipo,argumentos);}
  public VNuevoArray nuevoObjArray(Tipo tipo, ArrayInit arrayInit) {
    return new VNuevoArray(tipo, arrayInit);
  }
  //Argumentos
  public Argumentos argumentos() {return new Argumentos();}
  //Instrucciones, se le añaden instrucciones con un .add
  public BloqueIns bloque() {return new BloqueIns();}
  //Inicializadores de nuevoObjArray
  public ArrayInit arrayInit() {return new ArrayInit();}
  public ArrayInit arrayInit(Argumentos args) {return new ArrayInit(args);}
  //Ref a método
  public VValorMet valorMet(Argumentos args) {return new VValorMet(args);}

  //DESIGNADORES
  //Variable (extienden a Expr)
  public VValor valor() {return new VValor();}
  public VValor valor(String id) {return new VValor(id);}
  public VThis vthis() {return new VThis();}
  //Extras de las expresiones (todas las expr contienen un atributo Extra pero en los lit lo hice inaccesible)
  public VValorExtraArray extraArray(Corchetes corch) {return new VValorExtraArray(corch);}
  public VValorExtraPuntoMetodo extraMetodo(Argumentos argumentos) {
    return new VValorExtraPuntoMetodo(argumentos);
  }
  public VValorExtraPuntoAtrib extraAtrib() {
    return new VValorExtraPuntoAtrib();
  }

  //EXPRESIONES
  public ESuma suma(Expr opnd1, Expr opnd2) {return new ESuma(opnd1,opnd2);}
  public EResta resta(Expr opnd1, Expr opnd2) {return new EResta(opnd1,opnd2);}
  public EMul mul(Expr opnd1, Expr opnd2) {return new EMul(opnd1,opnd2);}
  public EDiv div(Expr opnd1, Expr opnd2) {return new EDiv(opnd1,opnd2);}
  public EMod mod(Expr opnd1, Expr opnd2) {return new EMod(opnd1,opnd2);}
  public EAnd and(Expr opnd1, Expr opnd2) {return new EAnd(opnd1,opnd2);}
  public EOr or(Expr opnd1, Expr opnd2) {return new EOr(opnd1,opnd2);}
  public EIgualComp igualComp(Expr opnd1, Expr opnd2) {return new EIgualComp(opnd1,opnd2);}
  public EDistComp distComp(Expr opnd1, Expr opnd2) {return new EDistComp(opnd1,opnd2);}
  public EMayor mayor(Expr opnd1, Expr opnd2) {return new EMayor(opnd1,opnd2);}
  public EMayorIgual mayorIgual(Expr opnd1, Expr opnd2) {return new EMayorIgual(opnd1,opnd2);}
  public EMenor menor(Expr opnd1, Expr opnd2) {return new EMenor(opnd1,opnd2);}
  public EMenorIgual menorIgual(Expr opnd1, Expr opnd2) {return new EMenorIgual(opnd1,opnd2);}
  public ENegado negado(Expr opnd) {return new ENegado(opnd);}
  public EMasMasDer masMasDer(Expr opnd) {return new EMasMasDer(opnd);}
  public EMenosMenosDer menosMenosDer(Expr opnd) {return new EMenosMenosDer(opnd);}
  public EMasMasIzq masMasIzq(Expr opnd) {return new EMasMasIzq(opnd);}
  public EMenosMenosIzq menosMenosIzq(Expr opnd) {return new EMenosMenosIzq(opnd);}
  public EMasIzq masIzq(Expr opnd) {return new EMasIzq(opnd);}
  public EMenosIzq menosIzq(Expr opnd) {return new EMenosIzq(opnd);}
  public EOpTernario opTernario(Expr cond, Expr op1, Expr op2) {return new EOpTernario(cond, op1, op2);}
  //Literales (extienden a Expr)
  public LInt ent(String valor) {return new LInt(valor);}
  public LDouble doub(String valor) {return new LDouble(valor);}
  public LBoolean bool(String valor) {return new LBoolean(valor);}
  public LChar charac(String valor) {return new LChar(valor);}
  public VNulo nulo() {return new VNulo();} //No extiende a Lit pero tiene su sentido ponerlo aquí

  //Auxiliar
  public Corchetes corchetes() {return new Corchetes();}
  public IdCorchTemp idCorchTemp() {return new IdCorchTemp();}
  public TokenValue token(int fila, String id) {return new TokenValue(id,fila);}
}
