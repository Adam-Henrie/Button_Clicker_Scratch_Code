package com.example.buttonclicker;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    public static final String Quote_Key = "quote";
    public static final String Author_Key = "author";

    TextView mQuoteTextView;


    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    private DocumentReference rDocRef = FirebaseFirestore.getInstance().document("sampleData/with_more_inspiration");

    private FirebaseStorage storage = FirebaseStorage.getInstance();


    Button btn_button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQuoteTextView = (TextView) findViewById(R.id.quote_display);

        btn_button = findViewById(R.id.btn_button);
        EditText tv_quote = findViewById(R.id.et_quote);
        EditText tv_author =  findViewById(R.id.et_author);


        //if user hasn't logged in before create an intent which transfers you to the LoginRegisterActivity

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startLoginActivity();
        }

    }

    public void startLoginActivity(){
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        this.finish();
    };



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    startLoginActivity();
                                } else {
                                    System.out.println("Failed");
                                }
                            }
                        });
                return true;
        }


        return super.onOptionsItemSelected(item);
    }



    public void fetchQuote(View view){
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String quoteText = documentSnapshot.getString(Quote_Key);
                    String authorText = documentSnapshot.getString(Author_Key);
                    mQuoteTextView.setText("\"" + quoteText + "\" -- " + authorText);
                }
            }
        });

    }



    public void saveQuote(View view){
       EditText quoteView =   findViewById(R.id.et_quote);
        EditText authorView =  findViewById(R.id.et_author);
        String quoteText = quoteView.getText().toString();
        String authorText = authorView.getText().toString();


        if (quoteText.isEmpty() || authorText.isEmpty()) {
            return;
        }
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(Quote_Key, quoteText);
        dataToSave.put(Author_Key, authorText);
        Map<String, Object> dataToAdd  = new HashMap<String, Object>();
        dataToAdd.put("qussss", quoteText);
        dataToAdd.put("asds", authorText);

        mDocRef.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("InspiringQuote", "Document has been saved!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("InspiringQuote", "Document was not saved!", e);
            }
        });


        rDocRef.set(dataToSave);

    }










}