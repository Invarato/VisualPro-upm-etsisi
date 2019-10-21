package es.upm.etsisi.visualpro_upm_etsisi.Modelo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import es.upm.etsisi.visualpro_upm_etsisi.R;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.Capa;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.CapaFiltros;
//import com.VisualPro.controlador.Capa.ORDEN;


/** Clase encargada de gestionar la ventana emergente
 * @author Ramón Invarato Menéndez
 * @author Roberto Séez
 */
public abstract class ventana_un_valor extends Dialog {
	private Context contexto;
	
	private EditText edittext_num;
	private SeekBar seekbar_num;
	private TextView textView_final, textView_minimo;
	
	private int valor_minimo, valor_maximo;
	
	private ImageView imagenView_previsualizacion;
	
	private Capa capa_a_tratar, aux_prevCapa, prevCapa;
	private CapaFiltros.ORDEN orden; //Si es sepia, etc

	private String titulo="";
	
	private boolean usando_seek = false;
	
	public ventana_un_valor (Context contexto, int valor_min, int valor_inicial, int valor_max, String titulo, Capa capa, CapaFiltros.ORDEN orden){
		super (contexto);
		this.contexto = contexto;
		this.titulo = titulo;
		this.capa_a_tratar = capa;
		this.orden = orden;
		mensaje (valor_min, valor_inicial, valor_max);
	}

   private void mensaje (int valor_min, int valor_inicial, int valor_max){
   	OnClickListener dialogoListener = new OnClickListener() {
   		public void onClick(DialogInterface dialog, int which) {
   			try {
   				ventana_aceptada (Integer.parseInt(edittext_num.getText()+""));
   			} catch (Exception e) {
   				e.printStackTrace();
   			}
   		}
   	};
   	
   	
   	int rango_barra = valor_max - valor_min;
   	int valor_inicial_barra = valor_inicial-valor_min;
   	
   	valor_minimo = valor_min;
   	valor_maximo = valor_max;

   	//Frame hinchado.............................
   	LayoutInflater inflater = this.getLayoutInflater();
   	FrameLayout frameHinchado = new FrameLayout(contexto);
   	frameHinchado.addView(inflater.inflate(R.layout.ventana_un_valor, frameHinchado, false));

   	View view = frameHinchado.getChildAt(0);
   	//Frame hinchado.............................
   	
   	
   	//Imagen de previsualizaci�n.....................................................................
   	imagenView_previsualizacion = (ImageView) view.findViewById(R.id.imageView_previsualizacion2);
   	aux_prevCapa = new Capa (contexto);
   	prevCapa = new Capa (contexto);
   	aux_prevCapa.cargar_imagen(capa_a_tratar.obtener_bitmapResultante());
   	aux_prevCapa.ajustar_imagen_a(100, 100);
   	cambiar_prev (valor_inicial);
	//Imagen de previsualizaci�n.....................................................................
	
	
   	
   	textView_final = (TextView) view.findViewById(R.id.textView_final);
   	textView_final.setText(""+valor_max);
   	
   	textView_minimo = (TextView) view.findViewById(R.id.textView_inicial);
   	textView_minimo.setText(""+valor_min);
   	
   	edittext_num = (EditText) view.findViewById(R.id.editText_num_Embyn);
   	edittext_num.setText(""+valor_inicial);
   	edittext_num.addTextChangedListener(new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!usando_seek){
				int valor = 0;
				try {
					valor = Integer.parseInt(""+s);
				} catch(NumberFormatException nfe) {
					Log.v("test", "Error: "+nfe);
				}
				
				if (valor_maximo<valor)
					valor=valor_maximo;
				else if (valor_minimo>valor)
					valor=valor_minimo;
				
				seekbar_num.setProgress(valor);
				cambiar_prev (valor);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}
	});
   	
   	
   	seekbar_num = (SeekBar) view.findViewById(R.id.seekBar_num_Embyn);
   	seekbar_num.setMax(rango_barra);
   	seekbar_num.setProgress(valor_inicial_barra);
   	seekbar_num.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
		@Override
		public void onProgressChanged(SeekBar arg0, int progreso, boolean arg2) {
			usando_seek = true;
			edittext_num.setText(""+(valor_minimo+progreso));
			cambiar_prev (valor_minimo+progreso);
			usando_seek = false;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	});
   	
   	
   	//Diálogo....................................
   	new AlertDialog.Builder(contexto)
   	.setPositiveButton(getContext().getString(R.string.Procesar), dialogoListener)
   	.setNegativeButton(getContext().getString(R.string.Cancelar), null)
   	.setView(frameHinchado)
   	.setTitle(titulo)
   	.show();
   	//Diálogo....................................
   }
   
   public abstract void ventana_aceptada (int valor);
   
   private void cambiar_prev (int valor_umbral){
	   	prevCapa.cargar_imagen(aux_prevCapa.obtener_bitmapResultante());
	   	prevCapa.cambiar_color_variable(orden, valor_umbral);
		imagenView_previsualizacion.setImageBitmap(prevCapa.obtener_bitmapResultante());
   }
   
	
}