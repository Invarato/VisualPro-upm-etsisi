package es.upm.etsisi.visualpro_upm_etsisi.vista;


import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.etsisi.visualpro_upm_etsisi.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdaptadorMinicapa extends RecyclerView.Adapter<AdaptadorMinicapa.MyViewHolder>
        implements ItemTouchHelperAdapter {
    private Set<Long> itemIdSeleccionadas = new LinkedHashSet<>();
    private List<Long> dataPositionsToItemId = new ArrayList<>();
    private Map<Long, OneData> mDataset = new LinkedHashMap<>();
//    private SparseArray<OneData> mDataset = new SparseArray<>();
//    private SparseLongArray<OneData> mDataset = new SparseArray<>();

    private long lastItemIdAssigned = -1;


//    private Set<Long> stateSaveditemIdSeleccionadas = new LinkedHashSet<>();
//    private List<Long> stateSaveddataPositionsToItemId = new ArrayList<>();
//    private Map<Long, OneData> stateSavedmDataset = new LinkedHashMap<>();


    public static class OneData{
//        private int itemId;
        private Bitmap miniimagen;
        private Boolean isSelected;

        private Boolean isVisible;

        public OneData(Bitmap miniimagen, Boolean isSelected, Boolean isVisible){
            this.miniimagen = miniimagen;
            this.isSelected = isSelected;
            this.isVisible = isVisible;
        }

        public Bitmap getMiniimagen() {
            return miniimagen;
        }

        public void setMiniimagen(Bitmap miniimagen) {
            this.miniimagen = miniimagen;
        }

        public Boolean getSelected() {
            return isSelected;
        }

        public void setSelected(Boolean selected) {
            isSelected = selected;
        }

        public Boolean getVisible() {
            return isVisible;
        }

        public void setVisible(Boolean visible) {
            isVisible = visible;
        }

    }


    private static ClickListener clickListener;
    public interface ClickListener {
        void onItemClick(int position, View v);
        boolean onItemLongClick(int position, View v);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageButton;
        private final ConstraintLayout minicapaForegroundImagebutton;
        private final ConstraintLayout minicapaBckgroundBorrar;
        private final ConstraintLayout minicapaBckgroundVisibilidad;
        private final ConstraintLayout minicapaCompleta;

        public MyViewHolder(View v) {
            super(v);
            minicapaCompleta = v.findViewById(R.id.minicapa_completa);
            imageButton = v.findViewById(R.id.imageButton_minicapa);
            minicapaForegroundImagebutton = v.findViewById(R.id.minicapa_foreground_imagebutton);
            minicapaBckgroundBorrar = v.findViewById(R.id.minicapa_background_borrar);
            minicapaBckgroundVisibilidad = v.findViewById(R.id.minicapa_background_visibilidad);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition(), v);
                }
            });
            imageButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return clickListener.onItemLongClick(getAdapterPosition(), v);
                }
            });
        }

        public ImageView getImageView() {
            return imageButton;
        }

        public ConstraintLayout getMinicapaBckgroundBorrar() {
            return minicapaBckgroundBorrar;
        }

        public ConstraintLayout getMinicapaCompleta() {
            return minicapaCompleta;
        }

        public ConstraintLayout getMinicapaBckgroundVisibilidad() {
            return minicapaBckgroundVisibilidad;
        }

        public ConstraintLayout getMinicapaForegroundImagebutton() {
            return minicapaForegroundImagebutton;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public AdaptadorMinicapa(Map<Long, OneData> myDataset) {
        if (myDataset != null) {
            this.mDataset = myDataset;
        }

    }

    @Override
    public AdaptadorMinicapa.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        Log.d("test", "onCreateViewHolder " );
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_minicapa, parent, false);

        // Ajustar altura a la view
        int width = parent.getMeasuredWidth();
        ViewGroup.LayoutParams llpmbb = itemView.getLayoutParams();
        llpmbb.height = width;
        itemView.setLayoutParams(llpmbb);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("test", "onBindViewHolder: " + position);
        long itemId = this.getItemId(position);
        holder.getImageView().setImageBitmap(mDataset.get(itemId).getMiniimagen());
        holder.getImageView().setSelected(mDataset.get(itemId).getSelected());

        holder.getImageView().requestFocus();
    }

    @Override
    public int getItemCount() {
        Log.d("test", "getItemCount: " + mDataset.size());
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return this.dataPositionsToItemId.get(position);
    }

    public int getPosition(long itemId) {
        return this.dataPositionsToItemId.indexOf(itemId);
    }

    public void moveItemToPosition(long itemId, int newposition) {
        int oldposition = this.getPosition(itemId);
        this.dataPositionsToItemId.remove(itemId);
        this.dataPositionsToItemId.add(newposition, itemId);
        this.notifyItemMoved(oldposition, newposition);
    }


    public OneData getItem(long itemId){
    return mDataset.get(itemId);
}

    public long addItem(long itemId, OneData onedata, int position) {
        Log.d("test", "addItem itemId: " + itemId);
        mDataset.put(itemId, onedata);
        if (onedata.getSelected()) {
            this.itemIdSeleccionadas.add(itemId);
        }
        this.dataPositionsToItemId.add(position, itemId);
        this.notifyItemInserted(position);
        return itemId;
    }

    public long addItem(long itemId, OneData onedata) {
        return this.addItem(itemId, onedata, this.getItemCount() + 1);
    }

    public long addItem(OneData onedata, int position) {
        lastItemIdAssigned += 1;
        return this.addItem(lastItemIdAssigned, onedata, position);
    }

    public long addItem(OneData onedata) {
        lastItemIdAssigned += 1;
        return this.addItem(lastItemIdAssigned, onedata);
    }

    public void removeItem(long itemId) {
        int pos = this.dataPositionsToItemId.indexOf(itemId);
        this.dataPositionsToItemId.remove(pos);

        this.itemIdSeleccionadas.remove(itemId);
        this.mDataset.remove(itemId);
        this.notifyItemRemoved(pos);
    }


    public void updateItem(long itemId, OneData onedata) {
        mDataset.put(itemId, onedata);
        int pos = this.dataPositionsToItemId.indexOf(itemId);
        this.notifyItemChanged(pos);

        if (onedata.getSelected()) {
            this.itemIdSeleccionadas.add(itemId);
        } else {
            this.itemIdSeleccionadas.remove(itemId);
        }
    }

    public void setSelectedItem(long itemId, boolean isSelected){
        this.getItem(itemId).setSelected(isSelected);
        int pos = getPosition(itemId);
        this.notifyItemChanged(pos);

        if (isSelected) {
            this.itemIdSeleccionadas.add(itemId);
        } else {
            this.itemIdSeleccionadas.remove(itemId);
        }
    }

    public void unselectAllItems(){
        Log.d("test", "unselectAllItems: " + this.itemIdSeleccionadas);

//        List<Long> copyItemIdSeleccionadas = new ArrayList<>(this.itemIdSeleccionadas);
//        for (long itemIdSeleccionada: copyItemIdSeleccionadas) {
//            this.setSelectedItem(itemIdSeleccionada, false);
//        }

        for (long itemIdSeleccionada : this.itemIdSeleccionadas ) {
            this.getItem(itemIdSeleccionada).setSelected(false);
            int pos = getPosition(itemIdSeleccionada);
            this.notifyItemChanged(pos);
        }

        this.itemIdSeleccionadas = new LinkedHashSet<>();

    }

    /**
     * selecciona el elemento de la posición cero si existe
     * @return id del elemento seleccionado, -1 sino no había nada que seleccionar
     */
    public long selectFirstItemIfExists(){
        if (this.mDataset.size() > 0) {
            long itemIdFirstPos = this.getItemId(0);
            this.setSelectedItem(itemIdFirstPos, true);
            return itemIdFirstPos;
        }
        return -1;
    }

    public void selectAllItems(){
        for (long itemId = 0; itemId < this.getItemCount(); itemId++) {
            this.setSelectedItem(itemId, true);
        }
    }

//    TODO de momento no sirve para nada, lo suyo sería refrescarlo para que el recicler no se vuelva loco
    public void changeVisibility(long itemId, boolean isVisible){
        mDataset.get(itemId).setVisible(isVisible);
    }

//    ==========================================

    @Override
    public Boolean onMoveItem(int fromPosition, int toPosition) {
        Log.d("test", "onItemMove fromPosition:" + fromPosition + " toPosition: " + toPosition );
        long itemId = this.getItemId(fromPosition);
        this.moveItemToPosition(itemId, toPosition);
        return true;
    }

    @Override
    public void onDismissItem(int position) {
        Log.d("test", "onItemDismiss position:" + position );
        long itemId = this.getItemId(position);
        this.removeItem(itemId);
    }


}