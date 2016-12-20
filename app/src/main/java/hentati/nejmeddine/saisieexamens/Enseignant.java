package hentati.nejmeddine.saisieexamens;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by feres on 02/12/2016.
 */
@IgnoreExtraProperties
public class Enseignant implements Serializable{
    private String mCin;


    public Enseignant() {}

    public Enseignant(String cin) {
        this.mCin = cin;
    }

    public String getCin() {
        return mCin;
    }

    public void setCin(String cin) {
        this.mCin = cin;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cin", mCin);

        return result;
    }



}
