package tdp.siu;

public class Curso {

    private String nombreCurso;
    private String codigoCurso;
    private int idCurso;
    private int numeroCurso;
    private int alumnosInscriptos;
    private int vacantesRestantes;

    public Curso(String nombreCurso, String codigoCurso, int idCurso,int numeroCurso, int alumnosInscriptos, int vacantesRestantes) {
        this.nombreCurso = nombreCurso;
        this.codigoCurso = codigoCurso;
        this.idCurso = idCurso;
        this.numeroCurso = numeroCurso;
        this.alumnosInscriptos = alumnosInscriptos;
        this.vacantesRestantes = vacantesRestantes;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public String getCodigoCurso() { return codigoCurso; }

    public int getIdCurso() { return idCurso; }

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
