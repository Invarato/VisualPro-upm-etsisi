package es.upm.etsisi.visualpro_upm_etsisi.vista;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import es.upm.etsisi.visualpro_upm_etsisi.Modelo.ventana_about;
import es.upm.etsisi.visualpro_upm_etsisi.Modelo.ventana_guardar;
import es.upm.etsisi.visualpro_upm_etsisi.Modelo.ventana_un_valor;
import es.upm.etsisi.visualpro_upm_etsisi.Modelo.ventana_unico;
import es.upm.etsisi.visualpro_upm_etsisi.R;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.Capa;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.CapaFiltros;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.Gestor_de_capas;
import es.upm.etsisi.visualpro_upm_etsisi.controlador.Tocar.ACCION_SCROLL;

import java.util.ArrayList;
import java.util.List;


/** Interfaz de la clase PFG_Aplicación
 * @author Ramón Invarato Menéndez
 * @author Roberto Séez Ruiz
 */
public class interfaz extends AppCompatActivity {

	private String TAG = "test";

	private static final int IMAGEN_CARGADA = 1;

	public enum TIPO_DE_MENU {
		NO_CAPA,
		CAPA,
		CAPAS,
		RECORTE;
	}

	public TIPO_DE_MENU tipoDeMenu = TIPO_DE_MENU.NO_CAPA;
	public TIPO_DE_MENU tipoDeMenuPrevio = TIPO_DE_MENU.NO_CAPA;

	private ImageButton buttonAddNewCapa;
	private ImageButton buttonAcceptAndAddNewCapa;

	private RecyclerView rvIconosCapas;
	private AdaptadorMinicapa mAdapter;
	private RecyclerView.LayoutManager layoutManager;

	private Gestor_de_capas marcoTocar = null;
	
	private FrameLayout marco;

	private ACCION_SCROLL accionDedoActual = ACCION_SCROLL.MOVERSE;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//		AppBarLayout myToolbar = findViewById(R.id.toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		final Context cntxInterfaz = this;

		buttonAddNewCapa = findViewById(R.id.button_add_new_capa);
		buttonAddNewCapa.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				cargar_nueva_capa();
			}
		});

		buttonAcceptAndAddNewCapa = findViewById(R.id.button_accept_and_add_new_capa);

		marco = findViewById(R.id.frameLayout_ContenedorImagenes);
		marcoTocar = new Gestor_de_capas(this, marco);

		rvIconosCapas = findViewById(R.id.recyclerview_capas);
		layoutManager = new LinearLayoutManager(this);

		rvIconosCapas.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

		rvIconosCapas.setLayoutManager(layoutManager);

		mAdapter = new AdaptadorMinicapa(null);
		rvIconosCapas.setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(new AdaptadorMinicapa.ClickListener() {
			@Override
			public void onItemClick(int position, View v) {
				Log.d(TAG, "onItemClick position: " + position);
				long itemId = mAdapter.getItemId(position);
				Log.d(TAG, "onItemClick v.getId(): " + itemId);
				seleccionar_solouna_capa_y_minicapa(itemId);
			}

			@Override
			public boolean onItemLongClick(int position, View v) {
				Log.d(TAG, "onItemLongClick position: " + position);
				return false;
			}
		});


