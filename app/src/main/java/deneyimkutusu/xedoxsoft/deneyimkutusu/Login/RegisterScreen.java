package deneyimkutusu.xedoxsoft.deneyimkutusu.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.ProfileCreate;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;


public class RegisterScreen extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Remove notification bar
        setContentView(R.layout.activity_register_screen);
        getSupportActionBar().hide();//Actionbar gizleme
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//Activity çalıştığında otomatik klavye açılmasını önleme
        auth = FirebaseAuth.getInstance();//Get Firebase auth instance

        btnSignIn = (Button) findViewById(R.id.alreadyRegisterScreenBtn);
        btnSignUp = (Button) findViewById(R.id.kaydol);
        inputEmail = (EditText) findViewById(R.id.editText3);
        inputPassword = (EditText) findViewById(R.id.editText5);
        progressBar = (ProgressBar) findViewById(R.id.LoginProgressBar);
        btnResetPassword = (Button) findViewById(R.id.alreadyRegisterScreenBtn2);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterScreen.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterScreen.this, LoginScreen.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),R.string.mail_giris, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),R.string.sifre_giris, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(),R.string.sifre_uzunluk, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(RegisterScreen.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterScreen.this,R.string.hesap_bulunamadi,Toast.LENGTH_SHORT).show();
                                } else {
                                    //Kayıt olduysa profil oluşturmaya yönlendirilir.
                                    startActivity(new Intent(RegisterScreen.this, ProfileCreate.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterScreen.this, LoginScreen.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
