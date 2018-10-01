package tdp.siu;

public class Alumno {
    private String nombre;
    private String padron;
    private String prioridad;

    public Alumno(String nombre, String padron, String prioridad) {
        this.nombre = nombre;
        this.padron = padron;
        this.prioridad = prioridad;
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

}
