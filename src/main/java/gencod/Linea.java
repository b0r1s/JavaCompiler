package gencod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Linea {

    private final List<String> tag;

    private String instruc;
    private int variacionPila;

    private final List<String> params;

    public Linea() {
        this.tag = new ArrayList<>();
        this.tag.add("?"+genTag.getTag());
        this.instruc = "";
        this.variacionPila = 0;
        this.params = new ArrayList<>();
    }

    public Linea(int variacionPila, String instruc, int ... args) {
        this(variacionPila, instruc);
        for(int a : args) {
            addParam(a);
        }
    }

    public Linea(int variacionPila, String instruc, boolean ... args) {
        this(variacionPila,instruc);
        for(boolean a : args) {
            addParam(a);
        }
    }

    public Linea(int variacionPila, String instruc) {
        this();
        this.instruc = instruc;
        this.variacionPila = variacionPila;
    }

    public List<String> getTags() {
        return tag;
    }

    public int getVariacionPila() {
        return variacionPila;
    }

    public String getFirstTag() {return tag.get(0);}

    public String getInstruc() {
        return instruc;
    }

    public void addParam(int x) {
        params.add(""+x);
    }

    public void addParam(boolean x) { params.add(""+x);}

    public void addTagNoResuelta(String tag) {
        params.add(tag);
    }

    public List<String> getParams() {
        return params;
    }

    public String toString(int linea, int iniInstruc, boolean debug) {
        String l = "{"+linea+"}";
        int espacios = iniInstruc - l.length();
        String p = String.join(" ", params);
        return l + String.join("", Collections.nCopies(espacios, " "))
                + instruc + " " + p + ';' + (debug ? " {"+variacionPila+"}" : "");
    }

    private static final GenTag genTag = new GenTag();

    private static class GenTag {
        private int numTag = 0;

        public int getTag() {
            return numTag++;
        }
    }
}
