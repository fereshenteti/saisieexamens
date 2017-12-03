package hentati.nejmeddine.saisieexamens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotesActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = "NotesActivity";
    private TextView prof, etudiant, details;
    private MaterialEditText noteEcrit, code;
    private AppCompatButton continuer, cBon;
    private FloatingActionButton fab;
    private int i = 1;
    private String list = "";
    private boolean exist = false;


    //Firebase variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private DatabaseReference mProfRef;

    private Enseignant mEnseignant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        firstInitView();
        initView();
        initFirebase();

    }

    private void initFirebase() {

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mProfRef = mDatabase.child("enseignants");
            mAuth = FirebaseAuth.getInstance();
        }
        catch(Exception x){
            Toast.makeText(getApplicationContext(), "Vérifiez votre connexion svp !", Toast.LENGTH_SHORT).show();
        }

    }

    private void firstInitView(){

        prof = (TextView) findViewById(R.id.prof);
        details = (TextView) findViewById(R.id.details);
        etudiant = (TextView) findViewById(R.id.etudiant);
        noteEcrit = (MaterialEditText) findViewById(R.id.noteEcrit);
        noteEcrit.setHint("note "+ExamensHelper.getInstance(this).getNoteType());
        code = (MaterialEditText) findViewById(R.id.code);
        continuer = (AppCompatButton) findViewById(R.id.continuer);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        continuer.setOnClickListener(this);
        fab.setOnClickListener(this);

        noteEcrit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int r, KeyEvent keyEvent) {
                if (r == EditorInfo.IME_ACTION_DONE) {
                    if(checkConnection()){
                        if (validateFields()) {
                            if (validateExistance() == -1)
                                Suivant();
                            else {
                                //sinon s'il existe, sa position sera 'j', alors prompter à la modifier ou non
                                if (exist == true) {
                                    new MaterialDialog.Builder(NotesActivity.this)
                                            .title("cet étudiant '" + ExamensHelper.getInstance(NotesActivity.this).getmNotesList().get(validateExistance()).getCode()
                                                    + "' a été déjà noté, voulez vous modifier sa note ?")
                                            .positiveText("Modifier")
                                            .negativeText("Annuler")
                                            .positiveColor(Color.MAGENTA)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    //modifier la note et regénérer la liste
                                                    if (ExamensHelper.getInstance(NotesActivity.this).getNoteType().equals("tp")) {
                                                        ExamensHelper.getInstance(NotesActivity.this).getmNotesList()
                                                                .get(validateExistance())
                                                                .setTp(noteEcrit.getText().toString());
                                                    } else if (ExamensHelper.getInstance(NotesActivity.this).getNoteType().equals("oral")) {
                                                        ExamensHelper.getInstance(NotesActivity.this).getmNotesList()
                                                                .get(validateExistance())
                                                                .setOral(noteEcrit.getText().toString());
                                                    } else {
                                                        ExamensHelper.getInstance(NotesActivity.this).getmNotesList()
                                                                .get(validateExistance())
                                                                .setEcrit(noteEcrit.getText().toString());
                                                    }
                                                    generateList();
                                                    i += 1;
                                                    initView();
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    initView();
                                                }
                                            })
                                            .show();

                                } else {
                                    if (ExamensHelper.getInstance(NotesActivity.this).getNoteType().equals("tp")) {
                                        ExamensHelper.getInstance(NotesActivity.this).getmNotesList()
                                                .get(validateExistance())
                                                .setTp(noteEcrit.getText().toString());
                                    } else if (ExamensHelper.getInstance(NotesActivity.this).getNoteType().equals("oral")) {
                                        ExamensHelper.getInstance(NotesActivity.this).getmNotesList()
                                                .get(validateExistance())
                                                .setOral(noteEcrit.getText().toString());
                                    } else {
                                        ExamensHelper.getInstance(NotesActivity.this).getmNotesList()
                                                .get(validateExistance())
                                                .setEcrit(noteEcrit.getText().toString());
                                    }
                                    generateList();
                                    i += 1;
                                    initView();
                                }

                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Vérifiez votre connexion svp !", Toast.LENGTH_SHORT).show();

                    }
                }

                return false;

            }
        });


        prof.setText(ExamensHelper.getInstance(this).getmEnseignant().getCin());
        if (ExamensHelper.getInstance(this).getProfModule().equals("")){
            details.setText(ExamensHelper.getInstance(this).getProfLevel() + " - "
                    + ExamensHelper.getInstance(this).getProfSubject());
        }else {
            details.setText(ExamensHelper.getInstance(this).getProfLevel() + " - "
                    + ExamensHelper.getInstance(this).getProfSubject() + " - "
                    + ExamensHelper.getInstance(this).getProfModule());
        }

    }

    private void initView() {

        etudiant.setText("Etudiant "+i);

        noteEcrit.setText("");
        code.setText(""+i);
        //code.requestFocus();
        noteEcrit.requestFocus();

    }


    @Override
    public void onClick(View view) {
        if(checkConnection()){
            if(view.getId() == R.id.fab) {

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();

                //vérifier les champs
                if(validateFields()){

                    //vérifier l'existance dans le vecteur
                    final int j = validateExistance();
                    if(j == -1){
                        //s'il n'existe pas, ajouter nouveau, générer la liste et reinitialiser la vue
                        addToVector();
                        generateList();
                        initView();
                        if(!ExamensHelper.getInstance(this).getmNotesList().isEmpty()) {
                            new MaterialDialog.Builder(this)
                                    .title("Confirmer ces notes ?")
                                    .content(list)
                                    .positiveText("Confirmer")
                                    .negativeText("Annuler")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            SubmitNotes();
                                        }
                                    })
                                    .show();
                        }
                    }
                    else{
                        //sinon s'il existe, sa position sera 'j', alors prompter à la modifier ou non
                        new MaterialDialog.Builder(this)
                                .title("cet étudiant ("+ExamensHelper.getInstance(this)
                                        .getmNotesList().get(j).getCode()+") a été déjà noté, voulez vous modifier sa note ?")
                                .positiveText("Modifier")
                                .negativeText("Annuler")
                                .positiveColor(Color.MAGENTA)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //modifier la note et regénérer la liste
                                        if(ExamensHelper.getInstance(NotesActivity.this).getNoteType().equals("tp")) {
                                            ExamensHelper.getInstance(NotesActivity.this).getmNotesList().get(j)
                                                    .setTp(noteEcrit.getText().toString());
                                        }else
                                        if (ExamensHelper.getInstance(NotesActivity.this).getNoteType().equals("oral")){
                                            ExamensHelper.getInstance(NotesActivity.this).getmNotesList().get(j)
                                                    .setOral(noteEcrit.getText().toString());
                                        }else{
                                            ExamensHelper.getInstance(NotesActivity.this).getmNotesList().get(j)
                                                    .setEcrit(noteEcrit.getText().toString());
                                        }

                                        generateList();
                                        i+=1;
                                        initView();
                                        if(!ExamensHelper.getInstance(NotesActivity.this).getmNotesList().isEmpty()) {
                                            new MaterialDialog.Builder(NotesActivity.this)
                                                    .title("Confirmer ces notes ?")
                                                    .content(list)
                                                    .positiveText("Confirmer")
                                                    .negativeText("Annuler")
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            SubmitNotes();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        initView();
                                        if(!ExamensHelper.getInstance(NotesActivity.this).getmNotesList().isEmpty()) {
                                            new MaterialDialog.Builder(getApplicationContext())
                                                    .title("Confirmer ces notes ?")
                                                    .content(list)
                                                    .positiveText("Confirmer")
                                                    .negativeText("Annuler")
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            SubmitNotes();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                })
                                .show();

                    }




                }
                else if(!ExamensHelper.getInstance(this).getmNotesList().isEmpty()){
                    generateList();
                    new MaterialDialog.Builder(this)
                            .title("Confirmer ces notes ?")
                            .content(list)
                            .positiveText("Confirmer")
                            .negativeText("Annuler")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    SubmitNotes();
                                }
                            })
                            .show();

                }


            }

            else if(view.getId() == R.id.continuer ) {
                if(validateFields()) {
                    if (validateExistance() == -1)
                        Suivant();
                    else {
                        Toast.makeText(getApplicationContext(), "ça existe déjà !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Vérifiez votre connexion svp !", Toast.LENGTH_SHORT).show();
        }

    }


    private void Suivant(){

                addToVector();
                i += 1;
                initView();

    }


    private void addToVector() {

        Notes n;
        if(ExamensHelper.getInstance(this).getNoteType().equals("tp")) {
            n = new Notes(code.getText().toString(), noteEcrit.getText().toString(), 1);
        }else
        if (ExamensHelper.getInstance(this).getNoteType().equals("oral")){
            n = new Notes(code.getText().toString(), noteEcrit.getText().toString(), 2);
        }else{
            n = new Notes(code.getText().toString(), noteEcrit.getText().toString(), 3);

        }

        if(ExamensHelper.getInstance(this).getmNotesList() != null) {
            ExamensHelper.getInstance(this).getmNotesList().add(n);
        }
        else{
            ExamensHelper.getInstance(this).setmNotesList(new ArrayList<Notes>());
            ExamensHelper.getInstance(this).getmNotesList().add(n);
        }

    }


    private void generateList(){

        list = "";
        for (int j = 0; j < ExamensHelper.getInstance(this).getmNotesList().size();j++) {


            if(ExamensHelper.getInstance(this).getNoteType().equals("tp")
                    && ExamensHelper.getInstance(this).getmNotesList().get(j).getTp() != null) {
                list += "Etudiant : " + ExamensHelper.getInstance(this).getmNotesList().get(j).getCode() + "\n";
                list += ExamensHelper.getInstance(this).getNoteType() + " : "
                        + ExamensHelper.getInstance(this).getmNotesList().get(j).getTp() + "\n\n";
            }
            else if(ExamensHelper.getInstance(this).getNoteType().equals("oral")
                    && ExamensHelper.getInstance(this).getmNotesList().get(j).getOral() != null){
                list += "Etudiant : " + ExamensHelper.getInstance(this).getmNotesList().get(j).getCode() + "\n";
                list += ExamensHelper.getInstance(this).getNoteType() + " : "
                        + ExamensHelper.getInstance(this).getmNotesList().get(j).getOral() + "\n\n";
            }
            else if(ExamensHelper.getInstance(this).getNoteType().equals("ecrit")
                    && ExamensHelper.getInstance(this).getmNotesList().get(j).getEcrit() != null){
                list += "Etudiant : " + ExamensHelper.getInstance(this).getmNotesList().get(j).getCode() + "\n";
                list += ExamensHelper.getInstance(this).getNoteType() + " : "
                        + ExamensHelper.getInstance(this).getmNotesList().get(j).getEcrit() + "\n\n";
            }

        }
    }


    private boolean validateFields(){

        if(!code.getText().toString().equals("") && !noteEcrit.getText().toString().equals("")
                && noteEcrit.getText().toString().length() <= 5
                && noteEcrit.getText().toString().indexOf(".")!=noteEcrit.getText().toString().length()-1
                && noteEcrit.getText().toString().indexOf(".")!=0) {



            Log.w(TAG, "NotesActivity: indexof . : " + noteEcrit.getText().toString().indexOf("."));
            Log.w(TAG, "NotesActivity: length : " + noteEcrit.getText().toString().length());

            Double _noteEcrit = Double.parseDouble(noteEcrit.getText().toString());

            if (_noteEcrit >= 0 && _noteEcrit <= 100){

                if(noteEcrit.getText().toString().contains(".")) {
                    String dec = noteEcrit.getText().toString()
                            .substring(noteEcrit.getText().toString().indexOf(".") + 1
                                    , noteEcrit.getText().toString().length());

                    int decimal = Integer.parseInt(dec);

                    Log.w(TAG, "NotesActivity: note : " + dec + " / " + decimal);

                    if(decimal == 25 || decimal == 5 || decimal == 75 || decimal == 50 || decimal == 0){
                        return true;
                    }
                    else{
                        noteEcrit.setError("seuls 0, 25, 5 et 75 sont acceptés après la virgule !");
                        return false;
                    }
                }
                else{
                    return true;
                }

            }
            else{
                noteEcrit.setError("Entrez une note entre 0 et 100 !");

                return false;
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs correctement svp !", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    private int validateExistance() {

        int j;
        if (ExamensHelper.getInstance(this).getmNotesList() != null){
            for (j = 0; j < ExamensHelper.getInstance(this).getmNotesList().size(); j++) {

                if (ExamensHelper.getInstance(this).getmNotesList().get(j).getCode().equals(code.getText().toString())) {
                    if (ExamensHelper.getInstance(this).getNoteType().equals("tp")
                            && ExamensHelper.getInstance(this).getmNotesList().get(j).getTp() != null) {
                        exist = true;
                        return j;
                    } else if (ExamensHelper.getInstance(this).getNoteType().equals("oral")
                            && ExamensHelper.getInstance(this).getmNotesList().get(j).getOral() != null) {
                        exist = true;
                        return j;
                    } else if (ExamensHelper.getInstance(this).getNoteType().equals("ecrit")
                            && ExamensHelper.getInstance(this).getmNotesList().get(j).getEcrit() != null) {
                        exist = true;
                        return j;
                    } else {
                        exist = false;
                        return j;
                    }
                    //item's position
                }
            }
        }

        return -1;//item does not exist, add a new one


    }


    private void SubmitNotes() {

        showProgressDialog();

        Map<String, Object> childUpdates = new HashMap<>();

            if(ExamensHelper.getInstance(this).getProfModule() != null
                    && !ExamensHelper.getInstance(this).getProfModule().equals("")) {
                childUpdates.put("/enseignants/"
                                + ExamensHelper.getInstance(this).getmEnseignant().getCin() + "/"
                                + ExamensHelper.getInstance(this).getProfLevel() + "/"
                                + ExamensHelper.getInstance(this).getProfSubject() + "/"
                                + ExamensHelper.getInstance(this).getProfModule() + "/"
                        , ExamensHelper.getInstance(this).getmNotesList());

                mDatabase.updateChildren(childUpdates);


                childUpdates = new HashMap<>();
                childUpdates.put("/niveaux/"
                                + ExamensHelper.getInstance(this).getProfLevel() + "/"
                                + "/matières/"
                                + ExamensHelper.getInstance(this).getProfSubject() + "/"
                                + ExamensHelper.getInstance(this).getProfModule() + "/"
                        , ExamensHelper.getInstance(this).getmNotesList());

                mDatabase.updateChildren(childUpdates);

            }
            else{
                childUpdates.put("/enseignants/"
                                + ExamensHelper.getInstance(this).getmEnseignant().getCin() + "/"
                                + ExamensHelper.getInstance(this).getProfLevel() + "/"
                                + ExamensHelper.getInstance(this).getProfSubject() + "/"
                        , ExamensHelper.getInstance(this).getmNotesList());

                mDatabase.updateChildren(childUpdates);


                childUpdates = new HashMap<>();
                childUpdates.put("/niveaux/"
                                + ExamensHelper.getInstance(this).getProfLevel() + "/"
                                + "/matières/"
                                + ExamensHelper.getInstance(this).getProfSubject() + "/"
                        , ExamensHelper.getInstance(this).getmNotesList());

                mDatabase.updateChildren(childUpdates);

            }

        HomeActivity.act.finish();
        MainActivity.act.finish();
        hideProgressDialog();
        Toast.makeText(getApplicationContext(), "Notes enregistrées avec succès..", Toast.LENGTH_SHORT).show();

        new MaterialDialog.Builder(this)
                .title("Voulez vous générer un fichier excel pour ces notes ?")
                .positiveText("Oui")
                .negativeText("Non")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showProgressDialog();
                        generateFile();
                        hideProgressDialog();
                        openFolder();
                        //startActivity(new Intent(NotesActivity.this, MainActivity.class));
                        //finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivity(new Intent(NotesActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .show();





    }

}
