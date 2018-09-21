package tdp.siu;

public class Alumno {
    private String nombre;
    private int padron;
    private int prioridad;

    public Alumno(String nombre, int padron, int prioridad) {
        this.nombre = nombre;
        this.padron = padron;
        this.prioridad = prioridad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPadron() {
        return padron;
    }

    public int getPrioridad() {
        return prioridad;
    }

}
