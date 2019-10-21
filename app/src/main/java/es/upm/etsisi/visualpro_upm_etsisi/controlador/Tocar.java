package es.upm.etsisi.visualpro_upm_etsisi.controlador;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/** Clase encargada en implementar el Touch en cada capa
 * @author Ramón Invarato Menéndez
 * @author Roberto Sáez Ruiz
 */
public abstract class Tocar extends View {

	private GestureDetector detectorDeGestos;
	private ScaleGestureDetector detectorDeEscalaDeGestos;

	private List<Long> idxCapasActivas = new LinkedList<>();

	private final int control_radio_dedo_esquina = 50;
	private int x_sel=0, y_sel=0, x2_sel=100, y2_sel=100;
	private RectF rectangulo_sel = null;
	private Paint paintSelecionado, paintPulsado;
	private final int control_anchuraLineaSeleccion = 2;

	private boolean mapa_de_todo_activado = false;


	private ACCION_SCROLL accionScroll = ACCION_SCROLL.MOVERSE;

	public enum ACCION_SCROLL {
		MOVERSE,
//		CAMBIAR_TAMANIO,
//		MAPA_DE_TODO,
//		CAMBIAR_TAMANIO_PANTALLA,
		SELECCIONARSE
	}

	private enum MOVER {
		PREPARADO,
		MOVIENDO,
//		PARADO,
//		ANULADO
	}

	private enum SELECCIONAR {
		NADA,
		SUP_DER,
		SUP_IZQ,
		INF_DER,
		INF_IZQ,
		CENTRO
	}



	private boolean imagenEnPantalla = false;	//Para cambiarlo por usuario

	public Tocar(Context contexto, View contenedor) {
		super(contexto);

		crear_borde ();
		detectorDeGestos = new GestureDetector(contexto,  new GestosListener(this));
		detectorDeEscalaDeGestos = new ScaleGestureDetector(contexto, new ScaleGestosListener(this));
		((ViewGroup) contenedor).addView(this);
	}

	public void establecer_idxCapaActiva (long idxCapaActiva){
		Log.v("test", "establecer_idxCapaActiva idxCapaActiva:"+idxCapaActiva);

		this.idxCapasActivas.add(idxCapaActiva);
	}

	public boolean is_idxCapaActiva (long idxCapaActiva){
		return this.idxCapasActivas.contains(idxCapaActiva);
	}


	public int idxCapaActiva_cantidad (){
		return this.idxCapasActivas.size();
	}

	public void quitar_idxCapaActiva (long idxCapaActiva){
		this.idxCapasActivas.remove(idxCapaActiva);
	}

	public void quitar_todas_idxCapaActiva (){
		this.idxCapasActivas = new LinkedList<>();
	}

	public List<Long> get_idxCapasActivas(){
		return this.idxCapasActivas;
	}

	public abstract Capa getCapa(long idxCapa);

	public void set_idxCapasActivas(List<Long> idxCapasActivas){
		this.idxCapasActivas = idxCapasActivas;
	}

	/** Establece la acción para el touch
	 * @param accion Acción del touch de tipo ACCION_SCROLL
	 */
	public void set_accion_scroll (ACCION_SCROLL accion){
		accionScroll = accion;
		invalidate();
	}

	/** devuelve las coordenadas del recuadro de la selección actual
	 * @return devuelve un vector de 4 posiciones: {x1, y1, x2, y2}
	 */
	public int[] get_seleccion (){
		return new int[]{x_sel+control_anchuraLineaSeleccion, y_sel+control_anchuraLineaSeleccion, x2_sel-control_anchuraLineaSeleccion, y2_sel-control_anchuraLineaSeleccion};
	}


