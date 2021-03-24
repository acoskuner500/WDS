package com.example.wds


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wds.databinding.ActivityMainBinding
import com.example.wds.fragments.choose.ChooseFragment
import com.example.wds.fragments.log.LogFragment
import com.example.wds.fragments.verify.VerifyFragment
import com.example.wds.utilities.prefs
import com.example.wds.utilities.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            signOut()
            return
        }
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var infoTitle = getString(R.string.info_verify_title)
        var infoBody = getString(R.string.info_verify)

        // Initialize
        setCurrentFragment(VerifyFragment())
        binding.apply {
            bottomNavigation.selectedItemId = R.id.miVerify
            toolbar.title = getString(R.string.title_verify)
            infoBtn.setOnClickListener {
                openDialog(infoTitle, infoBody, true)
            }
            logoutBtn.setOnClickListener {
                openDialog("Log out", "Are you sure you want to log out?", false)
            }
            bottomNavigation.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.miVerify -> {
                        toolbar.title = getString(R.string.title_verify)
                        infoTitle = getString(R.string.info_verify_title)
                        infoBody = getString(R.string.info_verify)
                        setCurrentFragment(VerifyFragment())
                    }
                    R.id.miLog -> {
                        toolbar.title = getString(R.string.title_log)
                        infoTitle = getString(R.string.info_log_title)
                        infoBody = getString(R.string.info_log)
                        setCurrentFragment(LogFragment())
                    }
                    R.id.miChoose -> {
                        toolbar.title = getString(R.string.title_choose)
                        infoTitle = getString(R.string.info_choose_title)
                        infoBody = getString(R.string.info_choose)
                        setCurrentFragment(ChooseFragment())
                    }
                }
                true
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun openDialog(title: String, body: String, isInfo: Boolean) {
        AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
            setTitle(title)
            setMessage(body)
            if (!isInfo) {
                setPositiveButton("CANCEL") { _, _ -> }
                setNegativeButton("LOG OUT") { _, _ ->
                    signOut()
                }
            }
            show()
        }
    }

    private fun signOut() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("new-deterred")
            .addOnCompleteListener { task ->
                val msg =
                    if (task.isSuccessful) "Unsubscribed from notifications"
                    else "Failed to unsubscribe from notifications"
                toast(this,msg)
            }
        prefs(this).edit().putBoolean("subscribed",false).apply()
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}