package asem;

import ast.Aux.Corchetes;
import ast.Codigo;
import ast.Expr.Expr;
import ast.Expr.ExprBin;
import ast.Expr.ExprTer;
import ast.Expr.ExprUni;
import ast.Extra.ExtraValor;
import ast.Extra.VValorExtraArray;
import ast.Extra.VValorExtraPuntoAtrib;
import ast.Extra.VValorExtraPuntoMetodo;
import ast.General.*;
import ast.Ins.*;
import ast.Lit.Lit;
import ast.Lit.VNulo;
import ast.Nodo;
import ast.Tipo.*;
import ast.Var.*;
import errors.GestionErroresTiny;

import java.util.ArrayList;
import java.util.List;

public class AnalizadorTipos {

    private final GestionErroresTiny errores;

    public AnalizadorTipos(Nodo raiz) {
        this.errores = new GestionErroresTiny();

        //Esta funcion en los apuntes tiene la función de eliminar los alias de ref
        //usando la propiedad vínculo hasta llegar a uno básico.
        //Yo no tengo alias, así que no la implemento
        //simplificaDefTipos(raiz);
        //tipoSimplificado(raiz);

        chequea(raiz);
    }

    public GestionErroresTiny getErrores() {
        return errores;
    }

    private void chequea(Nodo nodo) {
        if(nodo==null) {
            return;
        }
        switch(nodo.code()) {
            //PROGRAMA
            case PROG: {
                Prog n = (Prog) nodo;
                for(Clase c : n.getLista()) {
                    if(c.getTipoCalculado()==null) { //una clase pudo hacer ya otra, me ahorro la pasada
                        chequea(c);
                    }
                }
                break;}
            //DECLARACIONES
            case DECLAR: {
                Declar n = (Declar) nodo;
                Tipo tipo = null;
                if(n.getTtipo()!=null) { //Aqui es una delaracion
                    tipo = n.getTtipo();
                    revisarTipos(n.getFila(),tipo, false);
                }
                else { //Aqui es un designador
                    Nodo vinculo = n.getVinculo();
                    if(vinculo==null) {
                        throw new IllegalStateException("Vinculo invalido");
                    }
                    //Pudo declararse en clase, metodo, param, declar
                    switch (vinculo.code()) {
                        default:
                            break;
                        case CLASS:
                        case MET: {
                            errores.errorSemanticoTipo(n.getFila(),"El identificador "+n.getId()+" no es una variable");
                            break; }
                        case PARAM:
                        case DECLAR: {
                            if(vinculo.getTipoCalculado()==null) { //Creo que no deberia entrar pero por si acaso
                                chequea(vinculo);
                            }
                            tipo = vinculo.getTipoCalculado();
                            if(n.getCorch()!=null) {
                                tipo = calculoTipoExtra(tipo,n.getCorch());
                            }
                            break;}
                    }
                }
                n.setTipoCalculado(tipo);
                //No hay nada que revisar en el id
                break; }
            case MET: {
                Met n = (Met) nodo;
                Tipo tipo = n.getTtipo();
                if(tipo==null) { //Constructora
                    Nodo claseBase = n.getObj();
                    if(claseBase==null || !Codigo.CLASS.equals(claseBase.code())) {
                        throw new IllegalStateException("Vinculo no correcto");
                    }
                    Clase clase = (Clase) claseBase;
                    if(!n.getNombre().equals(clase.getNombre())) {
                        errores.errorSemanticoTipo(n.getFila(),"Método sin tipo que no es constructor");
                    }
                    TClase tipoClase = new TClase(n.getNombre());
                    tipoClase.setVinculo(claseBase);
                    n.setTipoCalculado(tipoClase);
                    //Lo he hecho asi para que no pete
                }
                else { //Metodo normal
                    revisarTipos(n.getFila(),tipo, true);
                    n.setTipoCalculado(tipo); //Debe ir antes que el resto y no depender de los errores de su bloque o params
                    //Provocaria un deadlock si hay referencias cruzadas si no
                }

                chequea(n.getParams()); //ya comprueban estos si hay que seguir la recursion
                chequea(n.getBloque()); //este tb lo comprueba
                break; }
            case PARAMETROS: {
                Params n = (Params) nodo;
                for(Param p : n.getLista()) {
                    chequea(p);
                }
                break; }
            case PARAM: {
                Param n = (Param) nodo;
                Tipo tipo = n.getTtipo();
                revisarTipos(n.getFila(),tipo,false);
                n.setTipoCalculado(tipo);
                //No se hace nada con el id
                break; }
            //TIPOS
            case TIPO_INT:
            case TIPO_DOUBLE:
            case TIPO_CHAR:
            case TIPO_BOOLEAN:
            case TIPO_VOID:
            case TIPO_ARRAY:
            case TIPO_NULL:
            case TIPO_CLASE:
                //Voy a calcular que esté bien cuando los coja, de forma que nunca llamaré
                //a chequea de un tipo
                //Los que cogen el tipo deben comprobar si tienen un array falso
                //Deben comprobarlo los que cogen tipo: Met, Param, Declar, VNuevoArray, VNuevoClass
                throw new IllegalStateException("Llame a checkea de un tipo");
            case CLASS: {
                Clase n = (Clase) nodo;
                TClase tipoClase = new TClase(n.getNombre());
                tipoClase.setVinculo(n);
                n.setTipoCalculado(tipoClase); //Lo necesitamos para el this, y debe ir antes que nada
                //Hay que hacerlo antes de lo demas y no dependiendo de sus errores, al igual que met
                //El no hace lo anterior

                for(IAsig i : n.getListaAtrib()) {
                    chequea(i); //No tiene TipoCalculado
                }
                for(Met met : n.getListaMet()) {
                    if(met.getTipoCalculado()==null) { //Me ahorro la pasada
                        chequea(met);
                    }
                }
                break; }
            //INSTRUCCIONES
            //no tienen tipo calculado
            case IF: {
                IIf n = (IIf) nodo;
                chequea(n.getCond());
                if(!Codigo.TIPO_BOOLEAN.equals(n.getCond().getTipoCalculado().code())) {
                    errores.errorSemanticoTipo(n.getFila(),"El tipo de la condición del if debe ser booleano");
                }
                chequea(n.getBloqueIf());
                chequea(n.getBloqueElse());
                break; }
            case FOR: {
                IFor n = (IFor) nodo;
                chequea(n.getCond());
                if(!Codigo.TIPO_BOOLEAN.equals(n.getCond().getTipoCalculado().code())) {
                    errores.errorSemanticoTipo(n.getFila(),"El tipo de la condición del for debe ser booleano");
                }
                chequea(n.getIni());
                chequea(n.getIter());
                chequea(n.getBloque());
                break; }
            case INS_EXPR: {
                IExpr n = (IExpr) nodo;
                chequea(n.getExpr());
                break; }
            case ASIG: {
                IAsig n = (IAsig) nodo;
                chequea(n.getDeclar());
                chequea(n.getExpr());
                if(n.getExpr()!=null && n.getDeclar()!=null) {
                    //Notese que es una declaracion, asi que es una designacion
                    //NO se iguala a un metodo vaya
                    if(!tiposCompatiblesAsig(n.getDeclar().getTipoCalculado(),n.getExpr().getTipoCalculado())) {
                        errores.errorSemanticoTipo(n.getFila(),"Incompatibilidad de tipos en asignacion");
                    }
                }
                break; }
            case RETURN: {
                IReturn n = (IReturn) nodo;
                chequea(n.getOpnd());
                if(!tiposCompatiblesAsig(n.getRefMet().getTipoCalculado(),n.getOpnd().getTipoCalculado())) {
                    errores.errorSemanticoTipo(n.getFila(),"Incompatibilidad en return");
                }
                break; }
            case RETURN_VOID: {
                IReturnVoid n = (IReturnVoid) nodo;
                if(!Codigo.TIPO_VOID.equals(n.getRefMet().getTipoCalculado().code())) {
                    errores.errorSemanticoTipo(n.getFila(),"Falta valor de return");
                }
                break; }
            // vuelven a tener tipoCalculado
            case NEW_CLASE: {
                VNuevoClass n = (VNuevoClass) nodo;
                Tipo tipo = n.getTtipo();
                if(Codigo.TIPO_ARRAY.equals(tipo.code())) {
                    errores.errorSemanticoTipo(n.getFila(),"Mezla de creación de array y clases");
                }
                if(!Codigo.TIPO_CLASE.equals(tipo.code())) {
                    errores.errorSemanticoTipo(n.getFila(),"Tipo no dinámico");
                }
                else {
                    TClase tipoAux = (TClase) tipo;
                    Nodo vinculo = tipoAux.getVinculo();
                    if(vinculo==null || !Codigo.CLASS.equals(vinculo.code())) {
                        throw new IllegalStateException("Vinculo no correcto");
                    }
                    Clase claseDeclar = (Clase) vinculo;
                    Nodo constructora = claseDeclar.getCampos().get(tipoAux.getId());
                    if(constructora==null || !Codigo.MET.equals(constructora.code())) {
                        errores.errorSemanticoTipo(n.getFila(),"Clase "+tipoAux.getId()+" no tiene el constructor pedido");
                    }
                    else {
                        Met construct = (Met) constructora;
                        Params params = construct.getParams();
                        chequea(params);
                        if(params.getLista().size()!=n.getArgumentos().getLista().size()){
                            errores.errorSemanticoTipo(n.getFila(),"Discordancia entre el número de parámetros de la constructora");
                        }
                        else {
                            boolean compatibles = true;
                            for(int j=0; j<params.getLista().size(); j++) {
                                if(!tiposCompatiblesAsig(params.getLista().get(j).getTipoCalculado(),
                                        n.getArgumentos().getLista().get(j).getTipoCalculado())) {
                                    errores.errorSemanticoTipo(n.getFila(),"Incompatibilidad de tipos en parámetro "+j+"-esimo");
                                    compatibles = false;
                                }
                            }
                            if(compatibles) {
                                n.setTipoCalculado(calculoTipoExtra(tipoAux,n.getExtra()));
                            }
                        }
                    }
                }
                break; }
            case NEW_ARRAY: {
                VNuevoArray n = (VNuevoArray) nodo;
                Tipo tipo = n.getTtipo();
                if(!Codigo.TIPO_ARRAY.equals(tipo.code())) {
                    errores.errorSemanticoTipo(n.getFila(),"No es un array");
                }
                else {
                    TSimpleOArray tipoAux = (TSimpleOArray) tipo;
                    List<Expr> corch = tipoAux.getCorchetes().getLista();
                    if(Codigo.TIPO_VOID.equals(tipoAux.getTipoBase().code())) {
                        //Los otros: null,array creo que no se pueden dar.
                        errores.errorSemanticoTipo(n.getFila(),"No hay arrays de void");
                    }
                    else {
                        if(n.getArrayInit()==null) {
                            boolean correcto;
                            int i;
                            for (i = 0; i < corch.size(); i++) {
                                if(Codigo.NULL.equals(corch.get(i).code())) {
                                    break;
                                }
                            }
                            correcto = i != 0;
                            int limiteExpr = i;
                            for(;i<corch.size();i++) {
                                if(!Codigo.NULL.equals(corch.get(i).code())) {
                                    break;
                                }
                            }
                            correcto = i == corch.size() && correcto;
                            if(!correcto) {
                                errores.errorSemanticoTipo(n.getFila(),"Creación de array incorrecta");
                            }
                            else {
                                boolean exprEnteras = true;
                                for(i=0;i<limiteExpr;i++) {
                                    chequea(corch.get(i));
                                    if(!Codigo.TIPO_INT.equals(corch.get(i).getTipoCalculado().code())) {
                                        errores.errorSemanticoTipo(n.getFila(),"Las expresiones entre corchetes deben ser enteras");
                                        exprEnteras = false;
                                    }
                                }
                                if(exprEnteras) {
                                    n.setTipoCalculado(calculoTipoExtra(tipoAux,n.getExtra()));
                                }
                            }
                        }
                        else {
                            //Como tiene inicializador, los corchetes deben no tener nada
                            boolean correcto = true;
                            for(Expr e : corch) {
                                chequea(e);
                                if (e != null && !Codigo.TIPO_NULL.equals(e.getTipoCalculado().code())) {
                                    correcto = false;
                                    break;
                                }
                            }
                            if(!correcto) {
                                errores.errorSemanticoTipo(n.getFila(),"Los corchetes no deberian contener expresiones");
                            }
                            correcto = comprobarInicalizadorProfYTipo(n.getArrayInit(),tipoAux.getTipoBase(),tipoAux.getCorchetes().getLista().size()-1) && correcto;
                            if(correcto) {
                                n.setTipoCalculado(calculoTipoExtra(tipoAux,n.getExtra()));
                            }
                        }
                    }
                }
                break; }
            case ARGUMENTOS: //Me lo salto en VNuevoClass, ValorMet y VExtraPuntoMet
            case ARRAY_INIT: //No se llama nunca pq uso la funcion de abajo
                throw new IllegalStateException("Argumentos o array init no deben llamarse");
            case BLOQUE_INS: {
                BloqueIns n = (BloqueIns) nodo;
                for(Ins ins : n.getLista()) {
                    chequea(ins); //Las instrucciones no tienen tipocalculado
                }
                break; }
            case VAL_MET: {
                VValorMet n = (VValorMet) nodo;
                Nodo vinculo = n.getObj();
                if(vinculo==null || !Codigo.CLASS.equals(vinculo.code())) {
                    throw new IllegalStateException("Vinculo no correcto");
                }
                Clase claseDeclar = (Clase) vinculo;
                Nodo metodoAux = claseDeclar.getCampos().get(n.getId());
                if(metodoAux==null || !Codigo.MET.equals(metodoAux.code())) {
                    errores.errorSemanticoTipo(n.getFila(),"Clase "+n.getId()+" sin método");
                }
                Met metodo = (Met) metodoAux;
                if(metodo.getParams().getLista().size()!=n.getArgs().getLista().size()) {
                    errores.errorSemanticoTipo(n.getFila(),"El método "+metodo.getNombre()+" no tiene la misma cantidad de parámetros");
                }
                else {
                    List<Param> params = metodo.getParams().getLista();
                    chequea(metodo.getParams()); //los params son los que revisan si es necesario
                    boolean correcto = true;
                    for (int i = 0; i < metodo.getParams().getLista().size(); i++) {
                        chequea(n.getArgs().getLista().get(i));
                        if(params.get(i).getTipoCalculado()!=null && n.getArgs().getLista().get(i).getTipoCalculado()!=null &&
                                !params.get(i).getTipoCalculado().code().equals(n.getArgs().getLista().get(i).getTipoCalculado().code())) {
                            errores.errorSemanticoTipo(n.getFila(),"Los argumentos no tienen el mismo tipo que los parámetros del método");
                            correcto = false;
                        }
                    }
                    if(correcto) {
                        if(metodo.getTipoCalculado()==null) { //metodo es lo primero que hace y evita recursión infinita de chequea si es recursiva
                            chequea(metodo);
                        }
                        n.setTipoCalculado(calculoTipoExtra(metodo.getTipoCalculado(),n.getExtra()));
                    }
                }
                break; }
            //DESIGNADORES
            case VAL: {
                VValor n = (VValor) nodo;
                Nodo declaracion = n.getVinculo();
                //Tuvo que ocurrir antes asi que ya tiene getTipoCalculado
                if(declaracion.getTipoCalculado()==null) { //por si acaso
                    chequea(declaracion);
                }
                n.setTipoCalculado(calculoTipoExtra(declaracion.getTipoCalculado(),n.getExtra()));
                break; }
            case THIS: {
                VThis n = (VThis) nodo;
                Nodo vinculo = n.getObj();
                if(vinculo==null || !Codigo.CLASS.equals(vinculo.code())) {
                    throw new IllegalStateException("Vinculo no correcto");
                }
                Clase claseDeclar = (Clase) vinculo;
                if(claseDeclar.getTipoCalculado()==null) { //tuvo que ocurrir, pero por si acaso
                    chequea(claseDeclar);
                }
                n.setTipoCalculado(calculoTipoExtra(claseDeclar.getTipoCalculado(),n.getExtra()));
                break; }
            case EXTRA_VAL_PUNTO_ATRIB: //No se llama nunca pq uso la funcion de abajo
            case EXTRA_VAL_PUNTO_MET: //No se llama nunca pq uso la funcion de abajo
            case EXTRA_VAL_POS: //No se llama nunca pq uso la funcion de abajo
                break;
            //EXPRESIONES
            case SUMA:
            case RESTA:
            case MUL:
            case DIV:
            case MOD: {
                ExprBin n = (ExprBin) nodo;
                chequea(n.getOp1());
                chequea(n.getOp2());
                Tipo t = operacionAritValid(n.getFila(),n.getOp1().getTipoCalculado(),n.getOp2().getTipoCalculado());
                n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                break; }
            case AND:
            case OR: {
                ExprBin n = (ExprBin) nodo;
                chequea(n.getOp1());
                chequea(n.getOp2());
                Tipo t1 = n.getOp1().getTipoCalculado();
                Tipo t2 = n.getOp2().getTipoCalculado();
                if(t1!=null && t2!=null) {
                    Tipo t = (Codigo.TIPO_BOOLEAN.equals(t1.code()) &&
                            Codigo.TIPO_BOOLEAN.equals(t2.code()))
                            ? new TBoolean() : null;
                    if(t==null) {
                        errores.errorSemanticoTipo(n.getFila(),"Una operacion lógica tiene que ser entre booleanos");
                    }
                    n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                }
                break; }
            case NEG: {
                ExprUni n = (ExprUni) nodo;
                chequea(n.getOp1());
                Tipo t1 = n.getOp1().getTipoCalculado();
                if(t1!=null) {
                    Tipo t = (Codigo.TIPO_BOOLEAN.equals(t1.code()))
                            ? new TBoolean() : null;
                    if(t==null) {
                        errores.errorSemanticoTipo(n.getFila(),"Una operacion lógica tiene que ser entre booleanos");
                    }
                    n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                }
                break; }
            case IGUAL_COMP:
            case DIST_COMP: {
                ExprBin n = (ExprBin) nodo;
                chequea(n.getOp1());
                chequea(n.getOp2());
                Tipo t1 = n.getOp1().getTipoCalculado();
                Tipo t2 = n.getOp2().getTipoCalculado();
                if(t1!=null && t2!=null) {
                    //Esto lo hago distinto que el, admito todos los tipos definidos excepto Void
                    Tipo t = (!Codigo.TIPO_VOID.equals(t1.code()) &&
                            !Codigo.TIPO_VOID.equals(t2.code()))
                            ? new TBoolean() : null;
                    if(t==null) {
                        errores.errorSemanticoTipo(n.getFila(),"Una comparacion no puede ser entre tipos void");
                    }
                    n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                }
                break; }
            case MAYOR:
            case MAYOR_IGUAL:
            case MENOR:
            case MENOR_IGUAL: {
                ExprBin n = (ExprBin) nodo;
                chequea(n.getOp1());
                chequea(n.getOp2());
                Tipo t1 = n.getOp1().getTipoCalculado();
                Tipo t2 = n.getOp2().getTipoCalculado();
                if(t1!=null && t2!=null) {
                    Tipo t = operacionAritValid(n.getFila(),t1,t2);
                    t = (t!=null) ? new TBoolean() : null;
                    n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                }
                break; }
            case MAS_MAS_DER:
            case MENOS_MENOS_DER:
            case MAS_MAS_IZQ:
            case MENOS_MENOS_IZQ:{
                ExprUni n = (ExprUni) nodo;
                chequea(n.getOp1());
                Tipo t = esNumerica(n.getFila(),n.getOp1().getTipoCalculado());
                if(t!=null && !Codigo.VAL.equals(n.getOp1().code())){ //Solo variables
                    errores.errorSemanticoTipo(n.getFila(),"No se puede aplicar un ++ o -- a algo que no es una variable");
                    t = null;
                }
                n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                break; }
            case MAS_IZQ:
            case MENOS_IZQ: {
                ExprUni n = (ExprUni) nodo;
                chequea(n.getOp1());
                Tipo t = esNumerica(n.getFila(),n.getOp1().getTipoCalculado());
                n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                break; }
            case OP_TER: {
                ExprTer n = (ExprTer) nodo;
                chequea(n.getCond());
                chequea(n.getOp1());
                chequea(n.getOp2());
                Tipo cond = n.getCond().getTipoCalculado();
                if(cond!=null && !Codigo.TIPO_BOOLEAN.equals(cond.code())) {
                    errores.errorSemanticoTipo(n.getFila(),"La condición del operador ternario debe ser booleana");
                }
                Tipo op1 = n.getOp1().getTipoCalculado();
                Tipo op2 = n.getOp2().getTipoCalculado();
                Tipo t = null;
                if(op1!=null && op2!=null) {
                    //No tengo claro que pueda pasar
                    if(Codigo.TIPO_VOID.equals(op1.code()) || Codigo.TIPO_VOID.equals(op2.code())) {
                        errores.errorSemanticoTipo(n.getFila(),"El tipo en un op. ternario no puede ser void");
                    }
                    else {
                        if(op1.code().equals(op2.code())) {
                            if(Codigo.TIPO_ARRAY.equals(op1.code())) {
                                if(((TSimpleOArray) op1).getCorchetes().getLista().size() ==
                                        ((TSimpleOArray) op2).getCorchetes().getLista().size()) {
                                    t = op1;
                                }
                                else {
                                    errores.errorSemanticoTipo(n.getFila(),"Tipos distintos (dimensiones de array) en el op. ternario");
                                }
                            }
                            else {
                                t = op1;
                            }
                        }
                        else {
                            errores.errorSemanticoTipo(n.getFila(),"Tipos distintos en el op. ternario");
                        }
                    }
                }
                n.setTipoCalculado(calculoTipoExtra(t,n.getExtra()));
                break; }
            //Literales
            case LIT_INT: {
                Lit n = (Lit) nodo;
                if(n.getExtra()!=null) {
                    errores.errorSemanticoTipo(n.getFila(),"Un literal no puede tener accesos con . o []");
                }
                n.setTipoCalculado(new TInt());
                break; }
            case LIT_DOUBLE: {
                Lit n = (Lit) nodo;
                if(n.getExtra()!=null) {
                    errores.errorSemanticoTipo(n.getFila(),"Un literal no puede tener accesos con . o []");
                }
                n.setTipoCalculado(new TDouble());
                break; }
            case LIT_CHAR: {
                Lit n = (Lit) nodo;
                if(n.getExtra()!=null) {
                    errores.errorSemanticoTipo(n.getFila(),"Un literal no puede tener accesos con . o []");
                }
                n.setTipoCalculado(new TChar());
                break; }
            case LIT_BOOLEAN: {
                Lit n = (Lit) nodo;
                if(n.getExtra()!=null) {
                    errores.errorSemanticoTipo(n.getFila(),"Un literal no puede tener accesos con . o []");
                }
                n.setTipoCalculado(new TBoolean());
                break; }
            case NULL: {
                VNulo n = (VNulo) nodo;
                if(n.getExtra()!=null) {
                    errores.errorSemanticoTipo(n.getFila(),"null no puede tener accesos con . o []");
                }
                n.setTipoCalculado(new TNull());
                break; }
        }
    }

