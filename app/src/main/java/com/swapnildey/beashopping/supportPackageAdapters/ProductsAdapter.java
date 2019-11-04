package com.swapnildey.beashopping.supportPackageAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.swapnildey.beashopping.R;
import com.swapnildey.beashopping.supportPackageDataModels.Product;

import java.util.ArrayList;



public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductDataHolder> {

    private Context context;
    private ArrayList<Product> products;

    public class ProductDataHolder extends RecyclerView.ViewHolder {

        public TextView productName,productOffer;
        public CircularImageView productImg;
        Product product;

        public ProductDataHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productOffer = itemView.findViewById(R.id.productOffer);
            productImg = itemView.findViewById(R.id.productImage);
        }

        public void setData(Product product) {
            this.product = product;
            productName.setText(product.getName());
            productOffer.setText(product.getDesc());
            Picasso.with(context).
                    load(product.getImageUrl())
                    .placeholder(R.drawable.product_placeholder)
                    .resize(180,240)
                    .onlyScaleDown()
                    .networkPolicy(NetworkPolicy.NO_CACHE).into(productImg);

        }
    }

    public ProductsAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public ProductDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_procuct, parent, false);
        ProductDataHolder holder=new ProductDataHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDataHolder holder, final int position) {
        holder.setData(products.get(position));

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}


