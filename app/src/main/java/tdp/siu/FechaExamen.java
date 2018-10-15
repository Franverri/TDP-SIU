package tdp.siu;

public class FechaExamen {
    private int id;
    private String fecha;
    private String hora;

    public FechaExamen(int id, String fecha, String hora){
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }
}
