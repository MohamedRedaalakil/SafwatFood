package cat.copernic.safwatfood.listener

import cat.copernic.safwatfood.model.ProducteModel

interface IProducteLoadListener {
    //acceso al modelo de  la lista de productos correctamente
    fun onProducteLoadSuccess(producteModelList:List<ProducteModel>?)
    // acceso al modelo de productos fallido y mostrar mensaje
    fun onProducteLoadFailed(message:String?)

}