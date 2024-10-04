package com.ahuja.sons.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.ahuja.sons.R
import com.ahuja.sons.activity.LoginActivity
import com.ahuja.sons.databinding.ProfilePageBinding
import com.ahuja.sons.globals.Global
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.ahujaSonsClasses.activity.AhujaSonsMainActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import taimoor.sultani.sweetalert2.Sweetalert
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null


    private lateinit var ticketFragment: ProfilePageBinding
    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        (activity as AppCompatActivity?)!!.findViewById<CollapsingToolbarLayout>(R.id.collapsetoolbar).visibility =
            View.GONE

    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketFragment = ProfilePageBinding.inflate(layoutInflater)
        viewModel = (activity as AhujaSonsMainActivity).viewModel
        ticketFragment.companyName.text = Prefs.getString(Global.Employee_Name, "")
        ticketFragment.mail.text = "Sales Emp. Code: ${Prefs.getString(Global.Employee_Code, "")}"
        ticketFragment.role.text = " ${Prefs.getString(Global.Employee_role, "")}"

        if (Prefs.getString(Global.Employee_role, "") == "Service Engineer"){
            ticketFragment.tvEmployeeID.visibility = View.GONE
        }else{
            ticketFragment.tvEmployeeID.visibility = View.VISIBLE
            ticketFragment.tvEmployeeID.text = "OTP - " +Global.formatAs6DigitNumber(Prefs.getString(Global.Employee_SalesEmpCode).toInt())
        }

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        builder = AlertDialog.Builder(requireContext())
       builder!!.setView(R.layout.dialog_progress).setCancelable(false)


      alertDialog = builder!!.create()

        if (ticketFragment.companyName.text.isNotEmpty()) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    ticketFragment.companyName.text[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            ticketFragment.nameIcon.setImageDrawable(drawable)
        }

        ticketFragment.delete.setOnClickListener {
            openlogoutdialog()
        }

        ticketFragment.clearcache.setOnClickListener {
            deleteCache(requireContext())
        }
        ticketFragment.rateus.setOnClickListener {
            rateusmethod()
        }
        ticketFragment.feedback.setOnClickListener {
            rateusmethod()
        }



        return ticketFragment.root
    }

    private fun rateusmethod() {
        val uri: Uri = Uri.parse("market://details?id=com.cinntra.servicesupportportal")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.cinntra.servicesupportportal")
                )
            )
        }
    }

    private fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
//        Toast.makeText(requireContext(),"Clear cache Successfully",Toast.LENGTH_SHORT).show()
    }

    private fun openlogoutdialog() {
        val pDialog = Sweetalert(context, Sweetalert.WARNING_TYPE)
        pDialog.titleText = "Are you sure?"
        pDialog.contentText = "You want to logout"
        pDialog.setCanceledOnTouchOutside(false)
        pDialog.cancelText = "No,cancel it!"
        pDialog.confirmText = "Yes,Logout!"
        pDialog.showCancelButton(true)
        pDialog.showConfirmButton(true)
        pDialog.show()

        pDialog.setCancelClickListener { sDialog ->
            sDialog.dismiss()

        }
        pDialog.setConfirmClickListener {
            var hashMap=HashMap<String,String>()
            hashMap["userName"]=Prefs.getString(Global.LogInUserName)
            hashMap["password"]=Prefs.getString(Global.LogInPassword)
            hashMap["FCM"]=""
            viewModel.logoutEmployeeNew(hashMap)
            subscribeToObserver(pDialog)


        }
    }

    companion object{
        private const val TAG = "ProfileFragment"
    }
    private fun subscribeToObserver(pDialog: Sweetalert) {
        viewModel.userlogout.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserver: $it",)
               // binding.loader.visibility = View.GONE
                alertDialog?.dismiss()
                Global.errormessagetoast(requireContext(), it)

                if (it=="For input string: \"NA\""){

                }
                //  Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }, onLoading = {
                alertDialog?.show()
               // binding.loader.visibility = View.VISIBLE
            }, {
                alertDialog?.dismiss()
                Log.e(TAG, "subscribeToObserver: ${it.status}", )
               //

                if (it.status == 200) {

                    Prefs.clear()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                } else {
                    Global.errormessagetoast(requireContext(), it.message)
                }

            }

        ))

    }


    override fun onDestroy() {
        if (alertDialog!!.isShowing) {
            alertDialog?.dismiss()
        }
        super.onDestroy()
    }

    override fun onDetach() {
        alertDialog?.dismiss()
        super.onDetach()
    }

}
