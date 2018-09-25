package tdp.siu;

public class Catedra {

    private String curso;
    private String catedra;
    private String horario;

    public Catedra(String curso, String catedra, String horario) {
        this.curso = curso;
        this.catedra = catedra;
        this.horario = horario;
    }

    public String getCurso() {
        return curso;
    }

    public String getCatedra() {
        return catedra;
    }

    public String getHorario() {
        return horario;
    }
}
