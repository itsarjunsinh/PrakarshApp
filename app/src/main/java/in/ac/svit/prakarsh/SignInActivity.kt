package `in`.ac.svit.prakarsh

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    // Request code to be assigned when starting Google login activity.
    private val RC_SIGN_IN = 9001

    private var phoneNumber: Long = 0
    private lateinit var name: String
    private lateinit var collegeName: String
    private lateinit var department: String
    private lateinit var city: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

        account_btn_confirm?.setOnClickListener {
            var validPhone = false
            var validText = false

            // Validate phone number.
            val phoneNumber = sign_in_et_phone?.text.toString()
            val numberLength = phoneNumber.length

            if (numberLength == 10 || numberLength == 12) {

                if (numberLength == 12) {
                    if (phoneNumber.startsWith("91")) {
                        validPhone = true
                    }
                } else {
                    validPhone = true
                }

            }

            // Validate text fields.
            name = sign_in_et_name?.text.toString()
            collegeName = sign_in_et_college_name?.text.toString()
            department = sign_in_et_department?.text.toString()
            city = sign_in_et_city?.text.toString()

            if (name.length > 1 && collegeName.length > 1 && department.length > 1 && city.length > 1) {
                validText = true
            }

            // Check whether inputs are valid.
            if (validPhone && validText) {
                this.phoneNumber = phoneNumber.toLong()
                setupLoginButton()
            } else {
                // Invalid input(s). Show message in Snackbar.
                var errorMessage = ""

                if (!validText)     errorMessage = "Input length too small. "
                if (!validPhone)    errorMessage += "Invalid phone number."

                Snackbar.make(sign_in_layout_main, errorMessage, Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                // Google sign in was successful, authenticate with Firebase.
                val account = task.getResult(ApiException::class.java)
                Snackbar.make(sign_in_layout_main, "Please wait. Logging you in.", Snackbar.LENGTH_LONG).show()
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google sign in failed, alert the user using Snackbar.
                Log.d(javaClass.name, "Google sign in failed. Error: ${e.message}", e)
                Snackbar.make(sign_in_layout_main, "Google sign in failed", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(javaClass.name, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, call function to store user's data to the Firestore database.
                        Log.d(javaClass.name, "signInWithCredential:success")
                        storeDataAndExit()
                    } else {
                        // Sign in failed, display a message to the user.
                        Log.w(javaClass.name, "signInWithCredential:failure", task.exception)
                        Snackbar.make(sign_in_layout_main, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    }

                }
    }

    private fun setupLoginButton() {
        // Hide "Confirm" button.
        account_btn_confirm.visibility = View.GONE

        // Configure Google sign in.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_login_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Setup Google login button.
        sign_in_btn_google?.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                val signInIntent: Intent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }

    }

    private fun storeDataAndExit() {
        /*
         * Gets user's information and stores them in a Firestore database.
         * After upload task is finished exit the activity.
         */

        fun handleFailure() {
            // Sign out the user and exit the activity.
            Toast.makeText(applicationContext, "Could not upload data.", Toast.LENGTH_SHORT).show()
            mAuth.signOut()
            finish()
        }

        val userDetails = HashMap<String, Any>()

        try {
            val uuid = mAuth.currentUser!!.uid
            val mDocRef = FirebaseFirestore.getInstance().document("users/$uuid")

            val email = "${mAuth.currentUser!!.email}"

            userDetails.put("name", name)
            userDetails.put("email", email)
            userDetails.put("phoneNumber", phoneNumber)
            userDetails.put("collegeName", collegeName)
            userDetails.put("department", department)
            userDetails.put("city", city)

            mDocRef.set(userDetails).addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful) {
                        // Data upload successful, exit the activity.
                        Log.d(javaClass.name, "Saved user data successfully.")
                        finish()
                    } else {
                        // Data upload failed, call function that logs out the user and exits the activity.
                        Log.d(javaClass.name, "Could not upload data.", task.exception)
                        handleFailure()
                    }
                }
            })

        } catch (e: Exception) {
            // Exception Occurred, call function that logs out the user and exits the activity.
            Log.d(javaClass.name, "Could not upload data. Exception: ${e.message}", e)
            handleFailure()
        }
    }
}
