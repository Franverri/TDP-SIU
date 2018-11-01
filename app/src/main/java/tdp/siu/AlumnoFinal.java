package tdp.siu;

public class AlumnoFinal {
    private String nombre;
    private String padron;
    private int nota;
    private boolean regular; //true es regular, false es libre

    public AlumnoFinal(String nombre, String padron, int nota, boolean regular){
        this.nombre = nombre;
        this.padron = padron;
        this.nota = nota;
        this.regular = regular;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPadron() {
        return padron;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public boolean isRegular() {
        return regular;
    }
}
