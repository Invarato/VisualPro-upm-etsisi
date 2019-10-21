package es.upm.etsisi.visualpro_upm_etsisi.Modelo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import es.upm.etsisi.visualpro_upm_etsisi.R;


/** Clase encargada de gestionar la ventana emergente
 * @author Ramón Invarato Menéndez
 * @author Roberto Pérez
 */
public abstract class ventana_guardar extends Dialog {
	private Context contexto;
	
	private EditText edittext_nombre;
	
	public ventana_guardar (Context contexto){
		super (contexto);
		this.contexto = contexto;
		mensaje ();
	}

   private void mensaje (){
   	OnClickListener dialogoListener = new OnClickListener() {
   		public void onClick(DialogInterface dialog, int which) {
   			try {
   				ventana_aceptada (edittext_nombre.getText()+"");
   			} catch (Exception e) {
   				e.printStackTrace();
   			};
   		}
   	};

   	//Frame hinchado.............................
   	LayoutInflater inflater = this.getLayoutInflater();
   	FrameLayout frameHinchado = new FrameLayout(contexto);
   	frameHinchado.addView(inflater.inflate(R.layout.ventana_guardar, frameHinchado, false));

   	View view = frameHinchado.getChildAt(0);
   	//Frame hinchado.............................
   	
   	edittext_nombre = (EditText) view.findViewById(R.id.editText_nombrarArchivo);
   	
   	//Di�logo....................................
   	new AlertDialog.Builder(contexto)
   	.setPositiveButton(getContext().getString(R.string.Procesar), dialogoListener)
   	.setNegativeButton(getContext().getString(R.string.Cancelar), null)
   	.setView(frameHinchado)
   	.setTitle(getContext().getString(R.string.Guardar))
   	.show();
   	//Di�logo....................................
   }
   
   public abstract void ventana_aceptada (String nombre_archivo);
	
}