//		https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.Callback.html
//		https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.SimpleCallback.html
		int dragDirs = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		int swipeDirs = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
		ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView,
								  @NonNull RecyclerView.ViewHolder viewHolder,
								  @NonNull RecyclerView.ViewHolder viewHolderTarget) {
				Log.d(TAG, "onMove" );
				final int fromPos = viewHolder.getAdapterPosition();
				final int toPos = viewHolderTarget.getAdapterPosition();

				long itemId = mAdapter.getItemId(fromPos);
				long toItemId = mAdapter.getItemId(toPos);
				marcoTocar.cambiar_posicion_capa(itemId, toItemId);

				return mAdapter.onMoveItem(fromPos, toPos); // true if moved, false otherwise
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				Log.v(TAG, "onSwiped direction: "+direction);
				final int pos = viewHolder.getAdapterPosition();
				final long itemId = mAdapter.getItemId(pos);

				switch (direction){
					case ItemTouchHelper.LEFT:{
						Log.v(TAG, "onSwiped LEFT pos: " + pos + " itemId: " + itemId);

						if (marcoTocar.is_idxCapaActiva(itemId)){
							seleccionar_solouna_capa_y_minicapa(itemId);
						} else {
							marcoTocar.mostrarCapa(itemId, false);
							mAdapter.changeVisibility(itemId, false);
						}
						break;}
					case ItemTouchHelper.RIGHT:{
						Log.v(TAG, "onSwiped RIGHT");

                        final AdaptadorMinicapa.OneData lastDeletedItem = mAdapter.getItem(itemId);
                        mAdapter.onDismissItem(pos);
						if (lastDeletedItem.getSelected()) {
							long newItemIdToSel = mAdapter.selectFirstItemIfExists();
//							marcoTocar.set_capa_activarTouch(newItemIdToSel);
							((interfaz) cntxInterfaz).seleccionar_capa_y_minicapa(newItemIdToSel);
							lastDeletedItem.setSelected(false);
						}

						marcoTocar.mostrarCapa(itemId, false);

						Snackbar.make(marco, "Capa eliminada", Snackbar.LENGTH_LONG)
								.setAction("DESHACER", new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										marcoTocar.mostrarCapa(itemId, true);
										mAdapter.addItem(itemId, lastDeletedItem, pos);
									}
								})
								.setActionTextColor(Color.YELLOW)
								.addCallback(new Snackbar.Callback(){
									@Override
									public void onDismissed(Snackbar snackbar, int event) {
										Log.v(TAG, "Snackbar onDismissed: " + event);
										switch (event){
											case Snackbar.Callback.DISMISS_EVENT_ACTION:{
												// Clicado en la acción de deshacer
												break;}
											default: {
												marcoTocar.eliminar_capa(itemId);
												break;}
										}

									}

									@Override
									public void onShown(Snackbar snackbar) {
										Log.v(TAG, "Snackbar onShown");
									}
								})
								.show();
						break;}

				}
			}

			@Override
			public boolean isLongPressDragEnabled() {
				return true;
			}

			@Override
			public boolean isItemViewSwipeEnabled() {
				return true;
			}

			/**
			 * Define que se mueve, por ejemplo la imagen que está encima o debajo del elemto de la lista
			 * @param viewHolder
			 * @param actionState
			 */
			@Override
			public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
										  int actionState) {
				Log.d(TAG, "onSelectedChanged" );
				if (viewHolder != null) {
					final AdaptadorMinicapa.MyViewHolder myViewHolder = (AdaptadorMinicapa.MyViewHolder) viewHolder;
					switch (actionState){
						case ItemTouchHelper.ACTION_STATE_SWIPE:{
							final View foregroundView = myViewHolder.getMinicapaForegroundImagebutton();
							getDefaultUIUtil().onSelected(foregroundView);
							break;}
						case ItemTouchHelper.ACTION_STATE_DRAG:{
							final View viewCompleta = myViewHolder.getMinicapaCompleta();
							getDefaultUIUtil().onSelected(viewCompleta);
							break;}
					}
				}
			}

			private int makeFlagDir(float dX, float dY){
				if (dY < 0) {
					return ItemTouchHelper.UP;
				} else if (dY > 0) {
					return ItemTouchHelper.DOWN;
				} else if (dX < 0) {
					return ItemTouchHelper.LEFT;
				} else if (dX > 0) {
					return ItemTouchHelper.RIGHT;
				} else {
					return ItemTouchHelper.ACTION_STATE_IDLE;
				}
			}

