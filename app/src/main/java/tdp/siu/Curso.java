package tdp.siu;

public class Curso {

    private String nombreCurso;
    private int numeroCurso;
    private int alumnosInscriptos;
    private int vacantesRestantes;

    public Curso(String nombreCurso, int numeroCurso, int alumnosInscriptos, int vacantesRestantes) {
        this.nombreCurso = nombreCurso;
        this.numeroCurso = numeroCurso;
        this.alumnosInscriptos = alumnosInscriptos;
        this.vacantesRestantes = vacantesRestantes;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public int getNumeroCurso() {
        return numeroCurso;
    }

    public int getAlumnosInscriptos() {
        return alumnosInscriptos;
    }

    public int getVacantesRestantes() {
        return vacantesRestantes;
    }
}
