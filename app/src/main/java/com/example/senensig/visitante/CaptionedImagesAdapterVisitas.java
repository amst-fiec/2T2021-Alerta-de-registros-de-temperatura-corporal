package com.example.senensig.visitante;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.senensig.R;
import com.example.senensig.objects.Visita;

import java.util.List;

public class CaptionedImagesAdapterVisitas extends RecyclerView.Adapter<CaptionedImagesAdapterVisitas.ViewHolder>
{




    // <==========|| we are telling the adapter what data it should work with. ||==========> [BEGIN]
    // We’ll use these variables (captions and imageIds) to hold the products data.
    private List<Visita> visitasArray;
    // <==========|| we are telling the adapter what data it should work with. ||==========> [END]






    // <==========|| Make the constructor. ||==========> [Begin]

    // We’ll pass the data to the adapter using its constructor.
    public CaptionedImagesAdapterVisitas(
            List<Visita> visitasArray
    )
    {
        this.visitasArray = visitasArray;
    }
    // <==========|| Make the constructor. ||==========> [END]






    // <==========|| Interfaces. ||==========> [BEGIN]
    public interface Listener
    {
        void onClick(Visita visitasArray);
    }
    // <==========|| Interface. ||==========> [END]






    // <==========|| Add the listeners as a private variable. ||==========> [BEGIN]
    public Listener listener;
    // <==========|| Add the listener as a private variable. ||==========> [END]





    // <==================|| Setting Listeners setters ||==================> [BEGIN]
    public void setListener(Listener listener)
    {
        this.listener = listener;
    }
    // <==================|| Setting Listeners ||==================> [END]






    // <==================|| Define the adapter’s view holder ||==================> [BEGIN}
    // Our recycler view needs to display CardViews, so we specify that our ViewHolder contains
    // CardViews. If you want to display another type of data (views) in the recycler view, you
    // should define it here.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView=v;
        }
    }
    // <==================|| Define the adapter’s view holder ||==================> [END]





    //
    // FILOSOFAR SOBRE TU MAL RENDIMIENTO EN LOS ESTUDIOS
    //





    // <==================|| implement the getItemCount() method ||==================> [BEGIN]

    // The length of the captions array equals the number of data items in the recycler view so
    // that number is returned by the getItemCount override method.
    @Override
    public int getItemCount() {
        return visitasArray.size();
    }
    // <==================|| implement the getItemCount() method ||==================> [END]






    // <==================|| Override the onCreateViewHolder() method ||==================> [BEGIN]
    // "CaptionedImagesAdapter.ViewHolder" was changed by just "ViewHolder"
    // The method "onCreateViewHolder" gets called when the recycler view needs to create a view
    // holder. (one for each card)
    @Override
    public CaptionedImagesAdapterVisitas.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        // "card_captioned_image" is the LayoutInflator to turn the layout into a CardView.
        // This is nearly identical to code you've already seen in the onCreateView() of fragments.
        CardView cv =
                (CardView) LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.card_captioned_image_adapter_visitas, parent, false
                );
        return new ViewHolder(cv);
    }
    // <==================|| Override the onCreateViewHolder() method ||==================> [END]





    /*
    <==================|| Add the data to the card views ||==================> [END]
    * The recycler view calls this method (onBindViewHolder) when it wants to use (or reuse) a
      view holder for a new piece of data.
    * This method populate the CardView’s ImageView and TextView with data.
    * The parameter "holder" it's an object of type "ViewHolder", an inner class previously created,
      the inner class ViewHolder method must hold the views that the cardView contain, those are:
        - the cardView per se.
        - a delete product button.
      So the holder is used to refer to those tow previously mentioned views.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        // I create a object "Producto" that binds with the attributes showed in the cardView of the
        // recyclerView"
        final Visita visita = visitasArray.get(position);

        // The cardView hold the views that shows the information of each product in the collection
        // "Productos"
        CardView cardView = holder.cardView;


        // Create a drawable with each images element (Image ID) in the imageIds array.

        // CardView UI, text views
        cardViewSetUIViews(cardView, position);

        // Recycler view that respond to clicks, for this a listener was added.
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (listener!= null){
                    listener.onClick(visita);
                }
            }
        });
    }
    // <==================|| Add the data to the card views ||==================> [END]






    // <==================|| CardView UI, text views  ||==================> [BEGIN]
    public void cardViewSetUIViews(CardView cardView, int position)
    {
        String mallNameStr, visitDateStr, visitTimeStr, visitTempStr;
        double visitTempFloat;

        Visita visita = visitasArray.get(position);

        mallNameStr = visita.getLugar();
        visitDateStr = getFecha(visita.getFecha())[0];
        visitTimeStr =  getFecha(visita.getFecha())[1];;
        visitTempFloat = visita.getTemperaturaC();
        visitTempStr = visitTempFloat + " °C";

        // ill show the product name according with the number of characters allowed, if the product
        //  name uses more characters than the allowed then ill add some ellipsis to the product
        //  name.
        // create the TextView para el nombre del producto
        TextView textView_nameMall = (TextView)cardView.findViewById(R.id.textView_nameMall);
        TextView textView_visitDate = (TextView)cardView.findViewById(R.id.textView_visitDate);
        TextView textView_visitTime = (TextView)cardView.findViewById(R.id.textView_visitTime);
        TextView textView_visitTemperature = (TextView)cardView.findViewById(R.id.textView_visitTemperature);
        // instance an ImageView and bind it with the one in the xml file
        ImageView imageViewHealthCondition = (ImageView)cardView.findViewById(R.id.my_info_image);

        textView_nameMall.setText(mallNameStr);
        textView_visitDate.setText(visitDateStr);
        textView_visitTime.setText(visitTimeStr);
        textView_visitTemperature.setText(visitTempStr);
        if (visitTempFloat < 38) {
            textView_visitTemperature.setTextColor(Color.parseColor("#67BA6B"));
            imageViewHealthCondition.setImageResource(R.drawable.thumb_ups);
        }
        else{
            imageViewHealthCondition.setImageResource(R.drawable.alert__copy_);
            textView_visitTemperature.setTextColor(Color.parseColor("#BE1E1E"));
        }
    }
    // <==================|| CardView UI format  ||==================> [END]

    public String[] getFecha (String strFecha){
        String[] arrayFecha =  strFecha.split(":");
        String mes;
        if (arrayFecha.length == 6){
            switch (Integer.parseInt(arrayFecha[1])){
                case 1:
                    mes = "Enero";
                    break;
                case 2:
                    mes = "Febrero";
                    break;
                case 3:
                    mes = "Marzo";
                    break;
                case 4:
                    mes = "Abril";
                    break;
                case 5:
                    mes = "Mayo";
                    break;
                case 6:
                    mes = "Junio";
                    break;
                case 7:
                    mes = "Julio";
                    break;
                case 8:
                    mes = "Agosto";
                    break;
                case 9:
                    mes = "Septiembre";
                    break;
                case 10:
                    mes = "Octubre";
                    break;
                case 11:
                    mes = "Noviembre";
                    break;
                case 12:
                    mes = "Diciembre";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + Integer.parseInt(arrayFecha[1]));
            }
            return new String[]{arrayFecha[2] + " " +mes + " , " + arrayFecha[0], arrayFecha[3] + ":" + arrayFecha[4] + ":" + arrayFecha[5]};
        }
        return new String[]{"Sin datos", "Sin datos"};
    }

}