    // Para declar, param y met: hago la comprobación de que la declaracion de un identificador
    // tenga tipo array correcto, ya que no pude hacerlo en CUP
    // y tb que no tenga tipo void
    private void revisarTipos(int fila, Tipo tipo, boolean voidPermitido) {
        switch (tipo.code()) {
            default:
                break;
            case TIPO_BOOLEAN:
            case TIPO_INT:
            case TIPO_DOUBLE:
            case TIPO_CHAR:
            case TIPO_NULL: //este no aparece en el árbol, así que solo será un recurso para la recursividad
            case TIPO_CLASE:
                break;
            case TIPO_VOID:
                if(!voidPermitido) {
                    errores.errorSemanticoTipo(fila,"Variable con tipo void");
                }
                break;
            case TIPO_ARRAY:
                TSimpleOArray n = (TSimpleOArray) tipo;
                for(Expr e : n.getCorchetes().getLista()) {
                    chequea(e);
                    if(e!=null && !Codigo.TIPO_NULL.equals(e.getTipoCalculado().code())) {
                        errores.errorSemanticoTipo(fila,"No puede haber expresiones en los corchetes de un tipo array");
                    }
                }
                Tipo tipoBase = n.getTipoBase();
                if(Codigo.TIPO_VOID.equals(tipoBase.code())) {
                    errores.errorSemanticoTipo(fila,"No es válido void[]");
                }
                break;
        }
    }

