package es.upm.etsisi.visualpro_upm_etsisi.controlador;

//import com.VisualPro.Library.Filtros;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import es.upm.etsisi.visualpro_upm_etsisi.R;

import java.io.FileNotFoundException;
import java.io.IOException;


/** Clase encargada de gestionar y realizar transforamciones a una imagen individual.
 * @author Ramón Invarato Menéndez
 * @author Roberto Sáez Ruiz
 */
public class Capa extends View {

	private final String DEBUG_TAG = "test";

	private Bitmap bitmap_original = null;

	private Context contexto;

	private Matrix matrix;

	private RectF rectContImagOriginal, rectContImagPantalla;

	private Paint paint;


	public Capa(Context contexto){
		super (contexto);
		matrix = new Matrix();
		this.contexto = contexto;

		//		TODO quitar
		// TODO paint=new Paint();
		// TODO paint.setColor(Color.RED);
		// TODO paint.setStyle(Style.STROKE);
	}


	@Override
	protected void onDraw(Canvas canvas){
		Log.v("test", "onDraw CAPA");
//TODO		super.onDraw(canvas);
//		if (visibleImagPantalla){
		Log.v("test", "onDraw visibleImagPantalla");
		//Imagen
		canvas.drawBitmap(bitmap_original, matrix, null);

		//		TODO quitar paint borde o darlo como opción
		//Borde
		// TODO canvas.drawRect(rectContImagPantalla, paint);
//		}
	}


	/** Transforma la imagen al rectángulo contenido por las posiciones descritas
	 * @param nuevo_rectContImagPantalla
	 * @param modo_de_transformacion
	 */
	public void transformar_a (RectF nuevo_rectContImagPantalla, Matrix.ScaleToFit modo_de_transformacion){
		Log.v("test", "transformar_a MATRIZ ANTES: ");
		this.DEBUG_mostrar_cont_matriz();

		Log.v("test", "transformar_a rectContImagPantalla: " + rectContImagPantalla);
		Log.v("test", "transformar_a nuevo_rectContImagPantalla: " + nuevo_rectContImagPantalla);

		// Escalar del rectángulo original al que se quiere dibujar
		matrix.setRectToRect(rectContImagOriginal, nuevo_rectContImagPantalla, modo_de_transformacion);

		// Reajustar el rectángulo a las proporciones dibujadas en pantalla
		RectF newRect = new RectF(rectContImagOriginal);
		matrix.mapRect(newRect);

		Log.v("test", "transformar_a MATRIZ Despues: ");
		this.DEBUG_mostrar_cont_matriz();

		actualizar_valores_y_refrescar (newRect);
	}

	private void actualizar_valores_y_refrescar (RectF nuevo_rectContImagPantalla){
		rectContImagPantalla = nuevo_rectContImagPantalla;
		float img_left = rectContImagPantalla.left;
		float img_top = rectContImagPantalla.top;
		float img_right = rectContImagPantalla.right;
		float img_bottom = rectContImagPantalla.bottom;
		float img_anchura = rectContImagPantalla.width();
		float img_altura = rectContImagPantalla.height();

		Log.v("test", "actualizar_valores_y_refrescar img_left: " + img_left + " img_top: " + img_top + " img_right: " + img_right + " img_bottom: " + img_bottom + " img_anchura: " + img_anchura +	" img_altura: " + img_altura);

		invalidate();
	}


	/** Redimensiona la imagen hasta los valores dados. La traslada y la escala. La centra en la view.
	 * @param anchuraAjuste Anchura a la que se quiere ajustar la imagen
	 * @param alturaAjuste Altura a la que se quiere ajustar la imagen
	 * @return Devuelve el porcentaje al que ha cambiado
	 */
	public void ajustar_imagen_a (int anchuraAjuste, int alturaAjuste){
		transformar_a(new RectF(0, 0, anchuraAjuste, alturaAjuste), Matrix.ScaleToFit.CENTER);
	}

//	public void escalar_a(float modFactorDeEscala){
//		Log.v("test", "escalar_a: " + modFactorDeEscala);
////		mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//		this.factorDeEscalaImagPantalla *= modFactorDeEscala;
//
////		float imgOriginal_left = rectContImagOriginal.left *factorDeEscala;
////		float imgOriginal_top = rectContImagOriginal.top *factorDeEscala;
////		float imgOriginal_right = rectContImagOriginal.right *factorDeEscala;
////		float imgOriginal_bottom = rectContImagOriginal.bottom *factorDeEscala;
//
//
//
//		float imgIniPantalla_left = this.rectContImagInicioEnPantalla.left * this.factorDeEscalaImagPantalla;
//		float imgIniPantalla_top = this.rectContImagInicioEnPantalla.top * this.factorDeEscalaImagPantalla;
//		float imgIniPantalla_right = this.rectContImagInicioEnPantalla.right * this.factorDeEscalaImagPantalla;
//		float imgIniPantalla_bottom = this.rectContImagInicioEnPantalla.bottom * this.factorDeEscalaImagPantalla;
//
////		this.factorDeEscala = factorDeEscala;
//		this.transformar_a ( new RectF(img_left, img_top, imgIniPantalla_right, imgIniPantalla_bottom ), Matrix.ScaleToFit.CENTER );
////		this.transformar_a ( new RectF(img_left, img_top, img_right*this.factorDeEscalaImagPantalla, img_bottom*this.factorDeEscalaImagPantalla ), Matrix.ScaleToFit.CENTER );
//	}




