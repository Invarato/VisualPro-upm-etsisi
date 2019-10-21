package es.upm.etsisi.visualpro_upm_etsisi.Modelo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import es.upm.etsisi.visualpro_upm_etsisi.R;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.Capa;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.CapaFiltros;

//import com.VisualPro.controlador.Capa.ORDEN;


/** Clase encargada de gestionar la ventana emergente
 * @author Ramón Invarato Menéndez
 * @author Roberto Séez
 */
public abstract class ventana_unico extends Dialog {
	private Context contexto;
	
	private ImageView imagenView_previsualizacion;
	
	private Capa capa_a_tratar;
	private CapaFiltros.ORDEN orden; //Si es sepia, etc

	private String titulo="";
	
	public ventana_unico (Context contexto, String titulo, Capa capa, CapaFiltros.ORDEN orden){
		super (contexto);
		this.contexto = contexto;
		this.titulo = titulo;
		this.capa_a_tratar = capa;
		this.orden = orden;
		mensaje ();
	}

   private void mensaje (){
   	OnClickListener dialogoListener = new OnClickListener() {
   		public void onClick(DialogInterface dialog, int which) {
   			try {
   				ventana_aceptada ();
   			} catch (Exception e) {
   				e.printStackTrace();
   			};
   		}
   	};

   	//Frame hinchado.............................
   	LayoutInflater inflater = this.getLayoutInflater();
   	FrameLayout frameHinchado = new FrameLayout(contexto);
   	frameHinchado.addView(inflater.inflate(R.layout.ventana_unico, frameHinchado, false));

   	View view = frameHinchado.getChildAt(0);
   	//Frame hinchado.............................
   	
   	//Imagen de previsualizaci�n.....................................................................
   	imagenView_previsualizacion = (ImageView) view.findViewById(R.id.imageView_previsualizacion);
   	Capa aux_prevCapa = new Capa (contexto);
   	Capa prevCapa = new Capa (contexto);
   	aux_prevCapa.cargar_imagen(capa_a_tratar.obtener_bitmapResultante());
   	aux_prevCapa.ajustar_imagen_a(100, 100);
   	prevCapa.cargar_imagen(aux_prevCapa.obtener_bitmapResultante());
   	prevCapa.cambiar_color(orden);
	imagenView_previsualizacion.setImageBitmap(prevCapa.obtener_bitmapResultante());
	//Imagen de previsualizaci�n.....................................................................
	
	/*
	    	imagenView_previsualizacion = (ImageView) view.findViewById(R.id.imageView_previsualizacion);
   	Capa prevCapa = new Capa (contexto);
   	prevCapa.cargar_imagen(capa_a_tratar.obtener_bitmapResultante());
   	prevCapa.ajustar_imagen_a(100, 100);
   	prevCapa.cambiar_color(orden);
	imagenView_previsualizacion.setImageBitmap(prevCapa.obtener_bitmapResultante());
	 */
	
	
   	//Di�logo....................................
   	new AlertDialog.Builder(contexto)
   	.setPositiveButton(getContext().getString(R.string.Procesar), dialogoListener)
   	.setNegativeButton(getContext().getString(R.string.Cancelar), null)
   	.setView(frameHinchado)
   	.setTitle(titulo)
   	.show();
   	//Di�logo....................................
   }
   
   public abstract void ventana_aceptada ();
	
}