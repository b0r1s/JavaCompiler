package gencod;

import ast.Aux.Corchetes;
import ast.Aux.Pair;
import ast.Codigo;
import ast.Expr.*;
import ast.Extra.ExtraValor;
import ast.Extra.VValorExtraArray;
import ast.Extra.VValorExtraPuntoAtrib;
import ast.Extra.VValorExtraPuntoMetodo;
import ast.General.*;
import ast.Ins.*;
import ast.Lit.LBoolean;
import ast.Lit.LChar;
import ast.Lit.LInt;
import ast.Nodo;
import ast.Tipo.TClase;
import ast.Tipo.TSimpleOArray;
import ast.Var.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Tienen atributo dirMem: Declar y Param (declaraciones)
Se usa el viejo atributo vinculo para acceder a ellos
Tienen un atributo tipoIzq los extra, para saber a qué le hago el . o []
 */
public class GeneradorCodigo {

    private List<Linea> ins;

    public GeneradorCodigo(Clase clase, Met main) {
        initMain(clase,main);
        resuelveTags();
        System.out.println(generaCodigoP(true));
        calculaSep(0,ins.size()-1);
    }

    public String generaCodigoP(boolean debug) {
        StringBuilder sb = new StringBuilder();
        int inicioIns =(""+(ins.size()-1)).length()+3;
        for (int i = 0; i < ins.size(); i++) {
            Linea l = ins.get(i);
            sb.append(l.toString(i,inicioIns,debug)).append('\n');
        }
        return sb.toString();
    }

    /*
    Los métodos son ssp (reservas_params_args) sep (cuerpo_metodo) retp/retf/stp (sin ujp)
    Las llamadas a métodos son mst (params) ujp (metodo) cup
     */

    // La idea es calcular el máximo de la pila entre las instrucciones de posicion iniIncl y finIncl
    // Saltándonos las llamadas a un método en el interior, pero teniendo en cuenta lo que apilamos para pasar
    // los parámetros, que descontamos cuando esa función se acaba
    // Se espera que los métodos empiecen por ssp y que acaben por retp/retf/stp
    // Se espera que todas las llamadas a métodos empiecen por mst, tengan un salto ujp a cup y entre ambos un método
    private void calculaSep(int iniIncl, int finIncl) {
        Linea sep = null;
        int pila = 0, maxPila = 0;

        //Para mantener coherencia en una llamada a un método
        List<Pair<Integer,Boolean>> llamadaMet= new ArrayList<>();
        Pair<Integer,Boolean> p = new Pair<>(0,false);
        llamadaMet.add(p);

        for (int i = iniIncl; i <= finIncl; i++) { //Nótese este intervalo
            Linea l = ins.get(i);
            if ("retp".equals(l.getInstruc()) || "retf".equals(l.getInstruc()) ||
                    "stp".equals(l.getInstruc())) {
                //Ha llegado el final de este método
                if(i!=finIncl) {
                    throw new IllegalStateException("Se ha alcanzado el final en medio del código");
                }
                pila += l.getVariacionPila();
                maxPila = Math.max(maxPila, pila);
                if(pila!=0) {
                    throw new IllegalStateException("La pila no ha acabado con el tamaño esperado");
                }
                if(sep==null) {
                    throw new IllegalStateException("No había sep en este método");
                }
                sep.addParam(maxPila);
            }
            else if ("sep".equals(l.getInstruc())) { //No varía el SP
                sep = l;
            } else if ("mst".equals(l.getInstruc())) {
                pila += l.getVariacionPila();
                maxPila = Math.max(maxPila, pila);
                //Vamos a contar lo que añadimos para la llamada al metodo
                //Podemos estar en una llamada a método en una llamada a método
                if(p.second) { //estabamos apilando ya, asi que este será un salto que será un param
                    p.setFirst(p.first+1);
                }
                p = new Pair<>(5,true);
                llamadaMet.add(p);
            } else if(p.second && "ujp".equals(l.getInstruc())) {
                int fin = Integer.parseInt(l.getParams().get(0)); //su parametro es la linea de cup
                calculaSep(i+1,fin-1); //desde el siguiente hasta cup
                i = fin; //Hemos saltado el cuerpo del método y cup, que no varía SP

                //Pero la línea anterior a cup era un final:
                //retp y retf quitan las variables locales (las cuenta el metodo saltado)
                //y los parametros del metodo (que ocupan tamPilaParms y las cuenta el método que llama => nos importa)
                pila -= p.first;
                pila += "retf".equals(ins.get(fin-1).getInstruc()) ? 1 : 0;
                maxPila = Math.max(maxPila, pila); //innecesario pero por mantener el estilo

                llamadaMet.remove(llamadaMet.size()-1);
                p = llamadaMet.get(llamadaMet.size()-1);
            } else {
                pila += l.getVariacionPila();
                maxPila = Math.max(maxPila, pila);
                if(p.second) {
                    p.setFirst(p.first + l.getVariacionPila());
                }
            }
        }
    }