//			https://medium.com/@Dalvin/android-recycler-view-item-swipe-with-multiple-context-options-4546f7e12fdc

			@Override
			public void onChildDrawOver(Canvas c,
                                        @NonNull RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder,
                                        float dX,
                                        float dY,
                                        int actionState,
                                        boolean isCurrentlyActive){

				Log.v(TAG, "onChildDrawOver dX: " + dX + " dY: " + dY + " actionState: " + actionState);

				final AdaptadorMinicapa.MyViewHolder myViewHolder = (AdaptadorMinicapa.MyViewHolder) viewHolder;

				switch (actionState){
					case ItemTouchHelper.ACTION_STATE_SWIPE:{
						final View foregroundView = myViewHolder.getMinicapaForegroundImagebutton();
						if (dX < 0) {
							// Mover a la izquierda
							getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX/2, dY,
									actionState, isCurrentlyActive);
						} else if (dX > 0) {
							// Mover a la derecha
							getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
									actionState, isCurrentlyActive);
						} else {
							int pos = viewHolder.getAdapterPosition();
							long itemId = mAdapter.getItemId(pos);

							Log.v(TAG, "LLEGAAAAA2 pos: " + pos + " itemId: " + itemId);
							marcoTocar.mostrarCapa(itemId, true);
							mAdapter.changeVisibility(itemId, true);
						}
						break;}
					case ItemTouchHelper.ACTION_STATE_DRAG:{
						final View viewCompleta = myViewHolder.getMinicapaCompleta();
						getDefaultUIUtil().onDrawOver(c, recyclerView,
								viewCompleta, dX, dY,
								actionState, isCurrentlyActive);
						break;}
				}
			}

			@Override
			public void onChildDraw(Canvas c,
                                    RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX,
                                    float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
				Log.v(TAG, "onChildDraw dX: " + dX + " dY: " + dY + " actionState: " + actionState);

				final AdaptadorMinicapa.MyViewHolder myViewHolder = (AdaptadorMinicapa.MyViewHolder) viewHolder;

				switch (actionState){
					case ItemTouchHelper.ACTION_STATE_SWIPE:{
						final View foregroundView = myViewHolder.getMinicapaForegroundImagebutton();
						final View backBorrar = myViewHolder.getMinicapaBckgroundBorrar();
						final View backVer = myViewHolder.getMinicapaBckgroundVisibilidad();

						if (dX < 0) {
							// Mover a la izquierda (visibilidad)
							getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX/2, dY,
									actionState, isCurrentlyActive);

							backVer.setVisibility(View.VISIBLE);
							backBorrar.setVisibility(View.INVISIBLE);
						} else if (dX > 0) {
							// Mover a la derecha (eliminar)
							getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
									actionState, isCurrentlyActive);

							backBorrar.setVisibility(View.VISIBLE);
							backVer.setVisibility(View.INVISIBLE);
						}
						break;}
					case ItemTouchHelper.ACTION_STATE_DRAG:{
						final View viewCompleta = myViewHolder.getMinicapaCompleta();
						getDefaultUIUtil().onDraw(c, recyclerView,
								viewCompleta, dX, dY,
								actionState, isCurrentlyActive);
						break;}
				}
			}

			@Override
			public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				Log.d(TAG, "clearView" );
				final AdaptadorMinicapa.MyViewHolder myViewHolder = (AdaptadorMinicapa.MyViewHolder) viewHolder;
				final View foregroundView = myViewHolder.getMinicapaForegroundImagebutton();
				getDefaultUIUtil().clearView(foregroundView);

				final View backBorrar = myViewHolder.getMinicapaBckgroundBorrar();
				final View backVer = myViewHolder.getMinicapaBckgroundVisibilidad();
				backVer.setVisibility(View.INVISIBLE);
				backBorrar.setVisibility(View.INVISIBLE);
			}

		});
		touchHelper.attachToRecyclerView(rvIconosCapas);
    }

//	https://www.intertech.com/Blog/saving-and-retrieving-android-instance-state-part-1/
//	https://trickyandroid.com/saving-android-view-state-correctly/
//	https://www.netguru.com/codestories/how-to-correctly-save-the-state-of-a-custom-view-in-android
//	https://blog.nextzy.me/the-right-way-to-save-state-viewgroup-c6cb6a5c5b91
	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);

//		state.putSerializable("tipoDeMenu", tipoDeMenu);
//		state.putSerializable("tipoDeMenuPrevio", tipoDeMenuPrevio);
//		state.putSerializable("buttonAddNewCapa", buttonAddNewCapa);
//		state.putSerializable("buttonAcceptAndAddNewCapa", buttonAcceptAndAddNewCapa);
//		state.putSerializable("rvIconosCapas", rvIconosCapas);
//		state.putSerializable("mAdapter", mAdapter);
//		state.putSerializable("layoutManager", layoutManager);
//		state.putSerializable("marcoTocar", marcoTocar);
//		state.putSerializable("marco", marco);
//		state.putSerializable("accionDedoActual", accionDedoActual);

	}
