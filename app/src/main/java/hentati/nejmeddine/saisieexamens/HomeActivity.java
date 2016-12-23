package hentati.nejmeddine.saisieexamens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;

public class HomeActivity extends BaseActivity implements ValueEventListener,View.OnClickListener {

    private static final String TAG = "HomeActivity";
    private String[] ITEMS = {"P1", "P2", "D1", "D2", "D3"};
    private String[] ITEMS2 = {""};
    public static Activity act;
    private DatabaseReference mRootRef;
    private ArrayList<Notes> mNotesList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DataSnapshot dataSnapshot;
    private int pos = -1;
    private RadioGroup noteTypeGroup;
    private AppCompatRadioButton selectedNoteType;

    private AppCompatButton fields;
    private AppCompatButton grids;

    private String[] subjects,module;
    private MaterialSpinner level_spinner,subject_spinner,module_spinner;
    private String selectedLevel,selectedSubject,selectedModule;
    private int selectedLevelPos, selectedSubjectPos, selectedModulePos;
    private Level level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        act=this;

        initLevelSpinner();

        initSubjectSpinner();

        initModuleSpinner();

        level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getSelectedItemId() != 0) {
                    selectedLevel = adapterView.getSelectedItem().toString();
                    checkSelection(selectedLevel);
                }
                else if (adapterView.getSelectedItemId() == 0){
                    initLevelSpinner();
                    initSubjectSpinner();
                    initModuleSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemId() != 0) {
                    checkModule(adapterView.getSelectedItem().toString());
                }
                else if(adapterView.getSelectedItemId() == 0){
                    initModuleSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        fields = (AppCompatButton) findViewById(R.id.fields);
        fields.setOnClickListener(this);

        grids = (AppCompatButton) findViewById(R.id.grids);
        grids.setOnClickListener(this);

        ExamensHelper.getInstance(HomeActivity.this).setNoteType("ecrit");

        noteTypeGroup = (RadioGroup) findViewById(R.id.radiosLayout);
        noteTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                selectedNoteType = (AppCompatRadioButton) findViewById(checkedId);

                //Toast.makeText(HomeActivity.this, selectedNoteType.getText(), Toast.LENGTH_SHORT).show();

                ExamensHelper.getInstance(HomeActivity.this).setNoteType(selectedNoteType.getText().toString());

            }

        });


        initFirebase();

    }


    private void initFirebase() {

        try {
            mRootRef = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mRootRef.addValueEventListener(this);
        }
        catch(Exception x){
            Toast.makeText(getApplicationContext(), "Vérifiez votre connexion svp !", Toast.LENGTH_SHORT).show();
        }

    }

    private void initModuleSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ITEMS2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        module_spinner = (MaterialSpinner) findViewById(R.id.module_spinner);
        module_spinner.setAdapter(adapter);
        module_spinner.setHint("Matières");
        if(module_spinner.isEnabled()){
            module_spinner.setEnabled(false);
        }


    }

    private void checkModule(String s) {

        if(s.equals("Sémiologie") || s.equals("T Digestif") || s.equals("Cardiologie") || s.equals("A Pulmonaire")
                || s.equals("A Locomoteur") || s.equals("A Urinaire") || s.equals("Endocrinologie")
                || s.equals("Gynécologie") || s.equals("Pédiatrie") || s.equals("Sys Nerveux") ){

            level = new Level();
            module = level.getModule(s);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, module);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            module_spinner.setAdapter(adapter);
            module_spinner.setFloatingLabelText("Matière - "+ s);
            module_spinner.setSelection(1);
            module_spinner.setEnabled(true);


        }
        else {
            initModuleSpinner();
        }

    }

    private void initSubjectSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ITEMS2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner = (MaterialSpinner) findViewById(R.id.subject_spinner);
        subject_spinner.setAdapter(adapter);
        subject_spinner.setHint("Modules");
        if(subject_spinner.isEnabled()){
            subject_spinner.setEnabled(false);
        }

    }

    private void initLevelSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level_spinner = (MaterialSpinner) findViewById(R.id.level_spinner);
        level_spinner.setAdapter(adapter);

    }

    private void checkSelection(String selectedLevel) {

        level = new Level();
        subjects = level.getSubjects(selectedLevel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(adapter);
        subject_spinner.setHint("Matière - "+selectedLevel);
        subject_spinner.setFloatingLabelText("Matière - "+selectedLevel);

        if(!subject_spinner.isEnabled()){
            subject_spinner.setEnabled(true);
        }

        if(module_spinner.isEnabled()){
            module_spinner.setEnabled(false);
        }

    }

    @Override
    public void onClick(View view) {

            selectedLevel = level_spinner.getSelectedItem().toString();
            selectedLevelPos = level_spinner.getSelectedItemPosition();

            if (subject_spinner.isEnabled()) {
                selectedSubject = subject_spinner.getSelectedItem().toString();
                selectedSubjectPos = subject_spinner.getSelectedItemPosition();
            }

            if (module_spinner.isEnabled()) {
                selectedModule = module_spinner.getSelectedItem().toString();
                selectedModulePos = module_spinner.getSelectedItemPosition();
            }



        subjectValidated(selectedLevel, selectedLevelPos,
                    selectedSubject, selectedSubjectPos,
                    selectedModule, selectedModulePos, view);


    }


    private void subjectValidated(String selectedLevel, int selectedLevelPos,
                                     String selectedSubject, int selectedSubjectPos,
                                     String selectedModule, int selectedModulePos, View view) {

        showProgressDialog();
        if(selectedLevelPos == 0){
            Toast.makeText(getApplicationContext(), "Veuillez choisir le niveau svp !", Toast.LENGTH_SHORT).show();
            hideProgressDialog();

        }
        else if(subject_spinner.isEnabled() && selectedSubjectPos == 0){
            Toast.makeText(getApplicationContext(), "Veuillez choisir le module svp !", Toast.LENGTH_SHORT).show();
            hideProgressDialog();

        }
        else if(module_spinner.isEnabled() && selectedModulePos == 0){
            Toast.makeText(getApplicationContext(), "Veuillez choisir la matière svp !", Toast.LENGTH_SHORT).show();
            hideProgressDialog();

        }
        else{
            //Toast.makeText(getApplicationContext(), "c bon", Toast.LENGTH_SHORT).show();

            ExamensHelper.getInstance(this).setProfLevel(selectedLevel);
            ExamensHelper.getInstance(this).setProfSubject(selectedSubject);

            if(module_spinner.isEnabled()) {
                ExamensHelper.getInstance(this).setProfModule(selectedModule);
            }
            else {
                ExamensHelper.getInstance(this).setProfModule("");
            }


            //getting the list
            if(ExamensHelper.getInstance(this).getmNotesList() != null) {
                ExamensHelper.getInstance(this).getmNotesList().clear();
            }else{
                ExamensHelper.getInstance(this).setmNotesList(new ArrayList<Notes>());
            }



            for (DataSnapshot keys : dataSnapshot.child("enseignants")
                    .child(ExamensHelper.getInstance(this).getmEnseignant().getCin())
                    .child(ExamensHelper.getInstance(this).getProfLevel())
                    .child(ExamensHelper.getInstance(this).getProfSubject())
                    .child(ExamensHelper.getInstance(this).getProfModule())
                    .getChildren()) {

                Notes n = new Notes();

                n.setCode(keys.child("code").getValue().toString());

                if(keys.hasChild("tp")) {
                    n.setTp(keys.child("tp").getValue().toString());
                }
                if(keys.hasChild("oral")) {
                    n.setOral(keys.child("oral").getValue().toString());
                }
                if(keys.hasChild("ecrit")) {
                    n.setEcrit(keys.child("ecrit").getValue().toString());
                }

                ExamensHelper.getInstance(this).getmNotesList().add(n);


            }

            Log.i(TAG, "notesList: "+ExamensHelper.getInstance(this).getmNotesList()+" / size : "
                    + ExamensHelper.getInstance(this).getmNotesList().size());

            hideProgressDialog();
            if(view.getId()==R.id.fields) {
                startActivity(new Intent(HomeActivity.this, NotesActivity.class));
            }else if(view.getId()==R.id.grids){
                startActivity(new Intent(HomeActivity.this, GridNotesActivity.class));
            }

        }

    }

    private boolean mawjoud(String c) {

        for(int i=0; i < ExamensHelper.getInstance(this).getmNotesList().size(); i++){
            if (ExamensHelper.getInstance(this).getmNotesList().get(i).getCode().equals(c)){
                pos = i;
                return true;
            }
        }
        return false;
    }


    public int NoteType(){
        if(ExamensHelper.getInstance(this).getNoteType().equals("tp")){
            return 1;
        }else
        if(ExamensHelper.getInstance(this).getNoteType().equals("oral")){
            return 2;
        }else{
            return 3;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