    private void resuelveTags() {
        //Quitamos las lineas que solo tienen etiqueta,
        //Moviendo las etiquetas a la primera instruccion siguiente
        List<String> tagsAcumuladas = new ArrayList<>();
        List<Integer> posAEliminar = new ArrayList<>();
        for (int i = 0; i < ins.size(); i++) {
            Linea l = ins.get(i);
            if("".equals(l.getInstruc())) {
                posAEliminar.add(i);
                tagsAcumuladas.addAll(l.getTags());
            }
            else {
                l.getTags().addAll(tagsAcumuladas);
                tagsAcumuladas = new ArrayList<>();
            }
        }

        //Borramos esas lineas que ahora sobran
        for (int i = posAEliminar.size()-1; i >= 0 ; i--) {
            ins.remove(posAEliminar.get(i).intValue());
        }

        //Anotamos en que linea esta cada referencia
        Map<String, Integer> refs = new HashMap<>();
        for(int i=0; i<ins.size(); i++) {
            Linea l = ins.get(i);
            for(String tag : l.getTags()) {
                refs.put(tag,i);
            }
        }

        //Cambiamos tags por lineas usando el mapa
        for (Linea l : ins) {
            for (int j = 0; j < l.getParams().size(); j++) {
                if ('?' == l.getParams().get(j).charAt(0)) {
                    int linea = refs.get(l.getParams().get(j));
                    l.getParams().remove(j);
                    l.getParams().add(j,"" + linea);
                }
            }
        }
    }