//TODO //
//TODO //	@Override
//TODO //	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//TODO //		super.onRestoreInstanceState(savedInstanceState);
//TODO //		Log.v(TAG, "Inside of onRestoreInstanceState");
//TODO //		startTime = (Calendar) savedInstanceState.getSerializable("starttime");
//TODO //	}



    
    /**Crea una nueva capa y la devuelve
     * @param capa si es null se crea una nueva capa. Si no, se inserta la pasada
     */
    private Capa nueva_capa_iniciar (Capa capa){
		long idCapa = marcoTocar.set_capa_aniadir(capa);
		changeMenuOptions(TIPO_DE_MENU.CAPA);
    	return marcoTocar.getCapa(idCapa);
    }

    /**Crea la miniatura de la capa. Ejecutar despueés de nueva_capa_iniciar
     * 
     */
    private long crear_miniatura_capa (Capa capa_original){
		Capa miniImagenCapa = new Capa (this);
		miniImagenCapa.cargar_imagen(capa_original.obtener_bitmapResultante());
		miniImagenCapa.ajustar_imagen_a(rvIconosCapas.getMeasuredWidth(), rvIconosCapas.getMeasuredWidth());
		AdaptadorMinicapa.OneData onedata = new AdaptadorMinicapa.OneData(miniImagenCapa.obtener_bitmapResultante(), true, true);
		return mAdapter.addItem(onedata, 0);
	}


	private void crear_miniatura_y_seleccionar (Capa capa_original){
//		mAdapter.unselectAllItems();
		long itemId = this.crear_miniatura_capa(capa_original);
		this.seleccionar_solouna_capa_y_minicapa(itemId);
//		marcoTocar.set_capa_activarTouch(itemId);
	}

//	private void seleccionar_una_unica_capa_y_minicapa(long itemId){
////		mAdapter.unselectAllItems();
//		if (marcoTocar.idxCapaActiva_cantidad() > -1) {
//			mAdapter.setSelectedItem(marcoTocar.getCapa_activa_id(), false);
//			mAdapter.setSelectedItem(itemId, true);
//			marcoTocar.set_capa_activarTouch(itemId);
//		}
//	}

	private void deseleccionar_capa_y_minicapa(long itemId){
		if (marcoTocar.is_idxCapaActiva(itemId)) {
			mAdapter.setSelectedItem(itemId, false);
			marcoTocar.quitar_idxCapaActiva(itemId);
		}
	}

	private void deseleccionar_todas_capas_y_minicapas(){
//		for (long itemIdCapaActiva: marcoTocar.get_idxCapasActivas()) {
		mAdapter.unselectAllItems();
//		List<Long> aux = marcoTocar.get_idxCapasActivas();
		marcoTocar.quitar_todas_idxCapaActiva();
//		}
	}

	private void seleccionar_capa_y_minicapa(long itemId){
		mAdapter.setSelectedItem(itemId, true);
		marcoTocar.establecer_idxCapaActiva(itemId);
//		marcoTocar.set_capa_activarTouch(itemId);
	}

	private void seleccionar_solouna_capa_y_minicapa(long itemId){
		this.deseleccionar_todas_capas_y_minicapas();
		this.seleccionar_capa_y_minicapa(itemId);
	}

//	private void seleccionar_capa_y_minicapa(long itemId){
////		mAdapter.unselectAllItems();
//		if (marcoTocar.idxCapaActiva_cantidad() > -1) {
//			mAdapter.setSelectedItem(marcoTocar.getCapa_activa_id(), false);
//			mAdapter.setSelectedItem(itemId, true);
//			marcoTocar.set_capa_activarTouch(itemId);
//		}
//	}

//	private void seleccionar_una_unica_capa_y_minicapa(long itemId){
////		mAdapter.unselectAllItems();
//		if (marcoTocar.idxCapaActiva_cantidad() > -1) {
//			mAdapter.setSelectedItem(marcoTocar.getCapa_activa_id(), false);
//			mAdapter.setSelectedItem(itemId, true);
//			marcoTocar.set_capa_activarTouch(itemId);
//		}
//	}


