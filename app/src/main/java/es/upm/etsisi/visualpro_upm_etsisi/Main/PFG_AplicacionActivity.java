package es.upm.etsisi.visualpro_upm_etsisi.Main;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import es.upm.etsisi.visualpro_upm_etsisi.R;
import es.upm.etsisi.visualpro_upm_etsisi.vista.interfaz;


/** Lanzador de la apliación
 * @author Ramón Invarato Menéndez
 * @author Roberto Sáez Ruiz
 */
public class PFG_AplicacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        finish();
//        Intent nuevoIntent = new Intent(PFG_AplicacionActivity.this, interfaz.class);
        Intent nuevoIntent = new Intent(PFG_AplicacionActivity.this, interfaz.class);
        startActivity (nuevoIntent);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
////        setSupportActionBar(myToolbar);
//
//        finish();
////        Intent nuevoIntent = new Intent(PFG_AplicacionActivity.this, interfaz.class);
//        Intent nuevoIntent = new Intent(PFG_AplicacionActivity.this, interfaz.class);
//        startActivity (nuevoIntent);
//
//
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//
//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

}

//public class PFG_AplicacionActivity extends Activity {
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//		finish();
//		Intent nuevoIntent = new Intent(PFG_AplicacionActivity.this, interfaz.class);
//		startActivity (nuevoIntent);
//    }
//}