	/** Mueve la imagen exactamente a estas coordenadas, siendo el (0,0) de la imagen las posición que se mueve
	 * @param x
	 * @param y
	 */
	public void mover_a (float x, float y){
		Log.v("test", "mover_a x: " + x + " y: " + y);
		float dx = x - rectContImagPantalla.left;
		float dy = y - rectContImagPantalla.top;

		desplazar_tanto_como (dx, dy);
	}

	/** Se desplaza la imagen una distancia determinada
	 * @param dx distancia x a desplazar
	 * @param dy distnacia y a desplazar
	 */
	public void desplazar_tanto_como (float dx, float dy){
		float left = rectContImagPantalla.left + dx;
		float right = rectContImagPantalla.right + dx;
		float top = rectContImagPantalla.top + dy;
		float bottom = rectContImagPantalla.bottom + dy;

		Log.v("test", "desplazar_tanto_como left: " + left + " right: " + right + " top: " + top + " bottom: " + bottom );

		transformar_a(new RectF(left, top, right, bottom), Matrix.ScaleToFit.CENTER);
	}


	private int grados_rotado = 0;

	public void rotar_tanto_como (float grados){
		grados_rotado += grados;

		matrix.setRotate(grados_rotado,
				rectContImagPantalla.left+rectContImagPantalla.centerX(),
				rectContImagPantalla.top+rectContImagPantalla.centerY());

		//TODO actualizar_valores_y_refrescar (RectF nuevo_rectContImagPantalla);
		invalidate();
	}


	private final Thread hilo_cargar_imagen = new Thread();

	public Bitmap obtener_thumbnail (int anchura, int altura){
		while (bitmap_original==null){
			try {
				synchronized (hilo_cargar_imagen){ //Si no tenemos imagen cargada, la esperamos
					hilo_cargar_imagen.wait();
				}
			} catch (InterruptedException ignored) {}
		}

		return Bitmap.createScaledBitmap(bitmap_original, anchura, altura, true);
	}


	/** Carga la imagen de una dirección a memoria para ser tratada
	 * @param uri Dirección URI de la imagen a cargar
	 */
	public void cargar_imagen(Uri uri){
		new CargarImagen(uri).execute(contexto);
		try {
			synchronized (hilo_cargar_imagen){ //Si no tenemos imagen cargada, la esperamos
				hilo_cargar_imagen.wait();
			}
		} catch (InterruptedException ignored) {}
	}


	/** Carga la imagen de una dirección a memoria para ser tratada
	 * @param imagen Carga una imagen de tipo Bitmap.
	 */
	public void cargar_imagen(Bitmap imagen){
		if (imagen!=null){
			bitmap_original = imagen;
			rectContImagOriginal = new RectF(0, 0, bitmap_original.getWidth(), bitmap_original.getHeight());
			actualizar_valores_y_refrescar(rectContImagOriginal);
			generar_previsulizacion_imagen ();
		}else
			Log.v(DEBUG_TAG, getContext().getString(R.string.No_se_ha_introducido_imagen));
	}


	/** Guarda en la dirección por defecto y con el formato por defecto.
	 * @param titulo_imagen título para guardar la imagen que no puede ser null
	 * return si ha ido bien, devuelve la ruta donde se ha guardado. null si ha ocurrido algún error
	 */
	public String guardar_imagen(String titulo_imagen){
		invalidate();
		return MediaStore.Images.Media.insertImage(contexto.getContentResolver(), obtener_bitmapResultante(), titulo_imagen, "");
	}


	/** Comprueba que el píxel dicho forma parte de la imagen.
	 * @param x Coordenada horizontal respecto el View
	 * @param y Coordenada vertical respecto el View
	 * @return TRUE si el pixel forma parte de la imagen. FALSE si es parte del fondo.
	 */
	public boolean is_PixelEnImagen(float x, float y){
		return (getCurImgLeft() <= x && x <= getCurImgLeft() + getCurImgWidth())
				&& (getCurImgTop() <= y && y <= getCurImgTop() + getCurImgHeigth());
	}