	/** Recortar: Crea el cuadrado rojo con los cuadrados verdes de recorte
	 *
	 */
	private void crear_borde (){
		paintSelecionado = new Paint(Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG);
		paintSelecionado.setColor(Color.RED);	//Colorear la figura
		paintSelecionado.setStyle(Style.STROKE);	//Define el estilo: Borde (sin relleno)
		paintSelecionado.setStrokeWidth(control_anchuraLineaSeleccion);	//Anchura del borde

		rectangulo_sel = new RectF();

		//Crear_cuadro_verde
		paintPulsado = new Paint(Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG);
		paintPulsado.setColor(Color.GREEN);
		paintPulsado.setStrokeWidth(3);

// TODO Para cambiar los cuadrados verdes y hacer el path: https://medium.com/androiddevelopers/playing-with-paths-3fbc679a6f77
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.v("test", "onDraw accionScroll: " + accionScroll);


		if ( (accionScroll== ACCION_SCROLL.SELECCIONARSE) && (rectangulo_sel!=null) ){
			Log.v("test", "onDraw: SELECCIONARSE ENTRA");
			rectangulo_sel.set(x_sel, y_sel, x2_sel, y2_sel);
			canvas.drawRect(rectangulo_sel, paintSelecionado);

			canvas.drawRect(x_sel-control_radio_dedo_esquina, y_sel-control_radio_dedo_esquina, x_sel, y_sel, paintPulsado);
			canvas.drawRect(x2_sel, y_sel-control_radio_dedo_esquina, x2_sel+control_radio_dedo_esquina, y_sel, paintPulsado);
			canvas.drawRect(x_sel-control_radio_dedo_esquina, y2_sel, x_sel, y2_sel+control_radio_dedo_esquina, paintPulsado);
			canvas.drawRect(x2_sel, y2_sel, x2_sel+control_radio_dedo_esquina, y2_sel+control_radio_dedo_esquina, paintPulsado);
			this.setZ(999999999);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		global_eventos (event);
		if (event.getPointerCount()==2){
			cambiar_tamanio_dos_dedos (event);
			return true;
		}

		return detectorDeEscalaDeGestos.onTouchEvent(event) && detectorDeGestos.onTouchEvent(event);
	}

	private void global_eventos (MotionEvent event){
		final int action = event.getActionMasked();

		switch (action) {
			case MotionEvent.ACTION_DOWN: { //Primer dedo pulsa la pantalla
				final int pointerIndex = event.getActionIndex();
				id_dedoA = event.getPointerId(pointerIndex);

				x_d1_ant = event.getX(pointerIndex);
				y_d1_ant = event.getY(pointerIndex);

				Log.v("test", "ACTION_DOWN pointerIndex:"+pointerIndex+" id_dedoA:"+id_dedoA+" x:"+x_d1_ant+" y:"+y_d1_ant);
				break;
			}

			case MotionEvent.ACTION_POINTER_DOWN: { //Dedo que no es el primero pulsa la pantalla
				final int pointerIndex = event.getActionIndex();
				id_dedoB = event.getPointerId(pointerIndex);

				x_d2_ant = event.getX(pointerIndex);
				y_d2_ant = event.getY(pointerIndex);

				// distancia_entre_dos_puntos = raiz_cuadrada( (x2-x1)^2 + (y2-y1)^2)
				distancia_2_dedos_ant = (float) Math.sqrt( (Math.pow( (x_d2_ant-x_d1_ant), 2 ) ) + (Math.pow( (y_d2_ant-y_d1_ant), 2 ) ) );
				Log.v("test", "ACTION_POINTER_DOWN pointerIndex:"+pointerIndex+" id_dedoB:"+id_dedoB+" x:"+x_d2_ant+" y:"+y_d2_ant + "distancia_2_dedos_ant:"+distancia_2_dedos_ant);
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				Log.v("test", "ACTION_MOVE");
		    	/*
		    	if (id_dedo1 == -1){
		    		final int pointerIndex = event.getActionIndex(event);
		    		id_dedo1 = event.getPointerId(pointerIndex);
		    	}

		    	if (id_dedo2 == -1){
		    		final int pointerIndex = event.getActionIndex(event);
		    		id_dedo2 = event.getPointerId(pointerIndex);
		    	}

		    	*/
				break;
			}

			case MotionEvent.ACTION_UP: {
				final int pointerIndex = event.getActionIndex();
				int dedo_id = event.getPointerId(pointerIndex);
				Log.v("test", "A ACTION_UP pointerIndex:"+pointerIndex+" dedo_id:"+dedo_id+ " x_d1_ant:"+x_d1_ant+" y_d1_ant:"+y_d1_ant);
				id_dedoA = INVALIDAR_PUNTERO_ID;

		    	/*
		    	final int pointerIndex = event.getActionIndex(event);
	    		int es_id_dedo = event.getPointerId(pointerIndex);

		    	if (es_id_dedo==id_dedo1)
		    		id_dedo1 = -1;

		    	if (es_id_dedo==id_dedo2)
		    		id_dedo2 = -1;
		    		*/
				break;
			}

			case MotionEvent.ACTION_POINTER_UP: {
		    	/*
	    		final int index_dedo1 = 0;
	    		final int pointerIndex = event.getActionIndex(event);
	    		int es_id_dedo = event.getPointerId(pointerIndex);
	    		*/


				final int pointerIndex = event.getActionIndex();

				int dedo_id = event.getPointerId(pointerIndex);

//				Log.v("test", "A ACTION_POINTER_UP pointerIndex:"+pointerIndex+" dedo_id:"+dedo_id+ " x_d1_ant:"+x_d1_ant+" y_d1_ant:"+y_d1_ant);
				if (dedo_id==id_dedoA){
					x_d1_ant = x_d2_ant;
					y_d1_ant = y_d2_ant;
					id_dedoA = id_dedoB;
				}
				//int id_dedo = event.getPointerId(pointerIndex);
				Log.v("test", "B ACTION_POINTER_UP pointerIndex:"+pointerIndex+" dedo_id:"+dedo_id+ " x_d1_ant:"+x_d1_ant+" y_d1_ant:"+y_d1_ant);

				//Log.v("test", " x_d2_ant:"+x_d1_ant+" y_d2_ant:"+y_d1_ant);

				id_dedoB = INVALIDAR_PUNTERO_ID;

		    	/*
		    	final int pointerIndex = event.getActionIndex(event);
	    		int es_id_dedo = event.getPointerId(pointerIndex);

		    	if (es_id_dedo==id_dedo1)
		    		id_dedo1 = -1;

		    	if (es_id_dedo==id_dedo2)
		    		id_dedo2 = -1;
		    		*/
				break;
			}

			case MotionEvent.ACTION_CANCEL: {
				id_dedoA = INVALIDAR_PUNTERO_ID;
				id_dedoB = INVALIDAR_PUNTERO_ID;
				break;
			}

		}

	}



	private final int INVALIDAR_PUNTERO_ID = -1;
	private float x_d1_ant=-1f, y_d1_ant=-1f, x_d2_ant=-1f, y_d2_ant=-1f;
	private int id_dedoA=INVALIDAR_PUNTERO_ID, id_dedoB=INVALIDAR_PUNTERO_ID;
	private int pointerIndexA=INVALIDAR_PUNTERO_ID, pointerIndexB=INVALIDAR_PUNTERO_ID;
	private float distancia_2_dedos_ant = -1f;

//	private int escalar_rotar_deformar = 1; //escalar=1, deformar=2, rotar=3

	public enum ACCION_DOS_DEDOS {
		ESCALAR,
		ROTAR,
		DEFORMAR
	}
	private ACCION_DOS_DEDOS escalar_rotar_deformar = ACCION_DOS_DEDOS.ESCALAR;

	private void cambiar_tamanio_dos_dedos (MotionEvent event){
		final int action = event.getActionMasked();


		switch (action) {
			case MotionEvent.ACTION_DOWN: { //Primer dedo pulsa la pantalla
				break;
			}

			case MotionEvent.ACTION_POINTER_DOWN: {
				break;
			}

			case MotionEvent.ACTION_MOVE: {

//					final int pointerIndex = event.getActionIndex();
//					int dedo_id = event.getPointerId(pointerIndex);
//
//					final int pointerIndex1 = event.findPointerIndex(id_dedoA);
//					final float x_d1 = event.getX(pointerIndex1);
//					final float y_d1 = event.getY(pointerIndex1);
//					//Log.v("test", "MOVER A index_dedo:"+index_dedo1+" x:"+x_d1+" y:"+y_d1);
//
//					final int pointerIndex2 = event.findPointerIndex(id_dedoB);
//					final float x_d2 = event.getX(pointerIndex2);
//					final float y_d2 = event.getY(pointerIndex2);
//					//Log.v("test", "MOVER A index_dedo:"+index_dedo2+" x:"+x_d2+" y:"+y_d2);
//
//					float dx_d1 = x_d1_ant-x_d1;
//					float dy_d1 = y_d1_ant-y_d1;
//					float dx_d2 = x_d2_ant-x_d2;
//					float dy_d2 = y_d2_ant-y_d2;

				/*
				if (escalar_rotar_deformar==2){
					float x = capaActiva.img_left;
					float y = capaActiva.img_top;
					float x2 = capaActiva.img_right;
					float y2 = capaActiva.img_bottom;

					float x_nueva = -1;
					float y_nueva = -1;
					float x2_nueva = -1;
					float y2_nueva = -1;

					float ampliar_disminuir = -1;

					if (x_d1<x_d2){ //dedo1 izquierda y dedo2 derecha
						x_nueva = x+dx_d1;
						y_nueva = y+dy_d1;
						x2_nueva = x2+dx_d2;
						y2_nueva = y2+dy_d2;
					}else{ //dedo2 izquierda y dedo1 derecha
						x_nueva = x+dx_d2;
						y_nueva = y+dy_d2;
						x2_nueva = x2+dx_d1;
						y2_nueva = y2+dy_d1;
					}

					float distancia_2_dedos = (float) Math.pow( (float) (Math.pow( (x_d2-x_d1), 2 ) ) + (float) (Math.pow( (y_d2-y_d1), 2 ) ), 0.5);

					float dist_ampliar_disminuir = distancia_2_dedos - distancia_2_dedos_ant;
					Log.v("test", "dist_ampliar_disminuir: "+dist_ampliar_disminuir);
					capaActiva.rotar_tanto_como(dist_ampliar_disminuir);

					distancia_2_dedos_ant = distancia_2_dedos;
				}
				*/
				Log.v("test", "escalar_rotar_deformar: "+escalar_rotar_deformar);

				final int pointerIndex = event.getActionIndex();
				int dedo_id = event.getPointerId(pointerIndex);

				final int pointerIndex1 = event.findPointerIndex(id_dedoA);
				final float x_d1 = event.getX(pointerIndex1);
				final float y_d1 = event.getY(pointerIndex1);

				final int pointerIndex2 = event.findPointerIndex(id_dedoB);
				final float x_d2 = event.getX(pointerIndex2);
				final float y_d2 = event.getY(pointerIndex2);

				float dx_d1 = x_d1_ant-x_d1;
				float dy_d1 = y_d1_ant-y_d1;
				float dx_d2 = x_d2_ant-x_d2;
				float dy_d2 = y_d2_ant-y_d2;

				x_d1_ant = x_d1;
				y_d1_ant = y_d1;
				x_d2_ant = x_d2;
				y_d2_ant = y_d2;

				float dx = dx_d1-dx_d2;
				float dy = dy_d1-dy_d2;

				if (x_d1<x_d2){ //dedoA izquierda y dedoB derecha
					dx = -dx;
				}

				if (y_d1<y_d2){ //dedoA arriba y dedoB abajo
					dy = -dy;
				}

				for (long idxCapasActiva: this.idxCapasActivas) {
					Capa capaActiva = this.getCapa(idxCapasActiva);

					Log.v("test", "cambiar_tamanio_dos_dedos capasActivas: "+capaActiva);


					switch (escalar_rotar_deformar) {
						case ESCALAR: {
//							float dx = dx_d1-dx_d2;
//							float dy = dy_d1-dy_d2;
//
//							if (x_d1<x_d2){ //dedoA izquierda y dedoB derecha
//								dx = -dx;
//							}
//
//							if (y_d1<y_d2){ //dedoA arriba y dedoB abajo
//								dy = -dy;
//							}


	//						float nPosLeft = capaActiva.img_left+dx;
	////						nPosLeft = nPosLeft < 0 ? 1: nPosLeft;
	//
	//						float nPosTop = capaActiva.img_top+dy;
	////						nPosTop = nPosTop < 0 ? 1: nPosTop;
	//
	//						float nPosRight = capaActiva.img_right-dx;
	////						nPosRight = nPosRight < 0 ? 1: nPosRight;
	//
	//						float nPosBottom = capaActiva.img_bottom-dy;
	////						nPosBottom = nPosBottom < 0 ? 1: nPosBottom;
	//
	//						if (nPosLeft >= nPosRight) {
	//							nPosLeft = capaActiva.img_left;
	//							nPosRight = capaActiva.img_right;
	//						}
	//
	//						if (nPosTop >= nPosBottom) {
	//							nPosBottom = capaActiva.img_bottom;
	//							nPosTop = capaActiva.img_top;
	//						}

	//						Log.v("test", "escalar_rotar_deformar ESCALAR nPosLeft:" + nPosLeft +"nPosTop:" + nPosTop +"nPosRight:" + nPosRight +"nPosBottom:" + nPosBottom);
	//
	//						capaActiva.transformar_a ( new RectF(nPosLeft, nPosTop,	nPosRight, nPosBottom), Matrix.ScaleToFit.CENTER );


							Log.v("test", "ESCALAR accionScroll: "+accionScroll.toString());

							// TODO mover y escalar cambiar a capas seleccionadas
	//						if (accionScroll == ACCION_SCROLL.MAPA_DE_TODO){
	//							ampliar_pantalla(dx, dy);
	//						} else {
								float nPosLeft = capaActiva.getCurImgLeft()+dx;
								float nPosTop = capaActiva.getCurImgTop()+dy;
								float nPosRight = capaActiva.getCurImgRight()-dx;
								float nPosBottom = capaActiva.getCurImgBottom()-dy;

								if (nPosLeft >= nPosRight) {
									nPosLeft = capaActiva.getCurImgLeft();
									nPosRight = capaActiva.getCurImgRight();
								}

								if (nPosTop >= nPosBottom) {
									nPosBottom = capaActiva.getCurImgBottom();
									nPosTop = capaActiva.getCurImgTop();
								}

								Log.v("test", "escalar_rotar_deformar ESCALAR nPosLeft:" + nPosLeft +"nPosTop:" + nPosTop +"nPosRight:" + nPosRight +"nPosBottom:" + nPosBottom);

								capaActiva.transformar_a( new RectF(nPosLeft, nPosTop,	nPosRight, nPosBottom), Matrix.ScaleToFit.CENTER );

//								Log.v("test", "escalar_rotar_deformar ESCALAR dx:" + dx +"dy:" + dy);


//								capaActiva.cambiar_tamanio_imagen(dx, dy);


	//						}


							//						capaActiva.transformar_a ( new RectF(capaActiva.img_left+dx, capaActiva.img_top+dy, capaActiva.img_right-dx, capaActiva.img_bottom-dy), Matrix.ScaleToFit.CENTER );

							break;
						}
						case DEFORMAR: {
							// TODO terminar
//							float dx = dx_d1-dx_d2;
//							float dy = dy_d1-dy_d2;
//
//							if (x_d1<x_d2){ //dedoA izquierda y dedoB derecha
//								dx = -dx;
//							}
//
//							if (y_d1<y_d2){ //dedoA arriba y dedoB abajo
//								dy = -dy;
//							}

							capaActiva.transformar_a ( new RectF(
									capaActiva.getCurImgLeft()+dx,
									capaActiva.getCurImgTop()+dy,
									capaActiva.getCurImgRight()-dx,
									capaActiva.getCurImgBottom()-dy),
									Matrix.ScaleToFit.FILL );

							break;
						}
						case ROTAR: {
							// TODO terminar
							float grados = x_d1;
							capaActiva.rotar_tanto_como (grados);
						/*
						float x = capaActiva.img_left;
						float y = capaActiva.img_top;
						float x2 = capaActiva.img_right;
						float y2 = capaActiva.img_bottom;

						float x_nueva = -1;
						float y_nueva = -1;
						float x2_nueva = -1;
						float y2_nueva = -1;

						if (x_d1<x_d2){ //dedo1 izquierda y dedo2 derecha
							x_nueva = x+dx_d1;
							y_nueva = y+dy_d1;
							x2_nueva = x2+dx_d2;
							y2_nueva = y2+dy_d2;
						}else{ //dedo2 izquierda y dedo1 derecha
							x_nueva = x+dx_d2;
							y_nueva = y+dy_d2;
							x2_nueva = x2+dx_d1;
							y2_nueva = y2+dy_d1;
						}

						float distancia_2_dedos = (float) Math.pow( (float) (Math.pow( (x_d2-x_d1), 2 ) ) + (float) (Math.pow( (y_d2-y_d1), 2 ) ), 0.5);

						float dist_ampliar_disminuir = distancia_2_dedos - distancia_2_dedos_ant;
						Log.v("test", "dist_ampliar_disminuir: "+dist_ampliar_disminuir);

						capaActiva.transformar_a ( new RectF(x_nueva, y_nueva, x2_nueva, y2_nueva), Matrix.ScaleToFit.CENTER );

						distancia_2_dedos_ant = distancia_2_dedos;
						*/
							break;
						}

					}
				}

				break;
			}

			case MotionEvent.ACTION_UP: {
				break;
			}

			case MotionEvent.ACTION_POINTER_UP: {
				break;
			}

			case MotionEvent.ACTION_CANCEL: {
				break;
			}

		}

	}


	private void mover_un_dedo (MotionEvent event){

		final int action = event.getActionMasked();

		for (long idxCapasActiva: this.idxCapasActivas) {
			Capa capaActiva = this.getCapa(idxCapasActiva);

			switch (action) {
				case MotionEvent.ACTION_DOWN: { //Primer dedo pulsa la pantalla
					break;
				}

				case MotionEvent.ACTION_MOVE: {
					/*
					int cantidad_dedos_sobre_pantalla = event.getPointerCount();

					if (cantidad_dedos_sobre_pantalla==1){*/
					//final int index_dedo1 = event.findPointerIndex(id_dedo1);
					final int index_dedo1 = 0;
					final float x_d1 = event.getX(index_dedo1);
					final float y_d1 = event.getY(index_dedo1);


					final int pointerIndex = event.getActionIndex();
					int dedo_id = event.getPointerId(pointerIndex);
					Log.v("test", "MOVER A pointerIndex:" + pointerIndex + " dedo_id:" + dedo_id + " x:" + x_d1 + " y:" + y_d1);


					float dx_d1 = x_d1 - x_d1_ant;
					float dy_d1 = y_d1 - y_d1_ant;

					capaActiva.desplazar_tanto_como(dx_d1, dy_d1);
					//				capaActiva.mover_a(dx_d1, dy_d1);

					x_d1_ant = x_d1;
					y_d1_ant = y_d1;
					//}

					break;
				}

				case MotionEvent.ACTION_UP: {
					break;
				}

				case MotionEvent.ACTION_CANCEL: {
					break;
				}

			}
		}



	}

	//*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



	/** Clase interna encargada de gestionar el Touch
	 *
	 *
	 */

	protected class GestosListener implements GestureDetector.OnGestureListener {
		private MOVER mover = MOVER.PREPARADO;

		private SELECCIONAR sel = SELECCIONAR.NADA;

		private Tocar mtocar;

		public GestosListener(Tocar mtocar){
			this.mtocar = mtocar;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			sel = SELECCIONAR.NADA;
			mover = MOVER.PREPARADO;
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			Log.v("test", "onScroll accionScroll: " + accionScroll);

			//		TODO moverlo a detectorDeGestos
			if (accionScroll!= ACCION_SCROLL.SELECCIONARSE){
				global_eventos (e1);

				int cantidad_dedos_sobre_pantalla = e1.getPointerCount();

				if (cantidad_dedos_sobre_pantalla==1){
					mover_un_dedo (e1);
				} else if (cantidad_dedos_sobre_pantalla==2){
					cambiar_tamanio_dos_dedos (e1);
				}
			}
//		TODO moverlo a detectorDeGestos


			switch (accionScroll){
				case MOVERSE:{
					mover_imagen_donde_el_dedo (e1, e2, distanceX, distanceY);
					break;}

//				case CAMBIAR_TAMANIO:{
//					escalar_imagen_dos_dedos(e1, e2, distanceX, distanceY);
//					break;}

//				case MAPA_DE_TODO:{
//
//					// TODO temporal hasta que deshaga el lío de los punteros de desplazar
//					if (e1.getPointerCount()>=2){
//						ampliar_pantalla (distanceX, distanceY);
//					} else {
//						mover_pantalla (distanceX, distanceY);
//					}



//					break;}

//				case CAMBIAR_TAMANIO_PANTALLA:{
//					ampliar_pantalla(distanceX, distanceY);
//					break;}

				case SELECCIONARSE:{
					seleccionar_area (e1, e2, distanceX, distanceY);
					break;}
			}


			return false;
		}


		//-------------------------------


		/** Se mueve la imagen a donde esté el dedo
		 * @param e1 El primer evento cuando se empieza con el scroll
		 * @param e2 El evento actual lanzado en cada iteracción con el scroll
		 * @param distanceX Devuelve la cantidad de espacio en la horizontal que se ha desplazado desde la anterior vez
		 * @param distanceY Devuelve la cantidad de espacio en la vertical que se ha desplazado desde la anterior vez
		 */
		private void mover_imagen_donde_el_dedo (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			Log.v("test", "mover_imagen_donde_el_dedo e1: " + e1 + " distanceX: " + distanceX + " distanceY: " + distanceY);

			for (long idxCapasActiva: mtocar.get_idxCapasActivas()) {
				Capa capaActiva = mtocar.getCapa(idxCapasActiva);

				Log.v("test", "mover_imagen_donde_el_dedo is_PixelEnImagen: " + capaActiva.is_PixelEnImagen(e1.getX(), e1.getY()));

				switch (mover) {
					case PREPARADO:{
						Log.v("test", "mover_imagen_donde_el_dedo PREPARADO MOVIENDO");
						mover = MOVER.MOVIENDO;
						break;}
					case MOVIENDO:{
						Log.v("test", "mover_imagen_donde_el_dedo MOVIENDO mover_a");
//						capaActiva.mover_a(capaActiva.getCurImgLeft()-distanceX, capaActiva.getCurImgTop()-distanceY);
						capaActiva.desplazar_tanto_como(-distanceX, -distanceY);
						break;}
				}
			}
		}


		/** Método encargado en seleccionar un área de recorte
		 * @param e1 El primer evento cuando se empieza con el scroll
		 * @param e2 El evento actual lanzado en cada iteracción con el scroll
		 * @param distanceX Devuelve la cantidad de espacio en la horizontal que se ha desplazado desde la anterior vez
		 * @param distanceY Devuelve la cantidad de espacio en la vertical que se ha desplazado desde la anterior vez
		 */
		private void seleccionar_area(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			Log.v("test", "seleccionar_area: distanceX"+distanceX+" distanceY"+distanceY);

			if (sel == SELECCIONAR.NADA){

				//Esquina superior izquierda
				if ( ( (x_sel-control_radio_dedo_esquina)<=e1.getX() ) && ( e1.getX()<=x_sel )  && ( (y_sel-control_radio_dedo_esquina)<=e1.getY() ) && ( e1.getY()<=y_sel ) ){
					sel = SELECCIONAR.SUP_IZQ;

					//Esquina superior derecha
				}else if ( ( (x2_sel)<=e1.getX() ) && ( e1.getX()<=(x2_sel+control_radio_dedo_esquina) ) && ( (y_sel-control_radio_dedo_esquina)<=e1.getY() ) && ( e1.getY()<=y_sel ) ){
					sel = SELECCIONAR.SUP_DER;

					//Esquina inferior izquierda
				}else if ( ( (x_sel-control_radio_dedo_esquina)<=e1.getX() ) && ( e1.getX()<=x_sel ) && ( (y2_sel)<=e1.getY() ) && ( e1.getY()<=(y2_sel+control_radio_dedo_esquina) ) ){
					sel = SELECCIONAR.INF_IZQ;

					//Esquina inferior derecha
				}else if ( ( x2_sel<=e1.getX() ) && ( e1.getX()<=(x2_sel+control_radio_dedo_esquina) ) && ( y2_sel<=e1.getY() ) && ( e1.getY()<=(y2_sel+control_radio_dedo_esquina) ) ){
					sel = SELECCIONAR.INF_DER;

					//Centro
				}else if ( ( x_sel<e1.getX() ) && ( e1.getX()<x2_sel ) && ( y_sel<e1.getY() ) && ( e1.getY()<y2_sel ) ){
					sel = SELECCIONAR.CENTRO;
					//Si no se ha seleccionado nada se mueve la pantalla
				}

			}else{

				switch (sel){
					case SUP_IZQ:{
						x_sel -= distanceX;
						y_sel -= distanceY;
						break;}
					case SUP_DER:{
						y_sel -= distanceY;
						x2_sel -= distanceX;
						break;}
					case INF_IZQ:{
						x_sel -= distanceX;
						y2_sel -= distanceY;
						break;}
					case INF_DER:{
						x2_sel -= distanceX;
						y2_sel -= distanceY;
						break;}
					case CENTRO:{
						x_sel -= distanceX;
						y_sel -= distanceY;
						x2_sel -= distanceX;
						y2_sel -= distanceY;
						break;}
				}


				//Controla que el cuadro de selección no se pase de lado ni se mueva

				int sep_min = (control_anchuraLineaSeleccion*2)-1;

				if (x_sel+sep_min>=x2_sel){
					switch (sel){
						case SUP_IZQ:{
							x_sel = x2_sel-sep_min;
							break;}
						case SUP_DER:{
							x2_sel = x_sel+sep_min;
							break;}
						case INF_IZQ:{
							x_sel = x2_sel-sep_min;
							break;}
						case INF_DER:{
							x2_sel = x_sel+sep_min;
							break;}
					}
				}
				if (y_sel+sep_min>=y2_sel){
					switch (sel){
						case SUP_IZQ:{
							y_sel = y2_sel-sep_min;
							break;}
						case SUP_DER:{
							y_sel = y2_sel-sep_min;
							break;}
						case INF_IZQ:{
							y2_sel = y_sel+sep_min;
							break;}
						case INF_DER:{
							y2_sel = y_sel+sep_min;
							break;}
					}

				}

			}


			invalidate();
		}



		//Métodos redefinidos no usados.............................................
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}


	}


