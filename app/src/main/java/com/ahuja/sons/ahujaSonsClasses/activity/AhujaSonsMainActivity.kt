package com.ahuja.sons.ahujaSonsClasses.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.databinding.ActivityAhujaSonsMainBinding
import com.ahuja.sons.databinding.DialogAssignDeliveryPersonBinding
import com.ahuja.sons.globals.Global

class AhujaSonsMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityAhujaSonsMainBinding
    lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAhujaSonsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavController()

        binding.chipAssign.setOnClickListener {
            if (GlobalClasses.cartListForOrderRequest.isNotEmpty()) {
                openDeliveryPersonDialog(this)
            } else {
                Global.infomessagetoast(this, "No Order Selected")
            }

        }

    }


    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_ahuja_sons) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(
            binding.navigationView,
            navController
        )

    }


    lateinit var dialogBinding: DialogAssignDeliveryPersonBinding

    private fun openDeliveryPersonDialog(context: Context) {

        val dialog = Dialog(context, R.style.Theme_Dialog)

        val layoutInflater = LayoutInflater.from(context)
        dialogBinding = DialogAssignDeliveryPersonBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = 400
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }


        dialogBinding.btnSave.setOnClickListener {
            Global.successmessagetoast(this, "Assign SuccessFully")
        }


        dialogBinding.tvTitle.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()


    }
}