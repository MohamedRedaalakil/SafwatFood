package cat.copernic.safwatfood.listener

import cat.copernic.safwatfood.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModelList: List<CartModel>)
    fun onLoadCartFailed(message:String?)
}