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
import deneyimkutusu.xedoxsoft.deneyimkutusu.MainActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.ProfileCreate;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;

public class LoginScreen extends AppCompatActivity {
    //Kullanılan nesneler
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Remove notification bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_screen);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// Activity çalıştığında otomatik klavye açılmasını önleme
        inputEmail = (EditText) findViewById(R.id.editText);
        inputPassword = (EditText) findViewById(R.id.editText2);
        progressBar = (ProgressBar) findViewById(R.id.LoginProgressBar);
        btnSignup = (Button) findViewById(R.id.buttonRegister);
        btnLogin = (Button) findViewById(R.id.button);
        btnReset = (Button) findViewById(R.id.resetPass);

        auth = FirebaseAuth.getInstance();//Firebase auth instance edilmesi
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginScreen.this, MainActivity.class));
            finish();
        }
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginScreen.this, RegisterScreen.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, ResetPasswordActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),R.string.mail_giris, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),R.string.sifre_giris, Toast.LENGTH_SHORT).show();
                    return;
                }
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        Toast.makeText(getApplicationContext(),R.string.sifre_uzunluk, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginScreen.this, getString(R.string.giris_hatasi), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
