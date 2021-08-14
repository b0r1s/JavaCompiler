package asem;

import ast.Expr.Expr;
import ast.Expr.ExprBin;
import ast.Expr.ExprTer;
import ast.Expr.ExprUni;
import ast.Extra.VValorExtraArray;
import ast.Extra.VValorExtraPuntoAtrib;
import ast.Extra.VValorExtraPuntoMetodo;
import ast.General.*;
import ast.Ins.*;
import ast.Nodo;
import ast.Tipo.TClase;
import ast.Tipo.TSimpleOArray;
import ast.Var.*;
import errors.GestionErroresTiny;

import java.util.HashMap;
import java.util.Map;

//Tienen atributo vinculo (a su declaracion): VValor (variable), Declar (si tipo es null)(variable)
//Nótese que no hay nodos ref por no haber alias, aunque se lo metí tb a TClase por si hace falta
//Tienen atributo obj (a la clase en la que están): VThis, VValorMet (no existe este concepto en los apuntes, se llaman mediante this), Met (para revisar en el analisis de tipos si es constructora)
//He añadido por mi cuenta un atributo refMet (al metodo en el que estan) en: Return y ReturnVoid (misma mecanica que con el this, aunque el no lo hace).
//No hay atributo super porque no hay herencia
//Tienen atributo campos: Clase
//Para la generacion de tipos:
//-> Declar ahora tb tiene un atributo que indica si es creacion local o esta en un objeto: esLocal
public class AnalizadorAmbito {

    private final Tabla tabla;
    private final GestionErroresTiny errores;

    public AnalizadorAmbito(Nodo raiz) {
        this.tabla = new Tabla();
        this.errores = new GestionErroresTiny();
        // Recuerda que primero enseñará estos problemas de ambito:
        //   -> Variables declaradas que se vuelven a declarar
        //   -> Variables no declaradas que se usan
        //   -> Uso de this fuera de la clase (creo que el analizador sintactico ya no lo permite)
        //   -> Miembros de una clase con el mismo nombre
        // Y después enseñará si ese Tipo (TClase) existe (es una clase que se llama así)
        // Para ello en vincular(PROG) llamamos a vincular y luego a vincularRef sin cerrar la tabla
        // para ver así las clases que hay
        vincular(raiz);
    }

    public boolean tieneMainCorrecto() {
        return tabla.tieneMainCorrecto();
    }

    public Clase getMain() {
        return tabla.getMain();
    }

    public GestionErroresTiny getErrores() {
        return errores;
    }

