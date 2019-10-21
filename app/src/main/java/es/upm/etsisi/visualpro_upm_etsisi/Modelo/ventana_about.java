package es.upm.etsisi.visualpro_upm_etsisi.Modelo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import es.upm.etsisi.visualpro_upm_etsisi.R;


/** Clase encargada de gestionar la ventana emergente
 * @author Ramón Invarato Menéndez
 * @author Roberto Pérez
 */
public class ventana_about extends Dialog {
	private Context contexto;

	
	private String titulo="About VisualPro";
	
	public ventana_about (Context contexto){
		super (contexto);
		this.contexto = contexto;
		mensaje ();
	}

   private void mensaje (){
   	//Frame hinchado.............................
   	LayoutInflater inflater = this.getLayoutInflater();
   	FrameLayout frameHinchado = new FrameLayout(contexto);
   	frameHinchado.addView(inflater.inflate(R.layout.ventana_about, frameHinchado, false));

   	View view = frameHinchado.getChildAt(0);
   	//Frame hinchado.............................
   	
   	
   	
   	//Diálogo....................................
   	new AlertDialog.Builder(contexto)
   	.setNegativeButton(getContext().getString(R.string.Cancelar), null)
   	.setView(frameHinchado)
   	.setTitle(titulo)
   	.show();
   	//Diálogo....................................
   }

}
