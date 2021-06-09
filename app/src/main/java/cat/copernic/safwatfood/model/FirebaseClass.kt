package cat.copernic.safwatfood.model

import com.google.firebase.auth.FirebaseAuth

class FirebaseClass {
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
}