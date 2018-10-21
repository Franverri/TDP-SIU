package tdp.siu;

public class Inscripcion {

    private String idCurso;
    private String nombreMateria;
    private String codigoMateria;
    private String nombreCatedra;
    private String horario;

    public Inscripcion(String idCurso, String nombreMateria, String codigoMateria, String nombreCatedra, String horario) {
        this.idCurso = idCurso;
        this.nombreMateria = nombreMateria;
        this.codigoMateria = codigoMateria;
        this.nombreCatedra = nombreCatedra;
        this.horario = horario;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public String getCodigoMateria() {
        return codigoMateria;
    }

    public String getNombreCatedra() {
        return nombreCatedra;
    }

    public String getHorario() {
        return horario;
    }

    public String getIdCurso() {
        return idCurso;
    }
}
