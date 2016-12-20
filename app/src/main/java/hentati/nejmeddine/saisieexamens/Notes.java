package hentati.nejmeddine.saisieexamens;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by feres on 05/12/2016.
 */
@IgnoreExtraProperties
public class Notes implements Serializable {

    private String code, ecrit, tp, oral;

    public Notes(){}

    public Notes(String code, String ecrit, int p) {

        this.code = code;
        switch (p) {
            case 1:
                this.tp = ecrit;
                break;
            case 2:
                this.oral = ecrit;
                break;
            default:
                this.ecrit = ecrit;
                break;
        }
    }

        public Notes(String code, String tp, String oral, String ecrit){

            this.code = code;

            if(tp != null) {
                this.tp = tp;
            }
            if(oral != null) {
                this.oral = oral;
            }
            if(ecrit != null) {
                this.ecrit = ecrit;
            }

            }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getOral() {
        return oral;
    }

    public void setOral(String oral) {
        this.oral = oral;
    }

    public String getEcrit() {
        return ecrit;
    }

    public void setEcrit(String ecrit) {
        this.ecrit = ecrit;
    }
}