    private void vincular(Nodo nodo) {
        if(nodo==null) {
            return;
        }
        if(nodo.getFila()==0) {
            throw new IllegalStateException("Fila no inicializada, revisar");
        }
        switch (nodo.code()){
            //PROGRAMA
            case PROG: {
                Prog n = (Prog) nodo;
                tabla.abreBloque();
                for(Clase c : n.getLista()) {
                    vincular(c);
                }
                //No cerramos la tabla para encontrar los tipos
                for(Clase c : n.getLista()) {
                    vincularRefs(c);
                }
                tabla.cierraBloque();
                break; }

            //DECLARACIONES
            case DECLAR: { //Tb es un designador
                //Siempre está en IAsig
                //Cuando lo usamos para definir atributos se hace manual
                //En el caso Clase
                Declar n = (Declar) nodo;
                if(n.getTtipo()!=null) { //Aqui es cuando es de vd una declaracion
                    vincular(n.getTtipo());
                    if(!tabla.insertaId(n.getId(),n)) {
                        errores.errorSemanticoAmbito(n.getFila(),true,n.getId());
                    }
                }
                else { //aqui es un designador
                    Nodo vinculo = tabla.declaracionDe(n.getId());
                    if(vinculo==null) {
                        errores.errorSemanticoAmbito(n.getFila(),false,n.getId());
                    }
                    else {
                        n.setVinculo(vinculo);
                    }
                    vincular(n.getCorch());
                }
                break; }
            case MET: {
                //La clase nos crea un bloque porque si no chocarian
                //Los atributos de la clase con los params del método
                Met n = (Met) nodo;
                Nodo claseBase = tabla.getNodoClase();
                if(claseBase==null) {
                    errores.errorSemanticoAmbitoErroneo(n.getFila(),"Metodo que no esta en una clase");
                }
                else {
                    n.setObj(claseBase);
                }
                //puede compartir nombre con la clase
                tabla.insertaId(n.getNombre(),n);
                vincular(n.getTtipo()); //El parece no tener tipo en el proc
                vincular(n.getParams()); //El aqui hace ya vinculaRefs
                vincular(n.getBloque());
                break; }
            case PARAMETROS: { //El lo tiene como bucle en proc
                Params n = (Params) nodo;
                for(Param p : n.getLista()) {
                    vincular(p);
                }
                break; }
            case PARAM: { //y esto tb es del bucle en proc
                //Declar y param son básicamente lo mismo
                //Asi que PARAM es declaracion
                Param n = (Param) nodo;
                vincular(n.getTtipo());
                if(!tabla.insertaId(n.getId(),n)) {
                    errores.errorSemanticoAmbito(n.getFila(),true,n.getId());
                }
                break; }

            //TIPOS
            //Tipos básicos: no tienen subvalores ni extras
            case TIPO_INT:
            case TIPO_DOUBLE:
            case TIPO_CHAR:
            case TIPO_BOOLEAN:
            case TIPO_VOID:
            case TIPO_NULL: //Este ni siquiera aparece, es un recurso para el árbol sintáctico
                break;
            //Tipos no básicos
            case TIPO_ARRAY: {
                TSimpleOArray n = (TSimpleOArray) nodo;
                vincular(n.getTipoBase());
                //Es inutil en vd llamar a vincular aqui, pero el lo hace igual
                //no se hace aquí nada con los corchetes
                break; }
            case TIPO_CLASE: {
                break; }
            case CLASS: {
                Clase n = (Clase) nodo;
                //Esto el no lo tenía, porque no considera varias clases en un archivo creo
                if(!tabla.insertaId(n.getNombre(),n)) { //no tenia esto
                    errores.errorSemanticoMiembroDuplic(n.getFila(),n.getNombre());
                }
                tabla.abreBloque(); //ni esto
                //Tal como está, no se permite la sobrecarga de métodos
                Map<String,Nodo> campos = new HashMap<>();
                for(IAsig i : n.getListaAtrib()) {
                    if(campos.containsKey(i.getDeclar().getId())) {
                        errores.errorSemanticoMiembroDuplic(i.getFila(),i.getDeclar().getId());
                    }
                    else {
                        campos.put(i.getDeclar().getId(),i);
                    }
                    i.getDeclar().setLocal(false); //esto solo puedo hacerlo en clase
                    vincular(i);
                }
                for(Met m : n.getListaMet()) {
                    if(campos.containsKey(m.getNombre())) {
                        errores.errorSemanticoMiembroDuplic(m.getFila(),m.getNombre());
                    }
                    else {
                        campos.put(m.getNombre(),m);
                    }
                    tabla.abreBloque(n,m);
                    vincular(m);
                    tabla.cierraBloque();
                }
                n.setCampos(campos);
                tabla.cierraBloque(); //esto, como abrirBloque, el no lo tenia
                break; }

            //INSTRUCCIONES
            case IF: {
                IIf n = (IIf) nodo;
                vincular(n.getCond());
                tabla.abreBloque(); //El no abre estos bloques
                vincular(n.getBloqueIf());
                tabla.cierraBloque();
                tabla.abreBloque();
                vincular(n.getBloqueElse());
                tabla.cierraBloque();
                break; }
            case FOR: {
                IFor n = (IFor) nodo;
                tabla.abreBloque(); //El no abre estos bloques
                vincular(n.getIni());
                vincular(n.getCond());
                vincular(n.getIter());
                vincular(n.getBloque());
                tabla.cierraBloque();
                break; }
            case INS_EXPR: {
                IExpr n = (IExpr) nodo;
                vincular(n.getExpr());
                break; }
            case ASIG: {
                IAsig n = (IAsig) nodo;
                //El lo hace en el orden normal, pero creo que el correcto es este
                vincular(n.getExpr());//importa el orden, declar despues
                vincular(n.getDeclar());
                break; }
            case RETURN: {
                IReturn n = (IReturn) nodo;
                Nodo metBase = tabla.getNodoMetodo();
                if(metBase==null) {
                    errores.errorSemanticoAmbitoErroneo(n.getFila(),"return");
                }
                else {
                    n.setRefMet(metBase);
                }
                vincular(n.getOpnd());
                break; }
            case RETURN_VOID: {
                IReturnVoid n = (IReturnVoid) nodo;
                Nodo metBase = tabla.getNodoMetodo();
                if(metBase==null) {
                    errores.errorSemanticoAmbitoErroneo(n.getFila(),"return");
                }
                else {
                    n.setRefMet(metBase);
                }
                break; }
            case NEW_CLASE: {
                VNuevoClass n = (VNuevoClass) nodo;
                vincular(n.getTtipo());
                vincular(n.getArgumentos());
                vincular(n.getExtra());
                break; }
            case NEW_ARRAY: {
                VNuevoArray n = (VNuevoArray) nodo;
                vincular(n.getTtipo());
                vincular(n.getArrayInit());
                vincular(n.getExtra());
                break;}
            case ARGUMENTOS: { //Los tiene como lista de VAL_MET
                Argumentos n = (Argumentos) nodo;
                for(Expr e: n.getLista()) {
                    vincular(e);
                }
                break; }
            case ARRAY_INIT: {
                ArrayInit n = (ArrayInit) nodo;
                if(n.isRecur()) {
                    for(ArrayInit a: n.getListaRecur()) {
                        vincular(a);
                    }
                }
                else {
                    for(Expr e: n.getLista()) {
                        vincular(e);
                    }
                }
                break; }
            case BLOQUE_INS: {
                BloqueIns n = (BloqueIns) nodo;
                for(Ins ins : n.getLista()) {
                    vincular(ins);
                }
                break; }
            case VAL_MET: { //No es un designador aunque se parezca a this!
                VValorMet n = (VValorMet) nodo;
                //El n.getId() no se puede comprobar aun pues depende de this
                vincular(n.getArgs());
                vincular(n.getExtra());
                Nodo claseBase = tabla.getNodoClase();
                //Esto el no lo tiene pq considera que siempre empiezan por this
                if(claseBase==null) {
                    errores.errorSemanticoAmbitoErroneo(n.getFila(),n.getId());
                }
                else {
                    n.setObj(claseBase);
                }
                break; }

            //DESIGNADORES
            //Variables
            //DECLAR tambien es designador!
            case VAL: {
                VValor n = (VValor) nodo;
                Nodo vinculo = tabla.declaracionDe(n.getId());
                if(vinculo==null) {
                    errores.errorSemanticoAmbito(n.getFila(),false,n.getId());
                }
                else {
                    n.setVinculo(vinculo);
                }
                vincular(n.getExtra());
                break; }
            case THIS: {
                VThis n = (VThis) nodo;
                Nodo claseBase = tabla.getNodoClase();
                if(claseBase==null) {
                    errores.errorSemanticoAmbitoErroneo(n.getFila(),"this");
                }
                else {
                    n.setObj(claseBase);
                }
                vincular(n.getExtra()); //puede haber expr en los corchetes de los extra
                break; }
            //Extras
            case EXTRA_VAL_PUNTO_ATRIB: {
                VValorExtraPuntoAtrib n = (VValorExtraPuntoAtrib) nodo;
                //vincular(n.getId()); este no porque el atributo depende del tipo
                //El tb lo hace asi
                vincular(n.getExtra());
                break; }
            case EXTRA_VAL_PUNTO_MET: {
                VValorExtraPuntoMetodo n = (VValorExtraPuntoMetodo) nodo;
                //vincular(n.getId()); este no porque el metodo depende del tipo
                //El tb lo hace asi
                vincular(n.getArgumentos());
                vincular(n.getExtra());
                break; }
            case EXTRA_VAL_POS: {
                VValorExtraArray n = (VValorExtraArray) nodo;
                for(Expr e : n.getCorch().getLista()) { //Gestiona sus corchetes
                    vincular(e);
                }
                vincular(n.getExtra());
                break; }

            //EXPRESIONES
            //Expresiones binarias:
            case SUMA:
            case RESTA:
            case MUL:
            case DIV:
            case MOD:
            case AND:
            case OR:
            case IGUAL_COMP:
            case DIST_COMP:
            case MAYOR:
            case MAYOR_IGUAL:
            case MENOR:
            case MENOR_IGUAL: {
                ExprBin n = (ExprBin) nodo;
                vincular(n.getOp1());
                vincular(n.getOp2());
                vincular(n.getExtra());
                break;}
            //Expresiones unarias:
            case NEG:
            case MAS_MAS_DER:
            case MENOS_MENOS_DER:
            case MAS_MAS_IZQ:
            case MENOS_MENOS_IZQ:
            case MAS_IZQ:
            case MENOS_IZQ:{
                ExprUni n = (ExprUni) nodo;
                vincular(n.getOp1());
                vincular(n.getExtra());
                break;}
            //Expresiones ternarias:
            case OP_TER: {
                ExprTer n = (ExprTer) nodo;
                vincular(n.getCond());
                vincular(n.getOp1());
                vincular(n.getOp2());
                vincular(n.getExtra());
                break; }
            //Literales: no tienen subvalores ni extras
            case LIT_INT:
            case LIT_DOUBLE:
            case LIT_CHAR:
            case LIT_BOOLEAN:
            case NULL:
                break;
        }
    }