    private Tipo operacionAritValid(int fila, Tipo t1, Tipo t2) {
        if(t1==null || t2==null) {
            return null;
        }
        Tipo t = null;
        if(Codigo.TIPO_CHAR.equals(t1.code())) {
            if(Codigo.TIPO_CHAR.equals(t2.code())) {
                t = t2;
            } else if(Codigo.TIPO_INT.equals(t2.code())) {
                t = t2;
            } else if(Codigo.TIPO_DOUBLE.equals(t2.code())) {
                t = t2;
            }
        } else if(Codigo.TIPO_INT.equals(t1.code())) {
            if(Codigo.TIPO_CHAR.equals(t2.code())) {
                t = t1;
            } else if(Codigo.TIPO_INT.equals(t2.code())) {
                t = t2;
            } else if(Codigo.TIPO_DOUBLE.equals(t2.code())) {
                t = t2;
            }
        } else if(Codigo.TIPO_DOUBLE.equals(t1.code())) {
            if(Codigo.TIPO_CHAR.equals(t2.code())) {
                t = t1;
            } else if(Codigo.TIPO_INT.equals(t2.code())) {
                t = t1;
            } else if(Codigo.TIPO_DOUBLE.equals(t2.code())) {
                t = t2;
            }
        }
        if(t==null) {
            errores.errorSemanticoTipo(fila,"Operación aritmética inválida");
        }
        return t;
    }

