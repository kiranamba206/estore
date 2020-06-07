package com.vetzforpetz.estore.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vetzforpetz.estore.Interface.ItemClickListener;
import com.vetzforpetz.estore.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductPrice, txtProductQuantity, txtCustomMessage;
    private ItemClickListener itemClickListner;


    public CartViewHolder(View itemView)
    {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_item_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_item_product_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_item_product_quantity);
        txtCustomMessage = itemView.findViewById(R.id.cart_item_custom_message);

    }

    @Override
    public void onClick(View view)
    {
        itemClickListner.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListener itemClickListner)
    {
        this.itemClickListner = itemClickListner;
    }
}
