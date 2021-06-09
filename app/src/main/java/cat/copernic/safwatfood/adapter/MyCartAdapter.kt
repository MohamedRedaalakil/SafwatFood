package cat.copernic.safwatfood.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.safwatfood.R
import cat.copernic.safwatfood.eventos.UpdateCartEvent
import cat.copernic.safwatfood.model.CartModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_cart_item.view.*
import org.greenrobot.eventbus.EventBus


class MyCartAdapter(
    private val context: Context,
    private val cartModelList: List<CartModel>
) : RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {


    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declaramos las variables de nuestro layout
        var btnMinus: ImageView? = null
        var btnPlus: ImageView? = null
        var imageView: ImageView? = null
        var btnDelete: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null
        var txtQuantity: TextView? = null

        //Enlazamos nuestras variables creadas de nuestro layout
        init {
            btnMinus = itemView.findViewById(R.id.btnMinus) as ImageView
            btnPlus = itemView.findViewById(R.id.btnPlus) as ImageView
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            btnDelete = itemView.findViewById(R.id.btnDelete) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            txtQuantity = itemView.findViewById(R.id.txtQuantity) as TextView

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item, parent, false)
        )

    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(cartModelList[position].name)
        holder.txtPrice!!.text = StringBuilder("€").append(cartModelList[position].price)
        holder.txtQuantity!!.text = StringBuilder("").append(cartModelList[position].quantity)

        //Evento boton menos
        holder.btnMinus!!.setOnClickListener { _ -> minusCartItem(holder, cartModelList[position]) }
        holder.btnPlus!!.setOnClickListener { _ -> plusCartItem(holder, cartModelList[position]) }
        holder.btnDelete!!.setOnClickListener { _ ->
            //Habilitar el boton de borrar en el firebase

            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            val dialog = AlertDialog.Builder(context)
                .setTitle("Vols eliminar aquest producte?")
                .setMessage("Segur d'esborrar aquest producte?")
                .setNegativeButton("CANCELAR") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("ESBORRAR") { dialog, _ ->

                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance()
                        .getReference("Cart")
                        .child(uid!!)
                        .child(cartModelList[position].key!!)
                        .removeValue()
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(UpdateCartEvent())
                        }
                }
                .create()
            dialog.show()
        }

    }

    //Funcion para suma a la base de datos de firebase con el boton más
    private fun plusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        cartModel.quantity += 1
        cartModel.totalPrice = cartModel.quantity * cartModel.price!!.toFloat()
        holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
        updateFirebase(cartModel)
    }

    //Funcion para restar la base de datos de firebase con el boton menos
    private fun minusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        if (cartModel.quantity > 1) {
            cartModel.quantity -= 1
            cartModel.totalPrice = cartModel.quantity * cartModel.price!!.toFloat()
            holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
            updateFirebase(cartModel)
        }
    }

    // Funcion para actualizar datos en el Firebase
    private fun updateFirebase(cartModel: CartModel) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child(uid!!)
            .child(cartModel.key!!)
            .setValue(cartModel)
            .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }

    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }


}