	/** Cambiar tamaño imagen. Si valor = 1.0f, no cambia de tamaño. Si 0.0f >= valor < 1.0f, se reduce. Si 1.0f < valor, se amplía. Por ejemplo, 1.5f es el doble de grande, y 0.5f la mitad
	 * @param anchura Cambiar tamaño a lo ancho
	 * @param altura Cambiar tamaño a lo alto
	 */
	public void cambiar_tamanio_imagen (float anchura, float altura){
		if (anchura<0)
			anchura=0.0f;
		if (altura<0)
			altura=0.0f;

		matrix.postScale(anchura, altura);

		// Reajustar el rectángulo a las proporciones dibujadas en pantalla
		RectF newRect = new RectF(rectContImagOriginal);
		matrix.mapRect(newRect);

		actualizar_valores_y_refrescar (newRect);
	}


	/** Cambiar tamaño imagen por razón. Cambia igual ancho que alto.
	 * @param porcentaje_variar Cambiar tamaño por razón en porcentaje (útiles desde -99% a infinito)
	 */
	public void cambiar_tamanio_imagen (int porcentaje_variar){
		float valor = porcentaje_variar;

		if (valor >= 0)
			valor = 1.0f+(valor/100);
		else if (valor <= -100)
			valor = 0.0f;
		else
			valor = ( (100+valor)/100 );

//		valor = Math.max(0, Math.min(valor, 100));

		cambiar_tamanio_imagen (valor, valor);
	}


	/** Crea un recorte de la imagen inicial y lo pone en una capa nueva
	 * @param x Coordenada horizontal superior de la subimagen
	 * @param y Coordenada vertical superior de la subimagen
	 * @param x2 Coordenada horizontal inferior de la subimagen
	 * @param y2 Coordenada vertical inferior de la subimagen
	 * @return Devuelve otra capa con el recorte. Si es null es que no se ha recortado nada por estar fuera de rango
	 */
	public Capa convertir_a_subimagen (int x, int y, int x2, int y2){

		try {
			Bitmap bitmap_aux = obtener_bitmapResultante();

			int x_imgOr = getCurImgLeft();
			int y_imgOr = getCurImgTop();
			int x2_imgOr = x_imgOr + getCurImgWidth ();
			int y2_imgOr = y_imgOr + getCurImgHeigth ();

			if (x_imgOr>x)
				x = x_imgOr;
			if (y_imgOr>y)
				y = y_imgOr;
			if (x2_imgOr<x2)
				x2 = x2_imgOr;
			if (y2_imgOr<y2)
				y2 = y2_imgOr;

			int anchura = x2-x;
			int altura = y2-y;

			int x_trans = x-x_imgOr;
			int y_trans = y-y_imgOr;

			Bitmap bitmap = Bitmap.createBitmap(bitmap_aux, x_trans, y_trans, anchura, altura);

			Capa capa = new Capa (contexto);
			capa.cargar_imagen(bitmap);
			return capa;
		} catch (Exception e) {
			e.printStackTrace();
			Log.v(DEBUG_TAG, getContext().getString(R.string.No_se_han_introducido_bien_los_parametros));
		}

		return null;
	}


	/** Devuelve el Bitmap de la capa. Devuelve solo la imagen mínima, no la posición en la que está en pantalla.
	 * @return Imagen contenida en la capa
	 */
	public Bitmap obtener_bitmapResultante(){
		Log.v("test", "obtener_bitmapResultante bitmap_original.getWidth(): " + bitmap_original.getWidth() + "bitmap_original.getHeight(): " + bitmap_original.getHeight());
		return Bitmap.createBitmap(bitmap_original, 0, 0, bitmap_original.getWidth(), bitmap_original.getHeight(), matrix, true);
	}


	/** La imagen que se pasa como parámetro se pondrá encima de la imagen que está en esta capa. La calidad resultante será la conversión de la imagen final
	 * @param imagen_a_sobresponer Imagen que se quiere sobresponer a esta capa
	 * @param x Posición horizontal de donde se quiere sobresponer
	 * @param y Posición vertical de donde se quiere sobresponer
	 */
	public void sobresponer_imagen (Bitmap imagen_a_sobresponer, int x, int y){
		int top = Math.min(getCurImgTop(), y);
		int left = Math.min(getCurImgLeft(), x);
		int bottom = Math.max(getCurImgTop()+getCurImgHeigth(), y+imagen_a_sobresponer.getHeight());
		int right = Math.max(getCurImgLeft()+getCurImgWidth(), x+imagen_a_sobresponer.getWidth());

		int anchura_resultante = right-left;
		int altura_resultante = bottom-top;

		int x_fondo=getCurImgLeft(), y_fondo=getCurImgTop(), x_encima=x, y_encima=y;

		if (y_fondo==top)
			y_fondo=0;
		else
			y_fondo=y_fondo-top;

		if (x_fondo==left)
			x_fondo=0;
		else
			x_fondo=x_fondo-left;



		if (y_encima==top)
			y_encima=0;
		else
			y_encima=y_encima-top;

		if (x_encima==left)
			x_encima=0;
		else
			x_encima=x_encima-left;

		Bitmap bitmap_nuevo = Bitmap.createBitmap(anchura_resultante, altura_resultante, Bitmap.Config.ARGB_8888);
		Canvas canvas_interno = new Canvas(bitmap_nuevo);
		canvas_interno.drawBitmap(obtener_bitmapResultante(), x_fondo, y_fondo, null);

		canvas_interno.drawBitmap(imagen_a_sobresponer, x_encima, y_encima, null);
		bitmap_original = bitmap_nuevo;

		generar_previsulizacion_imagen ();
	}




