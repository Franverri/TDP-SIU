package tdp.siu;

public class Alumno {
    private String nombre;
    private String padron;
    private String prioridad;
    private boolean condicional;

    public Alumno(String nombre, String padron, String prioridad, boolean condicional) {
        this.nombre = nombre;
        this.padron = padron;
        this.prioridad = prioridad;
        this.condicional = condicional;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPadron() {
        return padron;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public boolean esCondicional() {
        return condicional;
    }
}
