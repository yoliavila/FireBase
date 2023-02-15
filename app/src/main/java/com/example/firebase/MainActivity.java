package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
Button guardar;
Button listar;
Button borrar;
ListView lista;
Button modificar;
EditText nombre;
EditText numero;
EditText direccion;
EditText correo;
ImageButton telefono;
private DatabaseReference marksRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listar = (Button) findViewById(R.id.listaNombresBut);
        borrar = (Button) findViewById(R.id.borrar);
        modificar = (Button) findViewById(R.id.modificar);
        guardar = (Button) findViewById(R.id.save_button);
        nombre = findViewById(R.id.name_editText);
        numero = findViewById(R.id.numeroedit);
        direccion = findViewById(R.id.direccionedit);
        correo = findViewById(R.id.correoedit);
        lista = findViewById(R.id.listaNombres);
        telefono = findViewById(R.id.llamadabtn);
        SharedPreferences sp = getSharedPreferences("SP", this.MODE_PRIVATE);
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("AGENDAS");
       marksRef = dbr.child(userId);
       guardar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(validarTelefono(numero.getText().toString())==true) {
                   Contacto co = new Contacto(nombre.getText().toString(), numero.getText().toString(), correo.getText().toString(), direccion.getText().toString());
                   marksRef.push().setValue(co);
                   if(nombre.getText().toString().isEmpty()){
                       Toast.makeText(MainActivity.this, "EN EL CAMPO NOMBRE DEBE DE HABER ALGO.",
                               Toast.LENGTH_SHORT).show();
                   }
                   if(correo.getText().toString().isEmpty()){
                       Toast.makeText(MainActivity.this, "EN EL CAMPO CORREO DEBE DE HABER ALGO.",
                               Toast.LENGTH_SHORT).show();
                   }
                   if(direccion.getText().toString().isEmpty()){
                       Toast.makeText(MainActivity.this, "EN EL CAMPO DIRECCION DEBE DE HABER ALGO.",
                               Toast.LENGTH_SHORT).show();
                   }
               }else{
                   Toast.makeText(MainActivity.this, "EN EL CAMPO NÚMERO DEBE HABER UN NÚMERO DE TELÉFONO VALIDO.",
                           Toast.LENGTH_SHORT).show();
               }
           }
       });
       listar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ListView lista = findViewById(R.id.listaNombres);
               Contacto c = new Contacto();
               marksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       Contacto c;
                       ArrayAdapter<String> adaptador;
                       ArrayList<String> listado = new ArrayList<String>();
                       for(DataSnapshot ds : snapshot.getChildren()){
                           c = ds.getValue(Contacto.class);
                           listado.add(c.getNombre());
                       }
                       adaptador = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,listado);
                       lista.setAdapter(adaptador);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
           }
       });

       borrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Contacto c = new Contacto();
               Query q = marksRef.orderByChild("nombre").equalTo(nombre.getText().toString());
               q.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                     for (DataSnapshot ds: snapshot.getChildren()){
                         String clave = ds.getKey();
                         marksRef.child(clave).removeValue();
                     }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

           }
       });


       modificar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Contacto co = new Contacto(nombre.getText().toString(), numero.getText().toString(), correo.getText().toString(), direccion.getText().toString());
Query q = marksRef.orderByChild("nombre").equalTo(nombre.getText().toString());
q.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for(DataSnapshot ds: snapshot.getChildren()){
            String clave = ds.getKey();
            marksRef.child(clave).child("direccion").setValue(direccion.getText().toString());
            marksRef.child(clave).child("correo").setValue(correo.getText().toString());
            marksRef.child(clave).child("numero").setValue(numero.getText().toString());

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
           }
       });

       telefono.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(validarTelefono(numero.getText().toString())==true) {
                   llamar(numero.getText().toString());
               }else{
                   Toast.makeText(MainActivity.this, "EN EL CAMPO NÚMERO DEBE HABER UN NÚMERO DE TELÉFONO VALIDO.",
                           Toast.LENGTH_SHORT).show();
               }
           }
       });





    }
    public void llamar(String tel){
        startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + tel)));

    }
}