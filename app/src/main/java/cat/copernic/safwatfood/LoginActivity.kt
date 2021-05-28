package cat.copernic.safwatfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val currentuser = auth.currentUser
        if(currentuser != null) {
            startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
            finish()
        }
        login()
    }

    private fun login() {

        loginButton.setOnClickListener {
            // Error al no introducir usuario
            if(TextUtils.isEmpty(usernameInput.text.toString())){
                usernameInput.setError("Siusplau escriu el nom!")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(passwordInput.text.toString())){
                usernameInput.setError("Siusplau escriu la password")
                return@setOnClickListener
            }
            // Fallo al entrar
            auth.signInWithEmailAndPassword(usernameInput.text.toString(), passwordInput.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login fallit, siusplau intenta-ho de nou! ", Toast.LENGTH_LONG).show()
                    }
                }

        }

        registerText.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))

        }
    }
}