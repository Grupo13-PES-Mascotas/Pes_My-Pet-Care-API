package org.pesmypetcare.pes_my_pet_care_apis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.pesmypetcare.pes_my_pet_care_apis.usermanagerlibrary.UserManagerLibrary;
import org.pesmypetcare.pes_my_pet_care_apis.usermanagerlibrary.UserManagerLibrary.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.pesmypetcare.pes_my_pet_care_apis.usermanagerlibrary.UserManagerLibrary.AltaUsuari;
import static org.pesmypetcare.pes_my_pet_care_apis.usermanagerlibrary.UserManagerLibrary.ConsultarUsuari;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //UserManagerLibrary.PostUser("kayle", "123abc", "kayle@gmail.com");
        AltaUsuari("pit_catalan","13santpere" , "mejubiloyaa@gmail.com");
        //ConsultarUsuari("kayle");
    }
}
