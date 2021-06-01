package cat.copernic.safwatfood

import android.os.Bundle
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import cat.copernic.safwatfood.adapter.MyProducteAdapter
import cat.copernic.safwatfood.listener.IProducteLoadListener
import cat.copernic.safwatfood.model.ProducteModel
import cat.copernic.safwatfood.utils.SpaceItemDecoration
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
        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_productes.layoutManager = gridLayoutManager
        recycler_productes.addItemDecoration(SpaceItemDecoration())
    }
    //Datos de producte carregat satisfactòriament !
    override fun onProducteLoadSuccess(producteModelList: List<ProducteModel>?) {
        val adapter = MyProducteAdapter(this, producteModelList!!)
        recycler_productes.adapter = adapter
    }

    override fun onProducteLoadFailed(message: String?) {
        Snackbar.make(listLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

}