    private Tipo calculoTipoExtra(Tipo tIzq, ExtraValor extraAux) {
        if(tIzq==null || extraAux==null) {
            return tIzq; //Se asume que ya venía un error o que ya hemos acabado
        }
        if(extraAux.code().equals(Codigo.EXTRA_VAL_POS) &&
                ((VValorExtraArray)extraAux).getCorch().isEmpty()) { //Es un falso extra y no puede tener uno de vd como extra, por como implementamos setExtra
            return tIzq;
        }
        if(!Codigo.TIPO_CLASE.equals(tIzq.code())
            && !Codigo.TIPO_ARRAY.equals(tIzq.code())) {
            errores.errorSemanticoTipo(extraAux.getFila(),"No se puede hacer . o corchete a un tipo básico");
            return null;
        }
        switch (extraAux.code()) {
            default:
                return null;
            case EXTRA_VAL_POS: {
                VValorExtraArray extra = (VValorExtraArray) extraAux;
                extra.setTipoIzq(tIzq);
                if(!Codigo.TIPO_ARRAY.equals(tIzq.code())) {
                    errores.errorSemanticoTipo(extraAux.getFila(),"No se puede acceder con [] a algo que no es array");
                    return null;
                }
                else {
                    TSimpleOArray tipoAux = (TSimpleOArray) tIzq;
                    boolean conNumeros = true;
                    for(Expr e : extra.getCorch().getLista()) {
                        chequea(e);
                        if(!Codigo.TIPO_INT.equals(e.getTipoCalculado().code())) {
                            errores.errorSemanticoTipo(extraAux.getFila(),"Para acceder con [] a un array, debe haber un entero entre los corchetes");
                            conNumeros = false;
                        }
                    }
                    if(!conNumeros) {
                        return null;
                    }
                    int restantes = tipoAux.getCorchetes().getLista().size() - extra.getCorch().getLista().size();
                    if(restantes > 0) {
                        List<Expr> copiaCorch = new ArrayList<>(tipoAux.getCorchetes().getLista());
                        copiaCorch = copiaCorch.subList(copiaCorch.size()-restantes,copiaCorch.size());
                        TSimpleOArray tModif = new TSimpleOArray(tipoAux.getTipoBase(),new Corchetes(copiaCorch));
                        extra.setTipoCalculado(tModif);
                        return calculoTipoExtra(tModif,extra.getExtra());
                    }
                    else if (restantes == 0) {
                        extra.setTipoCalculado(tipoAux.getTipoBase());
                        return calculoTipoExtra(tipoAux.getTipoBase(),extra.getExtra());
                    }
                    else {
                        errores.errorSemanticoTipo(extraAux.getFila(),"Acceso con [] a un tipo no array");
                        return null;
                    }
                }
            }
            case EXTRA_VAL_PUNTO_ATRIB: {
                VValorExtraPuntoAtrib extra = (VValorExtraPuntoAtrib) extraAux;
                if(!Codigo.TIPO_CLASE.equals(tIzq.code())) {
                    errores.errorSemanticoTipo(extraAux.getFila(),"No se puede acceder con . a algo que no es un objeto");
                    return null;
                }
                else {
                    extra.setTipoIzq(tIzq);
                    TClase tipoAux = (TClase) tIzq;
                    Nodo vinculo = tipoAux.getVinculo();
                    if(vinculo==null || !Codigo.CLASS.equals(vinculo.code())) {
                        throw new IllegalStateException("Vinculo no correcto");
                    }
                    Clase claseDeclar = (Clase) vinculo;
                    Nodo atribAux = claseDeclar.getCampos().get(extra.getId().getId());
                    if(atribAux==null || !Codigo.ASIG.equals(atribAux.code())) {
                        errores.errorSemanticoTipo(extraAux.getFila(),"Clase "+extra.getId()+" sin atributo");
                        return null;
                    }
                    IAsig atrib = (IAsig) atribAux;
                    if(atrib.getTipoCalculado()==null) {
                        chequea(atrib);
                    }
                    extra.setTipoCalculado(atrib.getDeclar().getTipoCalculado());
                    return calculoTipoExtra(atrib.getDeclar().getTipoCalculado(),extra.getExtra());
                }
            }
            case EXTRA_VAL_PUNTO_MET: {
                VValorExtraPuntoMetodo extra = (VValorExtraPuntoMetodo) extraAux;
                if(!Codigo.TIPO_CLASE.equals(tIzq.code())) {
                    errores.errorSemanticoTipo(extraAux.getFila(),"No se puede acceder con . a algo que no es un objeto");
                    return null;
                }
                else {
                    extra.setTipoIzq(tIzq);
                    TClase tipoAux = (TClase) tIzq;
                    Nodo vinculo = tipoAux.getVinculo();
                    if(vinculo==null || !Codigo.CLASS.equals(vinculo.code())) {
                        throw new IllegalStateException("Vinculo no correcto");
                    }
                    Clase claseDeclar = (Clase) vinculo;
                    Nodo metodoAux = claseDeclar.getCampos().get(extra.getId().getId());
                    if(metodoAux==null || !Codigo.MET.equals(metodoAux.code())) {
                        errores.errorSemanticoTipo(extraAux.getFila(),"Clase "+extra.getId()+" sin método");
                        return null;
                    }
                    Met metodo = (Met) metodoAux;
                    if(metodo.getParams().getLista().size()!=extra.getArgumentos().getLista().size()) {
                        errores.errorSemanticoTipo(extraAux.getFila(),"El método "+metodo.getNombre()+" no tiene la misma cantidad de parámetros");
                    }
                    else {
                        List<Param> params = metodo.getParams().getLista();
                        chequea(metodo.getParams());
                        for (int i = 0; i < metodo.getParams().getLista().size(); i++) {
                            chequea(extra.getArgumentos().getLista().get(i));
                            if(params.get(i).getTipoCalculado()!=null && extra.getArgumentos().getLista().get(i).getTipoCalculado()!=null &&
                                    !params.get(i).getTipoCalculado().code().equals(extra.getArgumentos().getLista().get(i).getTipoCalculado().code())) {
                                errores.errorSemanticoTipo(extraAux.getFila(),"Los argumentos no tienen el mismo tipo que los parámetros del método");
                            }
                        }
                    }
                    if(metodo.getTipoCalculado()==null) { //metodo es lo primero que hace y evita recursión infinita de chequea si es recursiva
                        chequea(metodo);
                    }
                    extra.setTipoCalculado(metodo.getTipoCalculado());
                    return calculoTipoExtra(metodo.getTipoCalculado(),extra.getExtra());
                }
            }
        }
    }