	/** Crea una previsualización de la imagen y refresca el dibujo de la view. Se debe de mantener siempre consistente con cualquier método que modifique la imagen en esta capa.
	 */
	public void generar_previsulizacion_imagen (){
		invalidate();
	}



	/** Posición de la imagen en la horizontal
	 * @return pixeles desplazado en la horizontal
	 */
	public int getCurImgLeft (){
		return (int) rectContImagPantalla.left;
	}

	/** Posición de la imagen en la vertical
	 * @return pixeles desplazado en la vertical
	 */
	public int getCurImgTop (){
		return (int) rectContImagPantalla.top;
	}

	/** Posición de la imagen en la horizontal
	 * @return pixeles desplazado en la horizontal
	 */
	public int getCurImgRight (){
		return (int) rectContImagPantalla.right;
	}

	/** Posición de la imagen en la vertical
	 * @return pixeles desplazado en la vertical
	 */
	public int getCurImgBottom (){
		return (int) rectContImagPantalla.bottom;
	}

	/** Devuleve la anchura de la imagen transformada
	 * @return Anchura en píxeles de la imagen
	 */
	public int getCurImgWidth (){
		return (int) rectContImagPantalla.width();
	}

	/** Devuleve la altura de la imagen transformada
	 * @return Altura en píxeles de la imagen
	 */
	public int getCurImgHeigth (){
		return (int) rectContImagPantalla.height();
	}



	private void DEBUG_mostrar_cont_matriz(){
		float[] values = new float[9];
		matrix.getValues(values);
		Log.v("test", " MSCALE_X:"+values[0]+" MSKEW_X:"+values[1]+" MTRANS_X:"+values[2]+" MSKEW_Y:"+values[3]+" MSCALE_Y:"+values[4]+" MTRANS_Y:"+values[5]+" MPERSP_0:"+values[6]+" MPERSP_1:"+values[7]+" MPERSP_2:"+values[8]);
	}







	//.................................................................................................................................


	/**
	 * Método que modifica una imagen completamente. Ya sea solo el color, o algún filtrado.
	 *@param orden Elige la modificación que se va a efectuar
	 */
	public void cambiar_color(CapaFiltros.ORDEN orden){
		this.bitmap_original = CapaFiltros.cambiar_color(contexto, this.bitmap_original, orden);
		generar_previsulizacion_imagen ();
	}


	/**
	 * Método que modifica una imagen según un valor prefijado.
	 *@param orden Elige la modificación que se va a efectuar
	 *@param umbral Elige el umbral sobre el que tiene que actuar el filtro
	 */
	public void cambiar_color_variable(CapaFiltros.ORDEN orden, int umbral){
		this.bitmap_original = CapaFiltros.cambiar_color_variable(contexto, this.bitmap_original, orden, umbral);
		generar_previsulizacion_imagen ();
	}


	private class CargarImagen extends AsyncTask<Context, Integer, Integer> {
		private Uri uri;

		public CargarImagen (Uri uri){
			this.uri = uri;
		}
		protected void onPreExecute() {
		}
		@Override
		protected Integer doInBackground(Context... contexto) {
			try {
				bitmap_original = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Log.v(DEBUG_TAG, getContext().getString(R.string.imagen_no_encontrada_en_la_direccion_expecificada));
			} catch (IOException e) {
				e.printStackTrace();
				Log.v(DEBUG_TAG, getContext().getString(R.string.Error_en_la_entrada_de_datos));
			}

			rectContImagOriginal = new RectF(0, 0,
					bitmap_original.getWidth(),
					bitmap_original.getHeight());
			rectContImagPantalla = rectContImagOriginal;

			transformar_a( new RectF(rectContImagOriginal), Matrix.ScaleToFit.CENTER );

			generar_previsulizacion_imagen ();

			synchronized (hilo_cargar_imagen){ //Notificamos que la imagen ya se ha cargado a memoria
				hilo_cargar_imagen.notifyAll();
			}

			return 100;
		}
		protected void onProgressUpdate (Integer... valores){
		}
		protected void onPostExecute(Integer bytes) {
		}

	}



}

