package com.schoolchat.schoolchat.UserInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.schoolchat.schoolchat.R;
import com.schoolchat.schoolchat.Firebase.conexion;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Altausuario extends Activity {

    private Button botonRegistrarse;
    private Button botonCancelar;

    private EditText ed_Em;
    private EditText ed_NomUsu;
    private EditText ed_Cont;


    private EditText ed_ContVerif;
    private CheckBox cb_Profesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);     //AÑADIDO DEBIDO A QUE ES NECESARIO PARA QUE FUNCIONE FIREBASE
        setContentView(R.layout.activity_altausuario);


        botonRegistrarse = (Button) findViewById(R.id.bt_Registrarse);
        botonCancelar = (Button) findViewById(R.id.bt_Cancelar);

        ed_Em = (EditText) findViewById(R.id.ed_Email);
        ed_NomUsu = (EditText) findViewById(R.id.ed_NombreUsuario);
        ed_Cont = (EditText) findViewById(R.id.ed_Contraseña);


        //Verificacion si es profesor:
        ed_ContVerif = (EditText) findViewById(R.id.ed_ContraseñaVerif);
        cb_Profesor = (CheckBox) findViewById(R.id.cb_Profesor);





    }


    //Muestra el campo de la contraseña de verificacion si el checkbox esta seleccionado
    public void onProfesor(View v){
        if (cb_Profesor.isChecked()){
            ed_ContVerif.setVisibility(View.VISIBLE);
        }else{
            ed_ContVerif.setVisibility(View.INVISIBLE);
        }
    }



    /*  Accion a realizar al pulsar en REGISTRAR: Ir a la actividad MainActivity
      * y crear un nuevo usuario en la base de datos de firebase */
    public void onSign(View v){
        //variables para registro pasadas a string
        String useremail=ed_Em.getText().toString();
        String userName=ed_NomUsu.getText().toString();
        String userpassword=ed_Cont.getText().toString();
        if(useremail.isEmpty()||userName.isEmpty()||userpassword.isEmpty()){
            MostrarError("Asegurese de que todos los campos esten rellenos");
        }else{
            final Firebase registroUsuario=new Firebase(conexion.FIREBASE_SCHOOLCHAT);
            final String finalUserEmail=useremail;
            final String finalUserPassword=userpassword;
            final String finalUserName=userName;
            //crear el usuario
            registroUsuario.createUser(useremail,userpassword,new Firebase.ValueResultHandler<Map<String,Object>>(){


                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {

                    Toast.makeText(Altausuario.this,"ususario creado",Toast.LENGTH_SHORT).show();

                    registroUsuario.authWithPassword(finalUserEmail,finalUserPassword,new Firebase.AuthResultHandler(){

                        @Override
                        public void onAuthenticated(AuthData authData) {
                            Map<String,Object> map=new HashMap<String, Object>();
                            map.put(conexion.NOMBRE,finalUserName);
                            map.put(conexion.USER_EMAIL,finalUserEmail);
                            map.put(conexion.CHILD_CONNECT,conexion.ESTADO_OFFLINE);


                            Date fecha=new Date(); //CAMBIADO*GABRI
                            map.put(conexion.FECHA,fecha);

                            // registroUsuario.child("Usuarios: ").child(authData.getUid()).setValue(map); //CAMBIO*GABRI -- Esta linea añade en la pestaña DATA, los datos recogidos del nuevo usuario.

                            registroUsuario.child(conexion.CHILD_USERS).child(authData.getUid()).setValue(map); //CAMBIADO PARA QUE SE MUESTRE EL NOMBRE DE USUARIO EN VEZ DE EL UID!!

                            Intent return_login= new Intent(Altausuario.this,LogInActivity.class);
                            return_login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            return_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(return_login);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {

                        }
                    });
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    //si el usuario tiene problemas con el registro se le notificara un error
                    MostrarError(firebaseError.getMessage());
                }
            });
        }
    }


    //metodo para crear mensajes de alerta de errores
    private void MostrarError(String error){
        //Create an AlertDialog to show error message
        AlertDialog.Builder builder=new AlertDialog.Builder(Altausuario.this);
        builder.setMessage(error)
                .setTitle(getString(R.string.title_LogIn))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    //la accion de cancelar te devuelve al la actividad de LogIn
    public void onCancelar(View v){
        Intent j = new Intent(this,LogInActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(j);
    }
}
