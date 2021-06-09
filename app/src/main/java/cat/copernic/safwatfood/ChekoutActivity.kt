package cat.copernic.safwatfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_chekout.*

class ChekoutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chekout)


        btnOk.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

    }
}