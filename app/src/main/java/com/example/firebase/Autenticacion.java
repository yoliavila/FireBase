package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Autenticacion extends BaseActivity {
private FirebaseAuth mAuth;
EditText contra;
EditText correo;
Button registro;
Button loguear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);
       setDayNight();
        correo = (EditText) findViewById(R.id.correo);
        contra = (EditText) findViewById(R.id.contraseña);
        loguear = (Button) findViewById(R.id.loguearbtn);
        registro = (Button) findViewById(R.id.registrarbtn);
       final Switch swi = findViewById(R.id.tema);
        SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
       int theme = sp.getInt("Theme",1);
       if(theme==1){
           swi.setChecked(false);
       }else{
           swi.setChecked(true);
       }

        swi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swi.isChecked()){
                    editor.putInt("Theme",0);
                }else{
                    editor.putInt("Theme",1);

                }
                editor.commit();
                setDayNight();
            }
        });
Intent intent = new Intent(this, MainActivity.class);
        loguear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = correo.getText().toString();
                String password = contra.getText().toString();
                loguear(email , password);
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = correo.getText().toString();
                String password = contra.getText().toString();
                registrar(email , password);
            }
        });
    }

    private void registrar(String email , String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Autenticacion.this, "Registrado con exito.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Autenticacion.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void loguear(String email , String password){
        mAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(this, MainActivity.class);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Autenticacion.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}