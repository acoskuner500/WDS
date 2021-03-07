package com.example.wds

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wds.databinding.ActivityMainBinding
import com.example.wds.fragments.choose.ChooseFragment
import com.example.wds.fragments.log.LogFragment
import com.example.wds.fragments.verify.VerifyFragment

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val verifyFragment = VerifyFragment()
    private val logFragment = LogFragment()
    private val chooseFragment = ChooseFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_WDS)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize
        setCurrentFragment(verifyFragment)
        binding.apply {
            infoTitle.text = getString(R.string.info_verify_title)
            infoText.text = getString(R.string.info_verify)
            bottomNavigation.selectedItemId = R.id.miVerify
            toolbar.title = "Verify Deterrences"

            // Info Dialog
            infoBtn.setOnClickListener {
                infoDialog.visibility =
                    if (infoDialog.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            okBtn.setOnClickListener { infoDialog.visibility = View.GONE }
            // Bottom Navigation Selector
            bottomNavigation.setOnNavigationItemSelectedListener {
                infoDialog.visibility = View.GONE
                when (it.itemId) {
                    R.id.miVerify -> {
                        setCurrentFragment(verifyFragment)
                        infoTitle.text = getString(R.string.info_verify_title)
                        infoText.text = getString(R.string.info_verify)
                    }
                    R.id.miLog -> {
                        setCurrentFragment(logFragment)
                        infoTitle.text = getString(R.string.info_log_title)
                        infoText.text = getString(R.string.info_log)
                    }
                    R.id.miChoose -> {
                        setCurrentFragment(chooseFragment)
                        infoTitle.text = getString(R.string.info_choose_title)
                        infoText.text = getString(R.string.info_choose)
                    }
                }
                true
            }
        }

        val notificationService = Intent(this,MyFirebaseMessagingService::class.java)
        startService(notificationService)
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            when (fragment) {
                verifyFragment  -> binding.toolbar.title = "Verify Deterrences"
                logFragment     -> binding.toolbar.title = "Deterrence Log"
                chooseFragment  -> binding.toolbar.title = "Choose Animals to Deter"
            }
            replace(R.id.flFragment, fragment)
            commit()
        }
}