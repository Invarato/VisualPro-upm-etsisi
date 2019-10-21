package es.upm.etsisi.visualpro_upm_etsisi.Modelo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import es.upm.etsisi.visualpro_upm_etsisi.R;


/** Clase encargada de gestionar la ventana emergente
 * @author Ramón Invarato Menéndez
 * @author Roberto Pérez
 */
public abstract class ventana_argb extends Dialog {
	private Context contexto;
	
	private EditText edittext_A, edittext_R, edittext_G, edittext_B;
	private SeekBar seekbar_A, seekbar_R, seekbar_G, seekbar_B;
	
	private int valor_max=255;
	
	private String titulo="ARGB";
	
	public ventana_argb (Context contexto, int ini_A, int ini_R, int ini_G, int ini_B){
		super (contexto);
		this.contexto = contexto;
		mensaje (ini_A, ini_R, ini_G, ini_B);
	}

   private void mensaje (int ini_A, int ini_R, int ini_G, int ini_B){
   	OnClickListener dialogoListener = new OnClickListener() {
   		public void onClick(DialogInterface dialog, int which) {
   			try {
   				ventana_aceptada (Integer.parseInt(edittext_A.getText()+""), Integer.parseInt(edittext_R.getText()+""), Integer.parseInt(edittext_G.getText()+""), Integer.parseInt(edittext_B.getText()+""));
   			} catch (Exception e) {
   				e.printStackTrace();
   			};
   		}
   	};

   	//Frame hinchado.............................
   	LayoutInflater inflater = this.getLayoutInflater();
   	FrameLayout frameHinchado = new FrameLayout(contexto);
   	frameHinchado.addView(inflater.inflate(R.layout.ventana_argb, frameHinchado, false));

   	View view = frameHinchado.getChildAt(0);
   	//Frame hinchado.............................
   	
   	
   	//  A  -----------------------------------------------------------------------------
   	edittext_A = (EditText) view.findViewById(R.id.EditText_A);
   	edittext_A.setText(""+ini_A);
   	edittext_A.setOnEditorActionListener(new OnEditorActionListener(){
		@Override
		public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
			seekbar_A.setProgress(Integer.parseInt(""+ view.getText()));
			return false;
		}
	});
   	
   	
   	seekbar_A = (SeekBar) view.findViewById(R.id.SeekBar_A);
   	seekbar_A.setMax(valor_max);
   	seekbar_A.setProgress(ini_A);
   	seekbar_A.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
		@Override
		public void onProgressChanged(SeekBar arg0, int progreso, boolean arg2) {
			edittext_A.setText(""+progreso);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	});
   	//  A  -----------------------------------------------------------------------------
   	
   	//  R  -----------------------------------------------------------------------------
   	edittext_R = (EditText) view.findViewById(R.id.EditText_R);
   	edittext_R.setText(""+ini_R);
   	edittext_R.setOnEditorActionListener(new OnEditorActionListener(){
		@Override
		public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
			seekbar_R.setProgress(Integer.parseInt(""+ view.getText()));
			return false;
		}
	});
   	
   	
   	seekbar_R = (SeekBar) view.findViewById(R.id.SeekBar_R);
   	seekbar_R.setMax(valor_max);
   	seekbar_R.setProgress(ini_R);
   	seekbar_R.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
		@Override
		public void onProgressChanged(SeekBar arg0, int progreso, boolean arg2) {
			edittext_R.setText(""+progreso);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	});
   	//  R  -----------------------------------------------------------------------------
   	
   	//  G  -----------------------------------------------------------------------------
   	edittext_G = (EditText) view.findViewById(R.id.EditText_G);
   	edittext_G.setText(""+ini_G);
   	edittext_G.setOnEditorActionListener(new OnEditorActionListener(){
		@Override
		public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
			seekbar_G.setProgress(Integer.parseInt(""+ view.getText()));
			return false;
		}
	});
   	
   	
   	seekbar_G = (SeekBar) view.findViewById(R.id.SeekBar_G);
   	seekbar_G.setMax(valor_max);
   	seekbar_G.setProgress(ini_G);
   	seekbar_G.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
		@Override
		public void onProgressChanged(SeekBar arg0, int progreso, boolean arg2) {
			edittext_G.setText(""+progreso);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	});
   	//  G  -----------------------------------------------------------------------------
   	
   	//  B  -----------------------------------------------------------------------------
   	edittext_B = (EditText) view.findViewById(R.id.EditText_B);
   	edittext_B.setText(""+ini_B);
   	edittext_B.setOnEditorActionListener(new OnEditorActionListener(){
		@Override
		public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
			seekbar_B.setProgress(Integer.parseInt(""+ view.getText()));
			return false;
		}
	});
   	
   	
   	seekbar_B = (SeekBar) view.findViewById(R.id.SeekBar_B);
   	seekbar_B.setMax(valor_max);
   	seekbar_B.setProgress(ini_B);
   	seekbar_B.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
		@Override
		public void onProgressChanged(SeekBar arg0, int progreso, boolean arg2) {
			edittext_B.setText(""+progreso);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	});
   	//  B  -----------------------------------------------------------------------------
   	
   	
   	//Di�logo....................................
   	new AlertDialog.Builder(contexto)
   	.setPositiveButton(getContext().getString(R.string.Procesar), dialogoListener)
   	.setNegativeButton(getContext().getString(R.string.Cancelar), null)
   	.setView(frameHinchado)
   	.setTitle(titulo)
   	.show();
   	//Di�logo....................................
   }
   
   public abstract void ventana_aceptada (int A, int R, int G, int B);
	
}
