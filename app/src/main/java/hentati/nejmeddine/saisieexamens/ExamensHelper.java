package hentati.nejmeddine.saisieexamens;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adnenhamdouni on 01/05/2016.
 */
public class ExamensHelper {

    private static volatile ExamensHelper mInstance = null;

    private ArrayList<Notes> mNotesList = null;
    private ArrayList<Enseignant> mEnseignants = null;

    private Enseignant mEnseignant;

    private String profLevel, profSubject, profModule, noteType;

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    private boolean isLoaded;

    private Context mContext;

    public static ExamensHelper getInstance(Context context) {

        synchronized (ExamensHelper.class) {
            if (mInstance == null) {
                mInstance = new ExamensHelper(context);
            }
        }
        return mInstance;
    }

    private ExamensHelper(Context context) {
        mContext = context;
        mEnseignants = new ArrayList<>();
    }

    public void setEnseignants(List<Enseignant> enseignants) {

        mEnseignants.clear();
        if (enseignants != null & enseignants.size() > 0) {
            for (Enseignant ens : enseignants) {
                mEnseignants.add(ens);
            }
        }
    }

    public List<Enseignant> getEnseignants() {
        List<Enseignant> enseignants = new ArrayList<>();
        if (mEnseignants != null & mEnseignants.size() > 0) {
            for (Enseignant ens : mEnseignants) {
                enseignants.add(ens);
            }
        }
        return enseignants;
    }

    public Enseignant getmEnseignant() {
        return mEnseignant;
    }

    public void setmEnseignant(Enseignant mEnseignant) {
        this.mEnseignant = mEnseignant;
    }

    public String getProfLevel() {
        return profLevel;
    }

    public void setProfLevel(String profLevel) {
        this.profLevel = profLevel;
    }

    public String getProfSubject() {
        return profSubject;
    }

    public void setProfSubject(String profSubject) {
        this.profSubject = profSubject;
    }

    public String getProfModule() {
        return profModule;
    }

    public void setProfModule(String profModule) {
        this.profModule = profModule;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public ArrayList<Notes> getmNotesList() {
        return mNotesList;
    }

    public void setmNotesList(ArrayList<Notes> mNotesList) {
        this.mNotesList = mNotesList;
    }
}
