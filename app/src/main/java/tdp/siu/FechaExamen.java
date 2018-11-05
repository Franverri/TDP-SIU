package tdp.siu;

public class FechaExamen {
    private String id;
    private String fecha;
    private String hora;
    private String inscriptos;

    public FechaExamen(String id, String fecha, String hora, String inscriptos){
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.inscriptos = inscriptos;
    }

    public String getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getInscriptos() {
        return inscriptos;
    }
}
