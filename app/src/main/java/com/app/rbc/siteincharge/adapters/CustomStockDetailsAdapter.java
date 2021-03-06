package com.app.rbc.siteincharge.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rbc.siteincharge.R;
import com.app.rbc.siteincharge.models.db.models.Categoryproduct;
import com.app.rbc.siteincharge.models.db.models.Site;
import com.app.rbc.siteincharge.models.db.models.site_overview.Stock;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CustomStockDetailsAdapter extends RecyclerView.Adapter<CustomStockDetailsAdapter.MyViewHolder> {

    private List<Stock> stocks;
    private Context context;
    public CustomStockDetailsAdapter(Context context,List<Stock> stocks) {
        this.stocks = stocks;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.stock_details,
                parent,
                false
        );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.e("Setting",position+"");
        holder.stock_location.setText(Site.findById(Site.class,Long.valueOf(stocks.get(position).getSite())).getName());
        holder.stock_product.setText(stocks.get(position).getProduct());
        holder.stock_quantity.setText(stocks.get(position).getQuantity());
        List<Categoryproduct> categoryproducts = Categoryproduct.find(Categoryproduct.class,
                "product = ?",stocks.get(position).getProduct());
        if(categoryproducts.size() != 0) {
            holder.stock_quantity.setText(holder.stock_quantity.getText()+" "
            +categoryproducts.get(0).getUnit());
        }


        if(stocks.get(position).getStocktype().equalsIgnoreCase("Stock"))
        {
            Picasso.with(context).load((R.drawable.stock)).into(holder.stock_type);
        }
        else {

            Picasso.with(context).load((R.drawable.crane)).into(holder.stock_type);
        }

    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView stock_location;
        public ImageView stock_type;
        public TextView stock_product;
        public TextView stock_quantity;

        public MyViewHolder(View itemView) {
            super(itemView);
            stock_location=(TextView)itemView.findViewById(R.id.stock_location);
            stock_type= (ImageView)itemView.findViewById(R.id.stock_type);
            stock_quantity = (TextView)itemView.findViewById(R.id.stock_quantity);
            stock_product = (TextView)itemView.findViewById(R.id.stock_product);

        }
    }

    public void refreshAdapter(List<Stock> data) {
        this.stocks.clear();
        this.stocks.addAll(data);
        notifyDataSetChanged();
    }
}