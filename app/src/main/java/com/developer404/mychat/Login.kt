package com.developer404.mychat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.developer404.mychat.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class Login : AppCompatActivity(),View.OnClickListener {
    val auth= FirebaseAuth.getInstance()
    val db= FirebaseFirestore.getInstance()
    lateinit var varification_id:String
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener(this)




    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
               R.id.login_btn->{
                   val code = binding.countryCode.selectedCountryCode
                   val number = binding.loginNumber.text
                   val full_num="+"+code+number

                   if (!binding.loginOptLay.isVisible && !binding.loginUsernameLay.isVisible){
                       if (number != null ) {
                           Toast.makeText(applicationContext,"1st",Toast.LENGTH_LONG).show()
                           sendOtp(full_num)
                       }
                   }
                   else if(binding.loginOptLay.isVisible && !binding.loginUsernameLay.isVisible){
                       Toast.makeText(applicationContext,"2nd",Toast.LENGTH_LONG).show()
                       val otp = binding.loginOtp.text.toString()
                       val credential = PhoneAuthProvider.getCredential(varification_id, otp)
                       sign_in(credential)
                   }
                   else if(binding.loginOptLay.isVisible && binding.loginUsernameLay.isVisible){
                       Toast.makeText(applicationContext,"3rd",Toast.LENGTH_LONG).show()
                   }
//                   val full_num="+"+code+number
//                   sendOtp(full_num)


//                   Toast.makeText(this,code+number,Toast.LENGTH_SHORT).show()
//                   binding.loginOptLay.visibility = View.VISIBLE
               }
            }
        }
    }
    fun sendOtp(full_num:String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(full_num) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(object :OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {


                    auth.signInWithCredential(p0).addOnCompleteListener {
                        Toast.makeText(applicationContext,"verified",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
//                               Toast.makeText(applicationContext,p0.toString(),Toast.LENGTH_LONG).show()
                    Log.e("erorr", "onVerificationFailed: "+ p0.toString() )
                }

                override fun onCodeSent(
                    p0: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    varification_id=p0
                    binding.loginOptLay.visibility= View.VISIBLE
                    Toast.makeText(applicationContext,"OTP Sent Successfully",Toast.LENGTH_LONG).show()
                    super.onCodeSent(p0, p1)
                }
            }) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun sign_in(credential: PhoneAuthCredential){
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signin", "signInWithCredential:success")

                        val user = task.result?.user
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w("signin", "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                        // Update UI
                    }
                }

    }

    fun adduser(userName:String){

        val user: MutableMap<String, Any> = HashMap()
        user[auth.uid.toString()] = "Ada"
        db.collection("users")
            .add(userName)
            

    }
}


