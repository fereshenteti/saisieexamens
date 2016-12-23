package hentati.nejmeddine.saisieexamens;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by feres on 02/12/2016.
 */
@IgnoreExtraProperties
public class Level implements Serializable {



    private String[] P1 = {"Anatomie","Anglais","Biochimie","Biologie"
                    , "Biophysique", "Genetique", "Histologie", "Med Communautaire"
                    , "Physiologie", "Sc Humaine", "Secourisme"};

    private String[] P2 = {"Anatomie", "Anglais", "Biochime", "Histologie"
                    , "Immunologie", "Microbiologie", "Physiologie", "Sémiologie"};

    private String[] Semiologie = {"Orth", "Derm", "Rhum", "Card", "Gyn", "Gas", "Uro"
                    , "Uro", "Neuro", "Inf", "Ped", "Chir", "Neph", "Pneum", "Psy", "Hemato"};

    private String[] D1 = {"Parasitologie", "Anapath gle", "Anglais", "Imagerie mle"
                    , "Microbiologie", "T Digestif", "Cardiologie", "Carcinologie", "Hematologie"
                    , "Pharmacologie", "A Pulmonaire", "Philosophie des Sc", "Statistiques"};

    private String[] Digestif = {"Radio", "Anapath", "Gastro", "Chirurgie"};

    private String[] Cardiologie = {"Radio", "Anapath", "Cardio", "CCV"};

    private String[] Pulmonaire = {"RX", "Anapath", "Pneumo", "Ch. Thoracique"};

    private String[] D2 = {"A Locomoteur", "A Urinaire", "Anglais", "Endocrinologie"
                    , "Génétique", "Gynécologie", "Hematologie Clinique", "Infectiologie"
                    , "Pédiatrie", "Pharmacologie"};

    private String[] Locomoteur = {"Radio", "Rhumato", "Ortho", "Anapath", "Med Phys"};

    private String[] Urinaire = {"Radio", "Anapath", "Nephro"};

    private String[] Endocrinologie = {"Anapath", "Endoc"};

    private String[] Gynecologie = {"Radio", "Anapath", "Gynecologie"};

    private String[] Pediatrie = {"Pediatrie", "Radio", "Neonat", "Ch Ped"};

    private String[] D3 = {"Anglais", "Carcinologie", "Dermatologie", "Droit", "Droit de l'homme"
                    , "Droit medical", "Immunologie", "M Interne", "Med Travail"
                    , "Med Preventive", "Med Legale", "Sys Nerveux", "Ophtalmologie", "ORL"
                    , "Stomatologie", "Pratiques Mle", "Psychiatrie", "Reanimation Med", "Therapeutique"};

    private String[] Nerveux = {"Radio", "Anapath", "N Chir", "Neurologie"};


    public Level(){}


    public String[] getSubjects(String s){

        switch (s){

            case "P1" : return P1;

            case "P2" : return P2;

            case "D1" : return D1;

            case "D2" : return D2;

            case "D3" : return D3;

            default : return null;

        }

    }



    public String[] getModule(String s){

        switch (s){

            case "Sémiologie" : return Semiologie;

            case "T Digestif" : return Digestif;

            case "Cardiologie" : return Cardiologie;

            case "A Pulmonaire" : return Pulmonaire;

            case "A Locomoteur" : return Locomoteur;

            case "A Urinaire" : return Urinaire;

            case "Endocrinologie" : return Endocrinologie;

            case "Gynécologie" : return Gynecologie;

            case "Pédiatrie" : return Pediatrie;

            case "Sys Nerveux" : return Nerveux;

            default : return null;

        }

    }


}