//	private void deseleccionar_capa_y_minicapa_activa(){
////		mAdapter.unselectAllItems();
//		if (marcoTocar.getCapa_activa_id() > -1) {
//			mAdapter.setSelectedItem(marcoTocar.getCapa_activa_id(), false);
//			marcoTocar.set_capa_activarTouch(-1);
//		}
//	}

	/**
	 * Cuando se seleccione una imagen para cargar entrará aquí
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == IMAGEN_CARGADA) {
				Capa ultima_capa = nueva_capa_iniciar(null);

				Uri uriSeleccionado = data.getData();
				ultima_capa.cargar_imagen(uriSeleccionado);
				ultima_capa.ajustar_imagen_a(marco.getWidth(), marco.getHeight());

				crear_miniatura_y_seleccionar(ultima_capa);
			}
		}
	}

	private List<Long> ultimas_capas_activas = null;

	public void activarModoMapa(){
		this.changeMenuOptions(TIPO_DE_MENU.CAPAS);
		ultimas_capas_activas = marcoTocar.get_idxCapasActivas();
		Log.v("test", "111permutarSeleccionarUnaCapaOTodas ultimas_capas_activas:"+ultimas_capas_activas);
		marcoTocar.set_idxCapasActivas(marcoTocar.get_todas_idxCapas());
	}

	public void desactivarModoMapa(){
		changeMenuOptionsPreviously();
		Log.v("test", "2222permutarSeleccionarUnaCapaOTodas ultimas_capas_activas:"+ultimas_capas_activas);
		if (ultimas_capas_activas != null) {
			marcoTocar.set_idxCapasActivas(ultimas_capas_activas);
		}
		ultimas_capas_activas = null;
	}

	//	Cortar ===============
	public void activarCortarImagen(){
		accionDedoActual=ACCION_SCROLL.SELECCIONARSE;

		marcoTocar.set_accion_scroll(accionDedoActual);

		this.changeMenuOptions(TIPO_DE_MENU.RECORTE);

		buttonAcceptAndAddNewCapa.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				aceptarCorte();
			}
		});
		buttonAddNewCapa.setVisibility(View.GONE);
		buttonAcceptAndAddNewCapa.setVisibility(View.VISIBLE);
	}

	private void aceptarCorte(){
		int [] array_sel = marcoTocar.get_seleccion();
		cancelarCorte();

		Log.v("test", "aceptarCorte:" + marcoTocar.get_idxCapasActivas());

		List<Long> copyIdxCapasActivas = new ArrayList<>(marcoTocar.get_idxCapasActivas());

		for (long idxCapasActiva: copyIdxCapasActivas) {
			final Capa capaActiva = marcoTocar.getCapa(idxCapasActiva);

			Capa capaCorte = capaActiva.convertir_a_subimagen (array_sel[0], array_sel[1], array_sel[2], array_sel[3]);

			if (capaCorte!=null){//Si no se ha seleccionado nada, no se recorta nada
				nueva_capa_iniciar(capaCorte);
				crear_miniatura_y_seleccionar(capaCorte);
			}
		}

	}

	private void cancelarCorte(){
		accionDedoActual=ACCION_SCROLL.MOVERSE;
		marcoTocar.set_accion_scroll(accionDedoActual);
		changeMenuOptionsPreviously();
	}

	//	Cortar ===============

	private void cargar_nueva_capa(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);	//Permite al usuarios seleccionar un tipo de datos y devolverlo
		intent.setType("image/*"); //Tipos de archivos
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {	//Prueba que exista un selector de ficheros
			this.startActivityForResult(Intent.createChooser(intent, this.getText(R.string.Selecciona_una_imagen)), IMAGEN_CARGADA);
		} catch (android.content.ActivityNotFoundException ex) {	//En caso de que no exista ningún selector de ficheros
			Toast.makeText(this, this.getText(R.string.No_existe_admin), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void guardar_proyecto(){
		new ventana_guardar(this) {
			@Override
			public void ventana_aceptada(String nombre_archivo) {
				String ruta = marcoTocar.guardar_todo_en_una_imagen(nombre_archivo);
				if (ruta!=null)
					Toast.makeText(this.getContext(), ruta, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this.getContext(), this.getContext().getString(R.string.A_ocurrido_un_error), Toast.LENGTH_LONG).show();
			}
		};
	}
	
	
	//--------------------------MENU----------------------------------------------------------------------------------------------

	/**
	 * Cambia el menú
	 *
	 * @param tipoDeMenu
	 */
	public void changeMenuOptions(TIPO_DE_MENU tipoDeMenu) {
		Log.v("test", "changeMenuOptions tipoDeMenu:" + this.tipoDeMenu);
		this.tipoDeMenuPrevio = this.tipoDeMenu;
		this.tipoDeMenu = tipoDeMenu;
		invalidateOptionsMenu();
	}

	/**
	 * Vuelve al menú previo al cambio
	 */
	public void changeMenuOptionsPreviously() {
		Log.v("test", "changeMenuOptionsPreviously tipoDeMenuPrevio:" + this.tipoDeMenuPrevio);
		this.changeMenuOptions(this.tipoDeMenuPrevio);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		switch (this.tipoDeMenu){
			case NO_CAPA:{
				inflater.inflate(R.menu.menu_toolbar, menu);
				menu.findItem(R.id.menu_guardar).setVisible(false);
				menu.findItem(R.id.menu_filtros).setVisible(false);
				menu.findItem(R.id.menu_modomapa).setVisible(false);
				menu.findItem(R.id.menu_cortar).setVisible(false);
				break;}
			case CAPA:{
				inflater.inflate(R.menu.menu_toolbar, menu);
				buttonAddNewCapa.setVisibility(View.VISIBLE);
				buttonAcceptAndAddNewCapa.setVisibility(View.GONE);
				buttonAddNewCapa.setEnabled(true);
				break;}

			case CAPAS:{
				inflater.inflate(R.menu.menu_modomapa, menu);
				buttonAddNewCapa.setEnabled(false);
				break;}

			case RECORTE:{
				inflater.inflate(R.menu.menu_recortar, menu);
				buttonAcceptAndAddNewCapa.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						aceptarCorte();
					}
				});
				buttonAddNewCapa.setVisibility(View.GONE);
				buttonAcceptAndAddNewCapa.setVisibility(View.VISIBLE);
				break;}
		}

		return true;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

