package cat.copernic.safwatfood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.safwatfood.R
import cat.copernic.safwatfood.eventos.UpdateCartEvent
import cat.copernic.safwatfood.listener.ICartLoadListener
import cat.copernic.safwatfood.listener.IRecyclerClickListener
import cat.copernic.safwatfood.model.CartModel
import cat.copernic.safwatfood.model.ProducteModel
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_producte_item.view.*
import org.greenrobot.eventbus.EventBus

class MyProducteAdapter (
    private val context: Context,
    private val list:List<ProducteModel>,
    private val cartListener: ICartLoadListener

): RecyclerView.Adapter<MyProducteAdapter.MyProducteViewHolder>(){
    class MyProducteViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
         var imageView: ImageView?=null
         var txtName:TextView?=null
         var txtPrice:TextView?=null


        //Declaramos la variable clicklistener para poder  escuchar el reycycler
        private var clickListener:IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener){
            this.clickListener = clickListener;

        }

        init {
                imageView = itemView.findViewById(R.id.imageView) as ImageView
                txtName = itemView.findViewById(R.id.txtName) as TextView
                txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

            //Hacer onclick el itemview
                itemView.setOnClickListener(this)

        }


        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v,adapterPosition)
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


        holder.setClickListener(object:IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
               addToCart(list[position])
            }

        })

    }

    private fun addToCart(producteModel: ProducteModel) {
        val userCart = FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("Usuari")
        userCart.child(producteModel.key!!)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                       if (snapshot.exists()) { //Si el item esta listo en el cesta, se actualiza

                           val cartModel = snapshot.getValue(CartModel::class.java)
                           val updateData : MutableMap<String,Any> = HashMap()
                           cartModel!!.quantity = cartModel!!.quantity+1
                           updateData["quantity"] = cartModel!!.quantity
                           updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()


                           userCart.child(producteModel.key!!)
                                   .updateChildren(updateData)
                                   .addOnSuccessListener {
                                       EventBus.getDefault().postSticky(UpdateCartEvent())
                                       cartListener.onLoadCartFailed("Afegit correctament a la cesta !!")
                                   }
                                   //añadir excepcion en caso de que no se añada correctamente
                                   .addOnFailureListener { exception ->  cartListener.onLoadCartFailed(exception.message) }


                       }else{// si item no esta en la cesta , se añade uno nuevo

                           val cartModel = CartModel()
                           cartModel.key = producteModel.key
                           cartModel.name = producteModel.name
                           cartModel.image = producteModel.image
                           cartModel.price = producteModel.price
                           cartModel.quantity = 1
                           cartModel.totalPrice = producteModel.price!!.toFloat()

                           userCart.child(producteModel.key!!)
                                   .setValue(cartModel)
                                   .addOnSuccessListener {
                                       EventBus.getDefault().postSticky(UpdateCartEvent())
                                       cartListener.onLoadCartFailed("Afegit correctament a la cesta !!")
                                   }
                                   //añadir excepcion en caso de que no se añada correctamente
                                   .addOnFailureListener { exception ->  cartListener.onLoadCartFailed(exception.message) }

                       }
                    }

                    override fun onCancelled(error: DatabaseError) {
                       cartListener.onLoadCartFailed(error.message)
                    }

                })

    }

    //devuelve tamaño de la lista
    override fun getItemCount(): Int {
       return list.size
    }
}