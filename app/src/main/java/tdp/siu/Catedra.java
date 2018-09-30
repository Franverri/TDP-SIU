package tdp.siu;

import android.util.Log;

import java.util.Objects;

public class Catedra {

    private String curso;
    private String catedra;
    private String horario;
    private String dias;
    private String sedes;
    private String aulas;
    private String cupos;

    public Catedra(String curso, String catedra, String dias, String horario, String sedes, String aulas, String cupos) {
        this.curso = curso;
        this.catedra = catedra;
        this.horario = horario;
        this.dias = dias;
        this.sedes = sedes;
        this.aulas = aulas;
        this.cupos = cupos;
    }

    public String getCupos(){
        return cupos;
    }

    public String getCurso() {
        return curso;
    }

    public String getCatedra() {
        return catedra;
    }

    public String getHorario() {

        String strFinal = "";
        if (sedes.equals("[\".\"]")){
            return "CONDICIONAL";
        }
        Log.i("DEBUG", sedes);
        //Obtengo el/los dias
        String strDias =  this.dias.replace("\"", "");
        strDias = strDias.replace("[", "");
        strDias = strDias.replace("]", "");
        String[] arrayDias = strDias.split(",");

        //Obtengo el/los horarios
        String strHorario = this.horario.replace("\"", "");
        strHorario = strHorario.replace("[", "");
        strHorario = strHorario.replace("]", "");
        String[] arrayHorario = strHorario.split(",");

        //Obtengo el/las sedes
        String strSedes = this.sedes.replace("\"", "");
        strSedes = strSedes.replace("[", "");
        strSedes = strSedes.replace("]", "");
        String[] arraySedes = strSedes.split(",");

        //Obtengo el/las aulas
        String strAulas = this.aulas.replace("\"", "");
        strAulas = strAulas.replace("[", "");
        strAulas = strAulas.replace("]", "");
        String[] arrayAulas = strAulas.split(",");
        for (int i = 0; i < arrayDias.length; i++) {
            strFinal = strFinal + arrayDias[i] + " " + arrayHorario[i] + " [" + arraySedes[i] + " - " + arrayAulas[i] + "] \n";
        }

        return strFinal;
    }
}
