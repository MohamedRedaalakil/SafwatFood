package cat.copernic.safwatfood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cat.copernic.safwatfood.listener.IProducteLoadListener
import cat.copernic.safwatfood.model.ProducteModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main_list.*


class MainList: AppCompatActivity(), IProducteLoadListener {

    //declaramos la variablelateinit de producte para leer la lista
    lateinit var producteLoadListener: IProducteLoadListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        init()

        loadProducteFromFirebase()

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
    }

    override fun onProducteLoadSuccess(producteModelList: List<ProducteModel>?) {

    }

    override fun onProducteLoadFailed(message: String?) {
        Snackbar.make(listLayout,message,Snackbar.LENGTH_LONG).show()
    }

}