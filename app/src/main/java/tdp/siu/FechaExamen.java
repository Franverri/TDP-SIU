package tdp.siu;

public class FechaExamen {
    private String id;
    private String fecha;
    private String hora;

    public FechaExamen(String id, String fecha, String hora){
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
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
}