    private void vincularRefs(Nodo nodo) {
        if(nodo==null) {
            return;
        }
        switch (nodo.code()){
            //PROGRAMA
            case PROG:
                break;
            //DECLARACIONES
            case DECLAR: {
                Declar n = (Declar) nodo;
                if(n.getTtipo()!=null) { //Aqui es cuando es de vd una declaracion
                    vincularRefs(n.getTtipo());
                }
                break;}
            case MET: {
                Met n = (Met) nodo;
                vincularRefs(n.getTtipo());//El parece no tener tipo en el proc
                vincularRefs(n.getParams()); //El hace esto en vincular no se por que
                vincularRefs(n.getBloque());
                break; }
            case PARAMETROS: { //Esto lo tiene como un bucle en proc
                Params n = (Params) nodo;
                for(Param p : n.getLista()) {
                    vincularRefs(p);
                }
                break; }
            case PARAM: { //Esto lo coge directamente del bucle de proc
                Param n = (Param) nodo;
                vincularRefs(n.getTtipo());
                break; }
            //TIPOS
            case TIPO_INT:
            case TIPO_DOUBLE:
            case TIPO_CHAR:
            case TIPO_BOOLEAN:
            case TIPO_NULL: //No esta en el arbol, es recurso para analisis tipos
            case TIPO_VOID:
                break;
            case TIPO_ARRAY: {
                TSimpleOArray n = (TSimpleOArray) nodo;
                vincularRefs(n.getTipoBase());
                break; }
            case TIPO_CLASE: {//Este es el importante!
                TClase n = (TClase) nodo;
                Nodo claseDeclaracion = tabla.declaracionDe(n.getId());
                if(claseDeclaracion==null) {
                    errores.errorSemanticoAmbito(n.getFila(),false,n.getId());
                }
                else {
                    n.setVinculo(claseDeclaracion);
                }
                break; }
            case CLASS: {
                Clase n = (Clase) nodo;
                for(IAsig i : n.getListaAtrib()) {
                    vincularRefs(i);
                }
                for(Met m : n.getListaMet()) {
                    vincularRefs(m);
                }
                break; }
            //INSTRUCCIONES
            //El no los hizo con vincularRef pero supongo que hay que hacerlas
            case IF: {
                IIf n = (IIf) nodo;
                vincularRefs(n.getCond());
                vincularRefs(n.getBloqueIf());
                vincularRefs(n.getBloqueElse());
                break; }
            case FOR: {
                IFor n = (IFor) nodo;
                vincularRefs(n.getIni());
                vincularRefs(n.getCond());
                vincularRefs(n.getIter());
                vincularRefs(n.getBloque());
                break; }
            case INS_EXPR: {
                IExpr n = (IExpr) nodo;
                vincularRefs(n.getExpr());
                break; }
            case ASIG: {
                IAsig n = (IAsig) nodo;
                vincularRefs(n.getDeclar());
                vincularRefs(n.getExpr());
                break; }
            case RETURN: {
                IReturn n = (IReturn) nodo;
                vincularRefs(n.getOpnd());
                break; }
            case RETURN_VOID:
                break;
            case NEW_CLASE: {
                VNuevoClass n = (VNuevoClass) nodo;
                vincularRefs(n.getTtipo());
                vincularRefs(n.getArgumentos());
                vincularRefs(n.getExtra());
                break; }
            case NEW_ARRAY: {
                VNuevoArray n = (VNuevoArray) nodo;
                vincularRefs(n.getTtipo());
                vincularRefs(n.getArrayInit());
                vincularRefs(n.getExtra());
                break;}
            case ARGUMENTOS: { //Los tiene como lista de VAL_MET
                Argumentos n = (Argumentos) nodo;
                for(Expr e: n.getLista()) {
                    vincularRefs(e);
                }
                break; }
            case ARRAY_INIT: {
                ArrayInit n = (ArrayInit) nodo;
                if(n.isRecur()) {
                    for(ArrayInit a: n.getListaRecur()) {
                        vincularRefs(a);
                    }
                }
                else {
                    for(Expr e: n.getLista()) {
                        vincularRefs(e);
                    }
                }
                break; }
            case BLOQUE_INS: {
                BloqueIns n = (BloqueIns) nodo;
                for(Ins ins : n.getLista()) {
                    vincularRefs(ins);
                }
                break; }
            case VAL_MET: { //No es un designador aunque se parezca a this!
                VValorMet n = (VValorMet) nodo;
                //El n.getId() no se puede comprobar aun pues depende de this
                vincularRefs(n.getArgs());
                vincularRefs(n.getExtra());
                break; }
            //DESIGNADORES
            //Podria vincularRef los extra pero pa que si ninguno lleva a TClase
            case VAL:
            case THIS:
            case EXTRA_VAL_PUNTO_ATRIB:
            case EXTRA_VAL_PUNTO_MET:
            case EXTRA_VAL_POS:
                break;
            //EXPRESIONES
            case SUMA:
            case RESTA:
            case MUL:
            case DIV:
            case MOD:
            case AND:
            case OR:
            case IGUAL_COMP:
            case DIST_COMP:
            case MAYOR:
            case MAYOR_IGUAL:
            case MENOR:
            case MENOR_IGUAL:
            case NEG:
            case MAS_MAS_DER:
            case MENOS_MENOS_DER:
            case MAS_MAS_IZQ:
            case MENOS_MENOS_IZQ:
            case MAS_IZQ:
            case MENOS_IZQ:
            case OP_TER:
            case LIT_INT:
            case LIT_DOUBLE:
            case LIT_CHAR:
            case LIT_BOOLEAN:
            case NULL:
                break;
        }
    }
}
