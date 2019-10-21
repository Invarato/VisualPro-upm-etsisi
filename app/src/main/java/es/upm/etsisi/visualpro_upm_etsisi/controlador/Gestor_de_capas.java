package es.upm.etsisi.visualpro_upm_etsisi.controlador;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/** Gestor de capas. Se encarga de almacenar todas las capas y de tratar el touch en cada una cuando sea necesario
 * @author Ramón Invarato Menéndez
 * @author Roberto Pérez
 */
public class Gestor_de_capas extends Tocar {
	private Context contexto;

	private View contenedor;

	private long last_capaid = -1;

	private Map<Long, Capa> idToCapa;
	private List<Long> posIdCapas;


	public Gestor_de_capas(Context contexto, View contenedor) {
		super(contexto, contenedor);
		this.contenedor = contenedor;
		this.contexto = contexto;
		this.idToCapa = new LinkedHashMap<>();
		this.posIdCapas = new LinkedList<>();
	}

	/** Dado un ID devuelve una capa
	 * @param idxCapa ID de la capa que se quiere obtener
	 * @return Capa que se deseaba obtener por el ID. Si no exite devuelve NULL
	 */
	@Override
	public Capa getCapa (long idxCapa){
		if (idToCapa.containsKey(idxCapa))
			return idToCapa.get(idxCapa);
		else
			return null;
	}

	/** Añade una nueva capa y la trae al frente en el árbol View de las capas
	 * @param capa Capa nueva a añadir
	 */
	public long set_capa_aniadir(Capa capa){
		last_capaid += 1;
		this.establecer_idxCapaActiva(last_capaid);

		if (capa == null) {
			capa = new Capa (contexto);
		}

		idToCapa.put(last_capaid, capa);
		posIdCapas.add(last_capaid);

		((ViewGroup) contenedor).addView(capa);

        capa.setZ(posIdCapas.size());

		return last_capaid;
	}

	/** Devuelve la cantidad de capas que existen almacenadas en el gestor de capas
	 * @return cantidad de capas
	 */
	public int getCantidadCapas(){
		return idToCapa.size();
	}

	public List<Long> get_todas_idxCapas(){
		return this.posIdCapas;
	}


	/** Guarda todas las capas sobrespuestas en una sola imagen PNG.
	 * @param nombre_archivo Nombre del archivo final
	 * @return Devuelve la dirección de la ruta en donde se ha guardado
	 */
	public String guardar_todo_en_una_imagen(String nombre_archivo){
		Capa capa_primera = idToCapa.get((long) 0);

		Capa capa_guardar = new Capa (contexto);

		capa_guardar.cargar_imagen(capa_primera.obtener_bitmapResultante());
		capa_guardar.mover_a(capa_primera.getCurImgLeft(), capa_primera.getCurImgTop());

		for (long i : posIdCapas){
			Capa capaATratar = idToCapa.get(i);
			capa_guardar.sobresponer_imagen (capaATratar.obtener_bitmapResultante(), capaATratar.getCurImgLeft(), capaATratar.getCurImgTop());
		}

		return capa_guardar.guardar_imagen(nombre_archivo);
	}

	public void cambiar_posicion_capa(long itemId, long toItemId){
		int posFrom = posIdCapas.indexOf(itemId);
		int posTo = posIdCapas.indexOf(toItemId);

		Log.d("test", "cambiar_posicion_capa itemId: " + itemId + " toItemId: " + toItemId);
		posIdCapas.remove(itemId);
		posIdCapas.add(posTo, itemId);
		Log.d("test", "cambiar_posicion_capa posIdCapas: " + posIdCapas);

		Capa toCapa = idToCapa.get(toItemId);
		Capa fromCapa = idToCapa.get(itemId);

		Log.d("test", "cambiar_posicion_capa posFrom: " + posFrom + " posTo: " + posTo);

		toCapa.setZ(posFrom);
		fromCapa.setZ(posTo);
	}

	/**
	 * Eliminar una capa
	 * @param itemId
	 * @return el id de la capa activa. Si se ha eliminado la capa activa devolverá la más al frente, si se ha eliminado la última devuelve -1
	 */
	public void eliminar_capa(long itemId){
		Capa capa = idToCapa.get(itemId);
		((ViewGroup) contenedor).removeView(capa);

		posIdCapas.remove(itemId);
		idToCapa.remove(itemId);

//		if (capaActiva_id == itemId) {
//			if (posIdCapas.size() > 0) {
//				long itemIdNewSelection = posIdCapas.get(posIdCapas.size()-1);
//				this.set_capa_activarTouch(itemIdNewSelection);
//				return itemIdNewSelection;
//			}
//			this.set_capa_activarTouch(-1);
//			return -1;
//		}
//		return itemId;
	}

    /**
     * Muestra u oculta la capa
     * @param itemId
     * @param mostrar true para mostrar la capa, false para ocultar
     */
    public void mostrarCapa(long itemId, boolean mostrar){
        Capa capa = idToCapa.get(itemId);
        if (mostrar) {
			capa.setVisibility(View.VISIBLE);
        } else {
            capa.setVisibility(View.INVISIBLE);
        }
    }

}
