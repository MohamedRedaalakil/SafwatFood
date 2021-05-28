package cat.copernic.safwatfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Usuari")

        register()
    }

    private fun register() {


        registerButton.setOnClickListener {

                if(TextUtils.isEmpty(firstnameInput.text.toString())) {
                    firstnameInput.setError("Escriu el nom ...")
                    return@setOnClickListener
                } else if(TextUtils.isEmpty(lastnameInput.text.toString())) {
                    firstnameInput.setError("Escriu el cognom ... ")
                    return@setOnClickListener
                }else if(TextUtils.isEmpty(usernameInput.text.toString())) {
                    firstnameInput.setError("Escriu el correu ...")
                    return@setOnClickListener
                }else if(TextUtils.isEmpty(passwordInput.text.toString())) {
                    firstnameInput.setError("Escriu la contrasenya ...")
                    return@setOnClickListener
                }


            auth.createUserWithEmailAndPassword(usernameInput.text.toString(), passwordInput.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        val currentUser = auth.currentUser
                        val currentUSerDb = databaseReference?.child((currentUser?.uid!!))
                        currentUSerDb?.child("Nom ")?.setValue(firstnameInput.text.toString())
                        currentUSerDb?.child("Cognom ")?.setValue(lastnameInput.text.toString())

                        Toast.makeText(this@RegistrationActivity, "S'ha Registrat correctament ! ", Toast.LENGTH_LONG).show()
                        finish()

                    } else {
                        Toast.makeText(this@RegistrationActivity, "Registre fallit , intenta-ho de nou! ", Toast.LENGTH_LONG).show()

                    }
                }
        }
    }
}