    private Tipo esNumerica(int fila, Tipo t) {
        if(t==null) {
            return null;
        }
        Tipo tipo = null;
        if(Codigo.TIPO_INT.equals(t.code()) || Codigo.TIPO_CHAR.equals(t.code())
         || Codigo.TIPO_DOUBLE.equals(t.code())) {
            tipo = t;
        }
        if(tipo==null) {
            errores.errorSemanticoTipo(fila,"Variable no numerica");
        }
        return tipo;
    }

    //Devuelve si es correcto
    private boolean comprobarInicalizadorProfYTipo(ArrayInit init, Tipo t, int prof) {
        if(prof==0) {
            if(init.isRecur()) {
                errores.errorSemanticoTipo(init.getFila(),"Demasiada profundidad en el inicializador");
                return false;
            }
            else {
                boolean err = false;
                for(Expr e : init.getLista()) {
                    chequea(e);
                    if(!tiposCompatiblesAsig(t,e.getTipoCalculado())) {
                        errores.errorSemanticoTipo(init.getFila(),"Tipo de array y de elementos del inicializador incompatibles");
                        err = true;
                    }
                }
                return !err;
            }
        }
        else {
            if(init.isRecur()) {
                boolean correcto = true;
                for(ArrayInit i : init.getListaRecur()) {
                    correcto = comprobarInicalizadorProfYTipo(i,t,prof-1) && correcto;
                }
                return correcto;
            }
            else {
                errores.errorSemanticoTipo(init.getFila(),"Poca profundidad en el inicializador");
                return false;
            }
        }
    }