    public List<Linea> codeR(Nodo nodo) { //Genera código para expresiones
        List<Linea> result = new ArrayList<>();
        if(nodo==null) {
            return result;
        }
        switch (nodo.code()) {
            default:
                throw new IllegalStateException("No se debería haber llamado a este nodo");
            case VAL_MET: {
                VValorMet n = (VValorMet) nodo;
                result.add(new Linea(5,"mst",0)); //siempre son 5
                result.add(new Linea(1,"lod",0,5)); //pasamos la variable this
                result.addAll(codeR(n.getArgs()));
                Linea l1 = new Linea(0,"ujp"); result.add(l1);
                Linea l2 = new Linea(); result.add(l2);
                Met met = (Met) ((Clase) n.getObj()).getCampos().get(n.getId());
                result.addAll(codeI(met)); //esto bajara -6+args la pila pq es void, si no -5+args, quedando como estabamos pre llamada
                Linea l3 = new Linea(0,"cup",getSizeParams(met)); result.add(l3);
                l1.addTagNoResuelta(l3.getFirstTag()); //ujp nos lleva a cup
                l3.addTagNoResuelta(l2.getFirstTag()); //cup nos lleva a la siguiente a ujp (sera un ssp)
                break; }
            case VAL: {
                VValor n = (VValor) nodo;
                result.addAll(preparacionExtras(n.getExtra()));
                result.addAll(codeL(nodo)); //Cojo su dirección (puede ser objeto o var local)
                if(n.getExtra()==null) {
                    if(!Codigo.TIPO_CLASE.equals(n.getTipoCalculado().code()) &&
                            !Codigo.TIPO_ARRAY.equals(n.getTipoCalculado().code())) {
                        result.add(new Linea(0,"ind"));
                    }
                }
                else {
                    if(Codigo.TIPO_CLASE.equals(n.getExtra().getTipoIzq().code())) {
                        result.add(new Linea(0,"ind"));
                    }
                    result.addAll(codeR(n.getExtra())); //puede darme dir de objeto o valor, dependiendo del tipo
                }
                break; }
            case THIS: {
                VThis n = (VThis) nodo;
                result.addAll(preparacionExtras(n.getExtra()));
                result.addAll(codeL(nodo)); //Cojo su dirección (puede ser objeto o var local)
                if(n.getExtra()==null) {
                    if(!Codigo.TIPO_CLASE.equals(n.getTipoCalculado().code()) &&
                            !Codigo.TIPO_ARRAY.equals(n.getTipoCalculado().code())) {
                        result.add(new Linea(0,"ind"));
                    }
                }
                else {
                    result.addAll(codeR(n.getExtra())); //puede darme dir de objeto o valor, dependiendo del tipo
                }
                break; }
            //Los extras se llaman originalmente por this o por val
            case EXTRA_VAL_PUNTO_ATRIB: {
                //Ya estara la direccion del objeto en la pila, asi que solo tengo que desplazarme
                VValorExtraPuntoAtrib n = (VValorExtraPuntoAtrib) nodo;
                result.addAll(codeL(nodo));

                if(!Codigo.TIPO_CLASE.equals(n.getTipoCalculado().code()) &&
                        !Codigo.TIPO_ARRAY.equals(n.getTipoCalculado().code())) {
                    result.add(new Linea(0,"ind"));
                }
                break; }
            case EXTRA_VAL_PUNTO_MET: {
                result.addAll(codeL(nodo));
                break; }
            case EXTRA_VAL_POS: {
                VValorExtraArray n = (VValorExtraArray) nodo;
                result.addAll(codeL(nodo));

                if(!Codigo.TIPO_CLASE.equals(n.getTipoCalculado().code()) &&
                        !Codigo.TIPO_ARRAY.equals(n.getTipoCalculado().code())) {
                    result.add(new Linea(0,"ind"));
                }
                break; }
            case ARGUMENTOS: {
                Argumentos n = (Argumentos) nodo;
                for(Expr e : n.getLista()) {
                    result.addAll(codeR(e));
                }
                break; }
            case SUMA: {
                ESuma n = (ESuma) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"add"));
                break; }
            case RESTA:{
                EResta n = (EResta) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"sub"));
                break; }
            case MUL:{
                EMul n = (EMul) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"mul"));
                break; }
            case DIV:{
                EDiv n = (EDiv) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"div"));
                break; }
            case AND:{
                EAnd n = (EAnd) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"and"));
                break; }
            case OR:{
                EOr n = (EOr) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"or"));
                break; }
            case IGUAL_COMP:{
                EIgualComp n = (EIgualComp) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"equ"));
                break; }
            case DIST_COMP:{
                EDistComp n = (EDistComp) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"neq"));
                break; }
            case MAYOR:{
                EMayor n = (EMayor) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"grt"));
                break; }
            case MAYOR_IGUAL:{
                EMayorIgual n = (EMayorIgual) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"geq"));
                break; }
            case MENOR: {
                EMenor n = (EMenor) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"les"));
                break; }
            case MENOR_IGUAL:{
                EMenorIgual n = (EMenorIgual) nodo;
                result.addAll(codeR(n.getOp1()));
                result.addAll(codeR(n.getOp2()));
                result.add(new Linea(-1,"leq"));
                break; }
            case NEG: {
                ENegado n = (ENegado) nodo;
                result.addAll(codeR(n.getOp1()));
                result.add(new Linea(0,"not"));
                break; }
            case MAS_MAS_DER: {
                EMasMasDer n = (EMasMasDer) nodo;
                result.addAll(codeL(n.getOp1())); //es de tipo VAR
                result.add(new Linea(0,"ind"));//valor de la variable
                result.addAll(codeL(n.getOp1()));
                result.addAll(codeL(n.getOp1()));
                result.add(new Linea(0,"ind"));//valor de la variable
                result.add(new Linea(0,"inc",1));
                result.add(new Linea(-2,"sto")); //guardamos valor en dir
                break; }
            case MENOS_MENOS_DER: {
                EMenosMenosDer n = (EMenosMenosDer) nodo;
                result.addAll(codeL(n.getOp1()));
                result.add(new Linea(0,"ind"));
                result.addAll(codeL(n.getOp1()));
                result.addAll(codeL(n.getOp1()));
                result.add(new Linea(0,"ind"));
                result.add(new Linea(0,"dec",1));
                result.add(new Linea(-2,"sto"));
                break; }
            case MAS_MAS_IZQ:{
                EMasMasIzq n = (EMasMasIzq) nodo;
                result.addAll(codeL(n.getOp1()));
                result.addAll(codeL(n.getOp1()));
                result.add(new Linea(0,"ind"));
                result.add(new Linea(0,"inc",1));
                result.add(new Linea(-2,"sto"));
                result.addAll(codeL(n.getOp1()));
                result.add(new Linea(0,"ind"));
                break; }
            case MENOS_MENOS_IZQ:{
                EMenosMenosIzq n = (EMenosMenosIzq) nodo;
                result.addAll(codeL(n.getOp1()));
                result.addAll(codeL(n.getOp1()));
                result.add(new Linea(0,"ind"));
                result.add(new Linea(0,"dec",1));
                result.add(new Linea(-2,"sto"));
                result.addAll(codeL(n.getOp1()));
                result.add(new Linea(0,"ind"));
                break; }
            case MAS_IZQ: {
                EMasIzq n = (EMasIzq) nodo;
                result.addAll(codeR(n.getOp1()));
                break; }
            case MENOS_IZQ: {
                EMenosIzq n = (EMenosIzq) nodo;
                result.addAll(codeR(n.getOp1()));
                result.add(new Linea(0,"neg"));
                break; }
            case OP_TER: {
                EOpTernario n = (EOpTernario) nodo;
                result.addAll(codeR(n.getCond()));
                Linea l1 = new Linea(-1,"fjp"); result.add(l1);
                result.addAll(codeR(n.getOp1())); //Me da ya el numero
                Linea l2 = new Linea(-1,"ujp"); result.add(l2); //para cuadrar que solo va a haber 1
                Linea l3 = new Linea(); result.add(l3);
                result.addAll(codeR(n.getOp2()));
                Linea l4 = new Linea(); result.add(l4);

                l1.addTagNoResuelta(l3.getFirstTag()); //fjp -> después de ujp
                l2.addTagNoResuelta(l4.getFirstTag()); //ujp -> al final
                break; }
            case LIT_INT: {
                LInt n = (LInt) nodo;
                result.add(new Linea(1,"ldc",Integer.parseInt(n.getValor())));
                break; }
            case LIT_DOUBLE: {
                LInt n = (LInt) nodo;
                result.add(new Linea(1,"ldc", (int) Math.floor(Double.parseDouble(n.getValor()))));
                break; }
            case LIT_BOOLEAN: {
                LBoolean n = (LBoolean) nodo;
                result.add(new Linea(1,"ldc",Boolean.parseBoolean(n.getValor())));
                break; }
            case LIT_CHAR:{
                LChar n = (LChar) nodo;
                result.add(new Linea(1,"ldc",n.getValor().charAt(1))); //El string contiene las comillas
                break; }
        }
        return result;
    }

    public List<Linea> codeL(Nodo nodo) {
        List<Linea> result = new ArrayList<>();
        if(nodo==null) {
            return result;
        }
        switch (nodo.code()) {
            default:
                throw new IllegalStateException("No se deberia haber llamado a este nodo");
            case DECLAR: { //Solo está pensado para IAsig dentro de método (variable local, no atributo)
                Declar n = (Declar) nodo;
                if(n.getTtipo()!=null) { //Declar
                    if(n.esLocal()) { //Variable local
                        result.add(new Linea(1,"lda",0,n.getDirMem()));
                    }
                    else { //Atributo this.x
                        result.add(new Linea(1,"lda",0,5)); //Pone la dir la casilla donde está la dir de this
                        result.add(new Linea(0,"ind")); //Pone la dir de this
                        result.add(new Linea(0,"inc",n.getDirMem()));
                    }
                }
                else {
                    result.addAll(codeL(n.getVinculo()));
                    result.addAll(codeL(n.getCorch()));
                }
                break; }
            case PARAM: {
                Param n = (Param) nodo;
                result.add(new Linea(1,"lda",0,n.getDirMem()));
                break; }
            case VAL: { //Lo llaman los ++ y --
                //3 opciones: parametro, var local o atributo
                VValor n = (VValor) nodo;
                result.addAll(codeL(n.getVinculo())); //3 opciones: parametro, var local o atributo
                break; }
            case THIS: {
                VThis n = (VThis) nodo;
                result.add(new Linea(1,"lda",0,5));
                result.add(new Linea(0,"ind"));
                break; }
            case EXTRA_VAL_PUNTO_ATRIB: {
                //Ya estara la direccion del objeto en la pila, asi que solo tengo que desplazarme
                VValorExtraPuntoAtrib n = (VValorExtraPuntoAtrib) nodo;
                TClase tipo = (TClase) n.getTipoIzq();
                IAsig atrib = (IAsig) ((Clase)tipo.getVinculo()).getCampos().get(n.getId().getId());
                result.add(new Linea(0,"inc",atrib.getDeclar().getDirMem()));

                result.addAll(codeL(n.getExtra()));
                break; }
            case EXTRA_VAL_PUNTO_MET: {
                VValorExtraPuntoMetodo n = (VValorExtraPuntoMetodo) nodo;
                TClase tipo = (TClase) n.getTipoIzq();
                //result.add(new Linea(5,"mst",0)); //Esto lo hago en la preparacion
                //result.add(new Linea(1,"lod",0,5)); //El this es la dir que está en la cima de la pila
                result.addAll(codeR(n.getArgumentos()));
                Linea l1 = new Linea(0,"ujp"); result.add(l1);
                Linea l2 = new Linea(); result.add(l2);
                Met met = (Met) ((Clase)tipo.getVinculo()).getCampos().get(n.getId().getId());
                result.addAll(codeI(met)); //esto bajara -6+args la pila pq es void, si no -5+args, quedando como estabamos pre llamada
                Linea l3 = new Linea(0,"cup",getSizeParams(met)); result.add(l3);
                l1.addTagNoResuelta(l3.getFirstTag()); //ujp nos lleva a cup
                l3.addTagNoResuelta(l2.getFirstTag()); //cup nos lleva a la siguiente a ujp (sera un ssp)

                result.addAll(codeL(n.getExtra()));
                break; }
            case EXTRA_VAL_POS: {
                VValorExtraArray n = (VValorExtraArray) nodo;
                result.add(new Linea(0,"ind")); //Ahora tengo la dir del descriptor
                result.add(new Linea(-1,"str",0,0)); //La guardo en MP
                result.add(new Linea(1,"lod",0,0)); //Y la vuelvo a cargar
                result.add(new Linea(0,"ind")); //Esta en el inicio del array

                //Calculamos el salto
                result.addAll(codeR(n.getCorch().getLista().get(0))); //i1
                for (int i = 1; i < n.getCorch().getLista().size(); i++) {
                    result.add(new Linea(1,"lod",0,0));
                    result.add(new Linea(0,"inc",i+1));
                    result.add(new Linea(0,"ind")); //di

                    result.add(new Linea(-1,"mul"));
                    result.addAll(codeR(n.getCorch().getLista().get(i))); //i2...
                    result.add(new Linea(-1,"add"));
                }

                result.add(new Linea(-1,"add"));
                result.addAll(codeL(n.getExtra()));
                break; }
        }


        return result;
    }

    private List<Linea> preparacionExtras(ExtraValor extra) {
        if(extra==null) {
            return new ArrayList<>();
        }
        List<Linea> recurFinal = preparacionExtras(extra.getExtra());
        if(Codigo.EXTRA_VAL_PUNTO_MET.equals(extra.code())) {
            recurFinal.add(new Linea(5,"mst",0));
        }
        return recurFinal;
    }

    private void initMain(Clase clase, Met main) {
        ins = new ArrayList<>();
        ins.add(new Linea(6,"ssp",6)); //5 marco y 1 param
        ins.add(new Linea(1,"ldc",5)); //guardara en casilla 5 this
        ins.add(new Linea(1,"ldc",getSize(clase)));
        ins.add(new Linea(-2,"new"));
        ins.addAll(codeI(clase));
        ins.add(new Linea(0,"sep"));
        ins.add(new Linea(5,"mst",0)); //siempre son 5
        ins.add(new Linea(1,"lod",0,5)); //pasamos la variable this
        Linea l1 = new Linea(0,"ujp"); ins.add(l1);
        Linea l2 = new Linea(); ins.add(l2);
        ins.addAll(codeI(main)); //esto bajara -6 la pila pq es void, si no -5, quedando como estabamos pre llamada
        Linea l3 = new Linea(0,"cup",1); ins.add(l3);
        l1.addTagNoResuelta(l3.getFirstTag()); //ujp nos lleva a cup
        l3.addTagNoResuelta(l2.getFirstTag()); //cup nos lleva a la siguiente a ujp (sera un ssp)
        ins.add(new Linea(-6,"stp")); //no retp por implementacion de maquina-P
    }

    public List<Linea> codeI(Nodo nodo) {
        List<Linea> result = new ArrayList<>();
        if(nodo==null) {
            return result;
        }
        switch (nodo.code()){
            default:
                throw new IllegalStateException("No se deberia haber llamado a este nodo");
            //Los objetos y arrays se pasan por referencia, los tipos básicos por valor
            case MET: {
                Met n = (Met) nodo;
                int sParams = getSizeParams(n)+5;
                int sVars = getSizeVars(n,sParams);
                int size = sParams + sVars;
                result.add(new Linea(sVars,"ssp",size));
                //No se llama a los parámetros pues ya están introducidos
                result.add(new Linea(0,"sep"));
                List<Linea> codBloque = codeI(n.getBloque()); result.addAll(codBloque);
                Linea l1 = new Linea(); result.add(l1);
                for (Linea l : codBloque) { //Sustituimos los tags de los return
                    for (int j = 0; j < l.getParams().size(); j++) {
                        if ("?f".equals(l.getParams().get(j))) {
                            l.getParams().remove(j);
                            l.getParams().add(j,l1.getFirstTag());
                        }
                    }
                }
                if(n.getTtipo()==null || Codigo.TIPO_VOID.equals(n.getTtipo().code())) {
                    result.add(new Linea(-sVars,"retp")); //En realidad es -size
                }
                else { //Notese que solo deja espacio para 1
                    result.add(new Linea(-sVars,"retf")); //En realidad es -size
                }
                //Puse -getSizeVars para que en calculaSep cuadre con la recursion,
                //De forma que es el metodo llamante el que tiene en cuenta alli que retp o retf tb
                //Quitan los parametros de llamada
                break; }
            case CLASS: {
                Clase n = (Clase) nodo;
                for(IAsig iAsig : n.getListaAtrib()) {
                    result.addAll(codeI(iAsig));
                }
                break; }
            case IF: {
                IIf n = (IIf) nodo;
                if(n.getBloqueElse()!=null) {
                    result.addAll(codeR(n.getCond()));
                    Linea l1 = new Linea(-1,"fjp"); result.add(l1);
                    result.addAll(codeI(n.getBloqueIf()));
                    Linea l2 = new Linea(0,"ujp"); result.add(l2);
                    Linea l3 = new Linea(); result.add(l3);
                    result.addAll(codeI(n.getBloqueElse()));
                    Linea l4 = new Linea(); result.add(l4);

                    l1.addTagNoResuelta(l3.getFirstTag()); //fjp -> después de ujp
                    l2.addTagNoResuelta(l4.getFirstTag()); //ujp -> al final
                }
                else {
                    result.addAll(codeR(n.getCond()));
                    Linea l1 = new Linea(-1,"fjp"); result.add(l1);
                    result.addAll(codeI(n.getBloqueIf()));
                    Linea l2 = new Linea(); result.add(l2);

                    l1.addTagNoResuelta(l2.getFirstTag()); //fjp -> al final
                }
                break; }
            case FOR: {
                IFor n = (IFor) nodo;
                result.addAll(codeI(n.getIni())); //Previo a la mecánica while
                Linea l1 = new Linea(); result.add(l1);
                result.addAll(codeR(n.getCond()));
                Linea l2 = new Linea(-1,"fjp"); result.add(l2);
                result.addAll(codeI(n.getBloque()));
                result.addAll(codeI(n.getIter()));
                Linea l3 = new Linea(0,"ujp"); result.add(l3);
                Linea l4 = new Linea(); result.add(l4);

                l2.addTagNoResuelta(l4.getFirstTag()); //fjp -> al final
                l3.addTagNoResuelta(l1.getFirstTag()); //ujp -> antes de la condición
                break; }
            case INS_EXPR: {
                IExpr n = (IExpr) nodo;
                if(!Codigo.TIPO_VOID.equals(n.getExpr().getTipoCalculado().code())) {
                    //Hay un elemento en la pila que nadie recoge
                    //Para quitarlo hago trampa y lo guardo en la dir de return !
                    result.add(new Linea(1,"lda",0,0));
                    result.addAll(codeR(n.getExpr()));
                    result.add(new Linea(-2,"sto"));
                }
                else {
                    result.addAll(codeR(n.getExpr()));
                }
                break; }
            case ASIG: {
                IAsig n = (IAsig) nodo;
                if(n.getExpr()!=null)  { //Una declaracion por si sola dejaría la pila con 1 elem de más y no haría nada
                    if(Codigo.NEW_CLASE.equals(n.getExpr().code())) { //NEW_CLASE
                        VNuevoClass m = (VNuevoClass) n.getExpr();
                        TClase tipo = (TClase) m.getTtipo();
                        Clase clase = (Clase) tipo.getVinculo();

                        result.addAll(codeL(n.getDeclar())); //direccion
                        result.add(new Linea(1,"ldc",getSize(clase)));
                        result.add(new Linea(-2,"new"));

                        result.add(new Linea(5,"mst",0)); //siempre son 5
                        result.addAll(codeL(n.getDeclar()));
                        result.add(new Linea(0,"ind")); //nuevo this
                        result.addAll(codeR(m.getArgumentos()));
                        Linea l1 = new Linea(0,"ujp"); result.add(l1);
                        Linea l2 = new Linea(); result.add(l2);
                        result.addAll(codeI(clase)); //inicializa atributos
                        Met met = (Met) clase.getCampos().get(clase.getNombre());
                        result.addAll(codeI(met)); //esto bajara -6+args la pila pq es void, si no -5+args, quedando como estabamos pre llamada
                        Linea l3 = new Linea(0,"cup",getSizeParams(met)); result.add(l3);
                        l1.addTagNoResuelta(l3.getFirstTag()); //ujp nos lleva a cup
                        l3.addTagNoResuelta(l2.getFirstTag()); //cup nos lleva a la siguiente a ujp (sera un ssp)
                    }
                    else if(Codigo.NEW_ARRAY.equals(n.getExpr().code())) {
                        VNuevoArray m = (VNuevoArray) n.getExpr();
                        Corchetes corch = ((TSimpleOArray)m.getTtipo()).getCorchetes();

                        result.addAll(codeL(n.getDeclar()));
                        int size = 2 + corch.getLista().size()-1; //cabecera
                        result.add(new Linea(1,"ldc",size));
                        result.add(new Linea(-2,"new"));

                        result.addAll(codeL(n.getDeclar()));
                        result.add(new Linea(0,"ind"));
                        result.add(new Linea(0,"inc",1));
                        List<Expr> lista = corch.getLista();
                        result.addAll(codeR(lista.get(0))); //va a ser la primera de las multiplicaciones
                        for (int i = 1; i < lista.size(); i++) {
                            Expr e = lista.get(i);
                            result.addAll(codeR(e));
                            result.add(new Linea(-1,"mul"));

                            result.addAll(codeL(n.getDeclar()));
                            result.add(new Linea(0,"ind"));
                            result.add(new Linea(0,"inc",1+i));
                            result.addAll(codeR(e));
                            result.add(new Linea(-2,"sto"));
                        }
                        result.add(new Linea(-2,"sto"));

                        result.addAll(codeL(n.getDeclar()));
                        result.add(new Linea(0,"ind"));
                        result.addAll(codeL(n.getDeclar()));
                        result.add(new Linea(0,"ind"));
                        result.add(new Linea(0,"inc",1));
                        result.add(new Linea(0,"ind"));
                        result.add(new Linea(-2,"new"));
                    }
                    else {
                        result.addAll(codeL(n.getDeclar())); //declar es !=null porque tengo INS_EXPR para casos sin Declar
                        result.addAll(codeR(n.getExpr()));
                        result.add(new Linea(-2,"sto"));
                    }

                }
                break; }
            case RETURN: {
                IReturn n = (IReturn) nodo;
                result.add(new Linea(1,"lda",0,0));
                result.addAll(codeR(n.getOpnd()));
                result.add(new Linea(-2,"sto"));

                Linea l1 = new Linea(0,"ujp"); result.add(l1);
                l1.addTagNoResuelta("?f");
                break; }
            case RETURN_VOID: {
                Linea l1 = new Linea(0,"ujp"); result.add(l1);
                l1.addTagNoResuelta("?f");
                break; }
            case BLOQUE_INS: {
                BloqueIns n = (BloqueIns) nodo;
                for(Ins i : n.getLista()) {
                    result.addAll(codeI(i));
                }
                break; }
        }
        return result;
    }

    private int getSizeParams(Met metodo) {
        //Size te dice la 1ª dir sin asignar
        int size = 1; //fake parameter this
        //Parámetros método
        //tb es 1 si es TClase, seria la direccion al obj
        //Tb es 1 si es array, punt al descriptor, que tiene: dirMem + arraySize + dimensions
        for (Param p : metodo.getParams().getLista()) {
            p.setDirMem(5 + size);
            size += 1;
        }
        return size;
    }

    //Te dice cuanto mover el sp en la llamada a funcion
    private int getSizeVars(Met metodo, int offset) {
        int size = 0;
        //Variables locales
        List<Ins> instruc = new ArrayList<>();
        instruc.addAll(metodo.getBloque().getLista());
        for(int j=0; j<instruc.size(); j++) {
            Ins i = instruc.get(j);
            if(Codigo.ASIG.equals(i.code())) {
                Declar d = ((IAsig) i).getDeclar();
                if(d.getTtipo()!=null) { //Declaracion
                    d.setDirMem(offset + size);
                    size += 1;
                }
            }
            else if(Codigo.FOR.equals(i.code())) {
                IFor n = (IFor) i;
                instruc.add(n.getIni());
                instruc.addAll(n.getBloque().getLista());
                instruc.add(n.getIter());
            }
            else if(Codigo.IF.equals(i.code())) {
                IIf n = (IIf) i;
                instruc.addAll(n.getBloqueIf().getLista());
                if(n.getBloqueElse()!=null) {
                    instruc.addAll(n.getBloqueElse().getLista());
                }
            }
        }
        //Ambos pueden contener casos de arrays dinamicos
        return size;
    }

    //Te dice el tam a reservar para el new
    private int getSize(Clase clase) {
        //Size el desplazamiento desde la dir
        int size = 0;
        for(IAsig i : clase.getListaAtrib()) {
            Declar d = i.getDeclar();
            d.setDirMem(size);
            size += 1;
        }
        return size;
    }
}
