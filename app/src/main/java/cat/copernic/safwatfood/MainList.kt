package cat.copernic.safwatfood

import android.content.Intent
import android.os.Bundle
import android.util.EventLog
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import cat.copernic.safwatfood.adapter.MyProducteAdapter
import cat.copernic.safwatfood.eventos.UpdateCartEvent
import cat.copernic.safwatfood.listener.ICartLoadListener
import cat.copernic.safwatfood.listener.IProducteLoadListener
import cat.copernic.safwatfood.model.CartModel
import cat.copernic.safwatfood.model.ProducteModel
import cat.copernic.safwatfood.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_main_list.*
import kotlinx.android.synthetic.main.activity_main_list.listLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainList: AppCompatActivity(), IProducteLoadListener, ICartLoadListener {

    //declaramos la variable lateinit de producte para leer la lista
    lateinit var producteLoadListener: IProducteLoadListener
    lateinit var cartLoadListener: ICartLoadListener


    //Hilo para mostrar las notificaciones
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }
    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
            EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public fun onUpdateCartEvent(event:UpdateCartEvent){
        countCartFromFirebase()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        init()
        loadProducteFromFirebase()
        countCartFromFirebase()

    }
    // Funcion para contar la cesta en el firebase
    private fun countCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("Usuari")
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (cartSnapshot in snapshot.children){

                            val cartModel = cartSnapshot.getValue(CartModel::class.java)
                            cartModel!!.key = cartSnapshot.key
                            cartModels.add(cartModel)

                        }
                        cartLoadListener!!.onLoadCartSuccess(cartModels)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cartLoadListener!!.onLoadCartFailed(error.message)
                    }

                })
    }
    //cargar productos de firebase
    private fun loadProducteFromFirebase() {
        val producteModels : MutableList<ProducteModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Producte")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        for (producteSnapshot in snapshot.children)
                        {
                            val producteModel = producteSnapshot.getValue(ProducteModel::class.java)
                            producteModel!!.key = producteSnapshot.key
                            producteModels.add(producteModel)
                        }
                        producteLoadListener.onProducteLoadSuccess(producteModels)
                    }else
                        producteLoadListener.onProducteLoadFailed("Item Producte no existeix! ")
                }

                override fun onCancelled(error: DatabaseError) {
                    producteLoadListener.onProducteLoadFailed(error.message)
                }

            })
    }
    private fun init(){
        producteLoadListener = this
        cartLoadListener = this


        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_productes.layoutManager = gridLayoutManager
        recycler_productes.addItemDecoration(SpaceItemDecoration())

        btnCart.setOnClickListener {  startActivity(Intent(this,CartActivity::class.java))}
        btnBack2.setOnClickListener { finish() }
    }
    //Datos de producte carregat satisfactòriament !
    override fun onProducteLoadSuccess(producteModelList: List<ProducteModel>?) {
        val adapter = MyProducteAdapter(this, producteModelList!!,cartLoadListener)
        recycler_productes.adapter = adapter
    }
    override fun onProducteLoadFailed(message: String?) {
        Snackbar.make(listLayout,message!!,Snackbar.LENGTH_LONG).show()
    }
    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModelList!!) cartSum += cartModel!!.quantity
        //Mostrar la notificacion +1 producto en la cesta
        badge!!.setNumber(cartSum)
    }
    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(listLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

}