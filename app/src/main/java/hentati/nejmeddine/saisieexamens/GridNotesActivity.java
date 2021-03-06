package hentati.nejmeddine.saisieexamens;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GridNotesActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "NotesActivity";
    private TextView prof, etudiant, details, noteView;
    private MaterialEditText code;
    private FloatingActionButton fab;
    private int i = 1;
    private String list = "", note = "";
    private boolean exist = false;


    //Firebase variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private DatabaseReference mProfRef;

    private Enseignant mEnseignant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_notes);

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
        noteView = (TextView) findViewById(R.id.noteView);
        noteView.setText("note "+ExamensHelper.getInstance(this).getNoteType()+" : ");
        code = (MaterialEditText) findViewById(R.id.code);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(this);

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
        code.setText(""+i);
    }

    @Override
    public void onClick(View view) {
        if(checkConnection()) {
            if (view.getId() == R.id.fab) {

                generateList();

                if (!ExamensHelper.getInstance(this).getmNotesList().isEmpty()) {
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
                else{
                    Toast.makeText(getApplicationContext(), "Veuillez noter au moins un étudiant pour continuer !", Toast.LENGTH_SHORT).show();
                }

            }
        }else {
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
            n = new Notes(code.getText().toString(), note, 1);
        }else
        if (ExamensHelper.getInstance(this).getNoteType().equals("oral")){
            n = new Notes(code.getText().toString(), note, 2);
        }else{
            n = new Notes(code.getText().toString(), note, 3);
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
            return !code.getText().toString().equals("");
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
                        //startActivity(new Intent(GridNotesActivity.this, MainActivity.class));
                        //finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivity(new Intent(GridNotesActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .show();


    }

    public void afficher(View view) {

        Toast.makeText(getApplicationContext(), ((TextView)view).getText(), Toast.LENGTH_SHORT).show();


        note = ((TextView)view).getText().toString();


        if(checkConnection()){
            if(validateFields()) {
                if(validateExistance() == -1)
                    Suivant();
                else {
                    //sinon s'il existe, sa position sera 'j', alors prompter à la modifier ou non
                    if (exist == true) {
                        new MaterialDialog.Builder(GridNotesActivity.this)
                                .title("cet étudiant '" + ExamensHelper.getInstance(GridNotesActivity.this).getmNotesList().get(validateExistance()).getCode()
                                        + "' a été déjà noté, voulez vous modifier sa note ?")
                                .positiveText("Modifier")
                                .negativeText("Annuler")
                                .positiveColor(Color.MAGENTA)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //modifier la note et regénérer la liste
                                        if (ExamensHelper.getInstance(GridNotesActivity.this).getNoteType().equals("tp")) {
                                            ExamensHelper.getInstance(GridNotesActivity.this).getmNotesList()
                                                    .get(validateExistance())
                                                    .setTp(note);
                                        } else if (ExamensHelper.getInstance(GridNotesActivity.this).getNoteType().equals("oral")) {
                                            ExamensHelper.getInstance(GridNotesActivity.this).getmNotesList()
                                                    .get(validateExistance())
                                                    .setOral(note);
                                        } else {
                                            ExamensHelper.getInstance(GridNotesActivity.this).getmNotesList()
                                                    .get(validateExistance())
                                                    .setEcrit(note);
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
                        if (ExamensHelper.getInstance(GridNotesActivity.this).getNoteType().equals("tp")) {
                            ExamensHelper.getInstance(GridNotesActivity.this).getmNotesList()
                                    .get(validateExistance())
                                    .setTp(note);
                        } else if (ExamensHelper.getInstance(GridNotesActivity.this).getNoteType().equals("oral")) {
                            ExamensHelper.getInstance(GridNotesActivity.this).getmNotesList()
                                    .get(validateExistance())
                                    .setOral(note);
                        } else {
                            ExamensHelper.getInstance(GridNotesActivity.this).getmNotesList()
                                    .get(validateExistance())
                                    .setEcrit(note);
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
}
