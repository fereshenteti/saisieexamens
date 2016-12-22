package hentati.nejmeddine.saisieexamens;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements ValueEventListener,View.OnClickListener {

    private static final String TAG = "userCheck";
    MaterialEditText cin;
    AppCompatButton continuer;
    public static Activity act;

    //Firebase variables
    private DatabaseReference mRootRef;
    private DatabaseReference mProfRef;

    private ArrayList<Enseignant> listeEnseignants;

    private FirebaseAuth mAuth;

    private boolean isMawjoud = false;
    private boolean isFergha = false;
    private boolean hasChanged = false;

    private Enseignant mEnseignant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act=this;
        listeEnseignants = new ArrayList<>();

        // 1
        initFirebase();

        // 2
        initView();

    }


    // 1
    private void initFirebase() {

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mProfRef = mRootRef.child("enseignants");
        mAuth = FirebaseAuth.getInstance();
        mRootRef.addValueEventListener(this);

    }

    // 2
    private void initView() {

        cin = (MaterialEditText) findViewById(R.id.cin);

        cin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    validateCIN();
                }
                return false;
            }
        });
        continuer = (AppCompatButton) findViewById(R.id.continuer);
        continuer.setOnClickListener(this);

    }

    // 3
    private void validateCIN() {

        //this method is to validate if the input is exactly 8 numbers or not

            if (!cin.getText().toString().isEmpty() && cin.getText().toString().length() == 8) {

                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Write new user
                        writeNewEnseignant(cin.getText().toString());
                    }
                });
            } else {
                //this is toasted when the input in empty or different than exactly 8 numbers
                Toast.makeText(getApplicationContext(), "Veuillez entrer les 8 chiffres de votre mCin !", Toast.LENGTH_LONG).show();
            }

    }

    // 4
    private void writeNewEnseignant(String _cin) {

        if(!ExamensHelper.getInstance(this).getEnseignants().isEmpty()) {

            for (Enseignant enseignant : ExamensHelper.getInstance(this).getEnseignants()){

                if(enseignant.getCin()!=null) {

                    if (enseignant.getCin().equals(_cin)) {
                        isMawjoud = true;
                        ExamensHelper.getInstance(this).setmEnseignant(enseignant);
                        hasChanged = true;
                        Log.w(TAG, "MainActivity:mawjoud");
                        break;
                    } else {
                        isMawjoud = false;
                        hasChanged = true;
                        Log.w(TAG, "MainActivity:mouch mawjoud");
                    }
                }
            }
            isFergha = false;
        }else{
            isFergha = true;
        }

        Log.w(TAG, "MainActivity: isMawjoud = "+isMawjoud);
        Log.w(TAG, "MainActivity: isFergha = "+isFergha);


        if((hasChanged == true || isFergha == true) && isMawjoud == false){

            Enseignant ens = new Enseignant(_cin);
            listeEnseignants.add(ens);
            ExamensHelper.getInstance(this).setmEnseignant(ens);

            //Map<String, Object> postValues = ens.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/enseignants/"+_cin, ens);

            mRootRef.updateChildren(childUpdates);

            hasChanged = false;

        }

        startActivity(new Intent(MainActivity.this, HomeActivity.class));

    }


    public void onStop() {
        super.onStop();
    }


    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.continuer) {
            validateCIN();
            Log.w(TAG, "MainActivity:validateCIN");

        }
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        listeEnseignants.clear();

        for (DataSnapshot keys : dataSnapshot.child("enseignants").getChildren()) {
            listeEnseignants.add(new Enseignant(keys.getKey()));
        }

        ExamensHelper.getInstance(this).setEnseignants(listeEnseignants);


        //Log.i(TAG, "HomeActivity:onDataChange ====> enseignants : "+ExamensHelper.getInstance(this).getEnseignants());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }



}
