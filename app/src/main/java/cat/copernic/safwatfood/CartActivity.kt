package cat.copernic.safwatfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.safwatfood.adapter.MyCartAdapter
import cat.copernic.safwatfood.eventos.UpdateCartEvent
import cat.copernic.safwatfood.listener.ICartLoadListener
import cat.copernic.safwatfood.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.listLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : AppCompatActivity(), ICartLoadListener {

    var cartLoadListener: ICartLoadListener?=null

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
    public fun onUpdateCartEvent(event: UpdateCartEvent){
        loadCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        init()
        loadCartFromFirebase()

        homeBtn3.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

        searchBtn3.setOnClickListener {
            val intent = Intent(this,MainList::class.java)
            startActivity(intent)
        }

        btnChekout.setOnClickListener {
            val intent = Intent(this,ChekoutActivity::class.java)
            startActivity(intent)
        }



    }

    private fun loadCartFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child(uid!!)
            .addListenerForSingleValueEvent(object: ValueEventListener {
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

    private fun init(){
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        btnBack!!.setOnClickListener { finish() }

    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var sum = 0.0
        for (cartModel in cartModelList!!){
            sum+= cartModel!!.totalPrice

        }
        txtTotal.text = StringBuilder("€").append(sum)
        val adapter = MyCartAdapter(this,cartModelList)
        recycler_cart!!.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(listLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

}