	protected class ScaleGestosListener implements ScaleGestureDetector.OnScaleGestureListener {

		private Tocar mtocar;

		public ScaleGestosListener(Tocar mtocar){
			this.mtocar = mtocar;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			Log.v("test", "onScale detector: " + detector);

//			final int pointerIndex = event.getActionIndex();
//			int dedo_id = event.getPointerId(pointerIndex);
//
//			final int pointerIndex1 = event.findPointerIndex(id_dedoA);
//			final float x_d1 = event.getX(pointerIndex1);
//			final float y_d1 = event.getY(pointerIndex1);
//			//Log.v("test", "MOVER A index_dedo:"+index_dedo1+" x:"+x_d1+" y:"+y_d1);
//
//			final int pointerIndex2 = event.findPointerIndex(id_dedoB);
//			final float x_d2 = event.getX(pointerIndex2);
//			final float y_d2 = event.getY(pointerIndex2);
//			//Log.v("test", "MOVER A index_dedo:"+index_dedo2+" x:"+x_d2+" y:"+y_d2);
//
//			float dx_d1 = x_d1_ant-x_d1;
//			float dy_d1 = y_d1_ant-y_d1;
//			float dx_d2 = x_d2_ant-x_d2;
//			float dy_d2 = y_d2_ant-y_d2;

//			mScaleFactor *= detector.getScaleFactor();
//
//			// Don't let the object get too small or too large.
////			mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.0f));
//			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//
//			capaActiva.escalar_a(mScaleFactor);

//			capaActiva.escalar_a(detector.getScaleFactor());

//			invalidate();
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			Log.v("test", "onScaleBegin detector: " + detector);
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			Log.v("test", "onScaleEnd detector: " + detector);
		}
	}



}