    // El tipo array solo viene si sus corchetes son no vacíos
    private boolean tiposCompatiblesAsig(Tipo t1, Tipo t2) {
        if(t1==null || t2==null) {
            return true;
        }
        if(Codigo.TIPO_CLASE.equals(t1.code()) || Codigo.TIPO_ARRAY.equals(t1.code())) {
            if(Codigo.TIPO_NULL.equals(t2.code())) {
                return true;
            }
        }
        boolean compatibles = false;
        switch (t1.code()) {
            default:
                break;
            case TIPO_ARRAY:
                if(Codigo.TIPO_ARRAY.equals(t2.code())) {
                    TSimpleOArray t1Aux = (TSimpleOArray) t1;
                    TSimpleOArray t2Aux = (TSimpleOArray) t2;
                    if(t1Aux.getCorchetes().getLista().size() ==
                        t2Aux.getCorchetes().getLista().size()) {
                        compatibles = true;
                    }
                }
                break;
            case TIPO_BOOLEAN: {
                if(Codigo.TIPO_BOOLEAN.equals(t2.code())){
                    compatibles = true;
                }
                break; }
            case TIPO_INT: {
                if(Codigo.TIPO_INT.equals(t2.code()) ||
                    Codigo.TIPO_CHAR.equals(t2.code())) {
                    compatibles = true;
                }
                break; }
            case TIPO_DOUBLE: {
                if(Codigo.TIPO_DOUBLE.equals(t2.code()) ||
                    Codigo.TIPO_INT.equals(t2.code()) ||
                    Codigo.TIPO_CHAR.equals(t2.code())) {
                    compatibles = true;
                }
                break; }
            case TIPO_CHAR: {
                if(Codigo.TIPO_CHAR.equals(t2.code())) {
                    compatibles = true;
                }
                break; }
            case TIPO_CLASE: {
                if(Codigo.TIPO_CLASE.equals(t2.code())) {
                    TClase t1Aux = (TClase) t1;
                    TClase t2Aux = (TClase) t2;
                    if(t1Aux.getId().equals(t2Aux.getId())) {
                        compatibles = true;
                    }
                }
                break; }
            case TIPO_VOID: //incompatible
            case TIPO_NULL: { //incompatible, pero no tengo claro que CUP permita null a la izq
                break; }
        }
        return compatibles;
    }
}
