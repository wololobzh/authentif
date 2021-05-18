package fr.eni.authentif

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.eni.authentif.model.User
import kotlinx.android.synthetic.main.activity_zone_secure.*

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class ZoneSecureActivity : AppCompatActivity() {

    companion object {
        val TAG = "ACOS"
    }

    private lateinit var auth: FirebaseAuth
    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_secure)
        auth = Firebase.auth
        val query = db.collection("users")

        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).setLifecycleOwner(this).build()

        val adapter = object : FirestoreRecyclerAdapter<User, UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val view = LayoutInflater.from(this@ZoneSecureActivity).inflate(android.R.layout.simple_list_item_2,parent, false)
                return UserViewHolder(view)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                val tvLogin: TextView = holder.itemView.findViewById(android.R.id.text1)
                val tvNumero: TextView = holder.itemView.findViewById(android.R.id.text2)
                tvLogin.text = model.displayName
                tvNumero.text = model.score
                Log.i(TAG,"Login : " + model.displayName)
                Log.i(TAG,"Login : " + model.score)
            }
        }
        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_deconnexion -> {
                auth.signOut()
                finish()
            }
            R.id.item_modifier -> {
                val editText = EditText(this)
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Modifier numéro")
                    .setView(editText)
                    .setNegativeButton("Cancel",null)
                    .setPositiveButton("OK",null)
                    .show()

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val saisie = editText.text.toString()
                    val user = auth.currentUser
                    db.collection("users").document(user.uid).update("score",saisie)
                    dialog.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}