//		for (long idxCapasActiva: marcoTocar.get_idxCapasActivas()) {
//			final Capa capaActiva = marcoTocar.getCapa(idxCapasActiva);
//			final Capa capaActiva = marcoTocar.get_idxCapasActivas().get(0);

//		if (marcoTocar.getCapas_cantidad()>0) {
		//		TODO se obtiene solo una para los filtros, puede que poner un for en cada uno
		final Capa capaActiva = marcoTocar.getCapa(marcoTocar.get_idxCapasActivas().get(0));
//		}

		switch (item.getItemId()) {

			case R.id.menu_guardar: {
				this.guardar_proyecto();
				break;
			}

			case R.id.menu_cortar: {
				this.activarCortarImagen();
				break;
			}

			case R.id.menu_cortar_cancelar: {
				this.cancelarCorte();
				break;
			}

			case R.id.menu_modomapa: {
				this.activarModoMapa();
//				permutarSeleccionarUnaCapaOTodas(item);
				break;
			}

			case R.id.menu_modomapa_cancelar: {
				this.desactivarModoMapa();
				break;
			}

			case R.id.menu_about: {
				new ventana_about(this);
				break;
			}

			//			case R.id.menu_cortar_aceptar:{
			//				this.aceptarCorte();
			//			break;}




			case R.id.SubMnuOpc_Sepia: {
				new ventana_unico(this, this.getString(R.string.Sepia), capaActiva, CapaFiltros.ORDEN.SEPIA) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.SEPIA);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Trans: {
				new ventana_un_valor(this, 0, 128, 255, this.getString(R.string.Transparencia), capaActiva, CapaFiltros.ORDEN.TRANS) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.TRANS, valor);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Intensidad: {
				new ventana_un_valor(this, -100, 0, 100, this.getString(R.string.Intensidad), capaActiva, CapaFiltros.ORDEN.INTENSIDAD) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.INTENSIDAD, valor);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Matiz: {
				new ventana_un_valor(this, 0, 180, 360, this.getString(R.string.Matiz), capaActiva, CapaFiltros.ORDEN.MATIZ) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.MATIZ, valor);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_ByN: {
				new ventana_un_valor(this, 0, 128, 255, this.getString(R.string.Blanco_y_Negro), capaActiva, CapaFiltros.ORDEN.BYN) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.BYN, valor);
					}
				};
				/*
				new ventana_argb(this, 123, 234, 15, 75) {
					@Override
					public void ventana_aceptada(int A, int R, int G, int B) {
						Log.v("test", "A:"+A+" R:"+R+" G:"+G+" B:"+B);
						// TODO

					}
				};
				*/
				break;
			}

			case R.id.SubMnuOpc_Satura: {
				new ventana_un_valor(this, -100, 0, 100, this.getString(R.string.Saturacion), capaActiva, CapaFiltros.ORDEN.SATURA) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.SATURA, valor);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Brillo: {
				new ventana_un_valor(this, -100, 0, 100, this.getString(R.string.Brillo), capaActiva, CapaFiltros.ORDEN.BRILLO) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.BRILLO, valor);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Solarizar: {
				new ventana_unico(this, this.getString(R.string.Solarizar), capaActiva, CapaFiltros.ORDEN.SOLAR) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.SOLAR);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Contraste: {
				new ventana_un_valor(this, 0, 45, 90, this.getString(R.string.Contraste), capaActiva, CapaFiltros.ORDEN.CONTRASTE) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.CONTRASTE, valor);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Post: {
				new ventana_un_valor(this, 0, 128, 255, this.getString(R.string.Post), capaActiva, CapaFiltros.ORDEN.POST) {
					@Override
					public void ventana_aceptada(int valor) {
						capaActiva.cambiar_color_variable(CapaFiltros.ORDEN.POST, valor);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Grises: {
				new ventana_unico(this, this.getString(R.string.Gris), capaActiva, CapaFiltros.ORDEN.GRIS) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.GRIS);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Invert: {
				new ventana_unico(this, this.getString(R.string.Invert), capaActiva, CapaFiltros.ORDEN.INVERT) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.INVERT);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_Azul: {
				new ventana_unico(this, this.getString(R.string.Azulado), capaActiva, CapaFiltros.ORDEN.AZUL) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.AZUL);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_PBajo: {
				new ventana_unico(this, this.getString(R.string.PBajo), capaActiva, CapaFiltros.ORDEN.PBAJO) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.PBAJO);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_Mediana: {
				new ventana_unico(this, this.getString(R.string.Mediana), capaActiva, CapaFiltros.ORDEN.MEDIANA) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.MEDIANA);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_Gradiente: {
				new ventana_unico(this, this.getString(R.string.Gradiente), capaActiva, CapaFiltros.ORDEN.GRADIENTE) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.GRADIENTE);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_Sobel: {
				new ventana_unico(this, this.getString(R.string.Sobel), capaActiva, CapaFiltros.ORDEN.SOBEL) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.SOBEL);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_Prewitt: {
				new ventana_unico(this, this.getString(R.string.Prewitt), capaActiva, CapaFiltros.ORDEN.PREWITT) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.PREWITT);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_Roberts: {
				new ventana_unico(this, this.getString(R.string.Roberts), capaActiva, CapaFiltros.ORDEN.ROBERTS) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.ROBERTS);
					}
				};
				break;
			}
			case R.id.SubMnuOpc_FreiChen: {
				new ventana_unico(this, this.getString(R.string.FreiChen), capaActiva, CapaFiltros.ORDEN.FREICHEN) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.FREICHEN);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_PAlto: {
				new ventana_unico(this, this.getString(R.string.PAlto), capaActiva, CapaFiltros.ORDEN.PALTO) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.PALTO);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_Laplace: {
				new ventana_unico(this, this.getString(R.string.Laplace), capaActiva, CapaFiltros.ORDEN.LAPLACE) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.LAPLACE);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_RLaplace: {
				new ventana_unico(this, this.getString(R.string.RLaplace), capaActiva, CapaFiltros.ORDEN.RLAPLACE) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.RLAPLACE);
					}
				};
				break;
			}

			case R.id.SubMnuOpc_SRoberts: {
				new ventana_unico(this, this.getString(R.string.SRoberts), capaActiva, CapaFiltros.ORDEN.SROBERTS) {
					@Override
					public void ventana_aceptada() {
						capaActiva.cambiar_color(CapaFiltros.ORDEN.SROBERTS);
					}
				};
				break;
			}

		}


		return false;
	}
	
	
}
