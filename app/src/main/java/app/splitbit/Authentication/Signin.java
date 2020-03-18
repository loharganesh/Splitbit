package app.splitbit.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import app.splitbit.R;
import app.splitbit.Splitbit;

public class Signin extends AppCompatActivity {

    private FirebaseAuth auth;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    private ProgressBar progress_bar;
    private Button button_signin;

    private FirebaseFunctions functions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //initializing layout components
        initLayoutComponents();

        //google sign in
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        auth = FirebaseAuth.getInstance();
        functions = FirebaseFunctions.getInstance();

    }

    //Initializing Activity UI
    private void initLayoutComponents(){
        getSupportActionBar().setTitle("Sign in");
        progress_bar = (ProgressBar) findViewById(R.id.signing_in_indicator);
        button_signin = (Button) findViewById(R.id.button_signin);

        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_bar.setVisibility(View.VISIBLE);
                button_signin.setText("Signing in");
                button_signin.setEnabled(false);
                signIn();
            }
        });

    }

    //Google authentication process
    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                //Google Signin is successfull
                //Authenticating user to firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            }catch (Exception e){
                e.printStackTrace();
                progress_bar.setVisibility(View.GONE);
                button_signin.setText("Sign in");
                button_signin.setEnabled(true);
            }
        }else{

        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d("Firebase Auth ID",account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        auth.signInWithCredential(credential)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        //Sign in successfull, handle user info and UI here
                        if(auth.getCurrentUser() != null){
                            updateDB("Write")
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        progress_bar.setVisibility(View.GONE);
                                        button_signin.setText("Sign in");
                                        button_signin.setEnabled(true);
                                        updateUI(auth.getCurrentUser());
                                        if (!task.isSuccessful()) {
                                            Exception e = task.getException();
                                            if (e instanceof FirebaseFunctionsException) {
                                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                                FirebaseFunctionsException.Code code = ffe.getCode();
                                                Object details = ffe.getDetails();
                                            }

                                            // ...
                                            Toast.makeText(Signin.this, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }

                                        // ...
                                    }
                                });
                        }

                    }else{
                        Log.d("Sign in status","Sign In Failed "+task.getException());
                        progress_bar.setVisibility(View.GONE);
                        button_signin.setText("Sign in");
                        button_signin.setEnabled(true);
                    }
                }
            });
    }

    private Task<String> updateDB(String data) {
        // Create the arguments to the callable function.

        return functions
                .getHttpsCallable("saveUserInfo")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(Signin.this, Splitbit.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }else{

        }
    }

}
