package cat.copernic.safwatfood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.safwatfood.R
import cat.copernic.safwatfood.model.ProducteModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_producte_item.view.*

class MyProducteAdapter (
    private val context: Context,
    private val list:List<ProducteModel>
): RecyclerView.Adapter<MyProducteAdapter.MyProducteViewHolder>(){
    class MyProducteViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
         var imageView: ImageView?=null
         var txtName:TextView?=null
         var txtPrice:TextView?=null

        init {
                imageView = itemView.findViewById(R.id.imageView) as ImageView
                txtName = itemView.findViewById(R.id.txtName) as TextView
                txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
        }


    }

    //Devuelve la vista de items de producte
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProducteViewHolder {
        return MyProducteViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_producte_item,parent,false))
    }
    //Devolver posicion de cada dato de la base de datos en firebase
    override fun onBindViewHolder(holder: MyProducteViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("€").append(list[position].price)

    }

    //devuelve tamaño de la lista
    override fun getItemCount(): Int {
       return list.size
    }
}