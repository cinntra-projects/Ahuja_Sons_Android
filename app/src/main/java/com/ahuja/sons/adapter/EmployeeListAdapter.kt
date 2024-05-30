package com.ahuja.sons.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.activity.UpdateEmployeeActivity
import com.ahuja.sons.databinding.EmployeeListAdapterLayoutBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.EmployeeResponseModel
import java.util.*

class EmployeeListAdapter (val AllitemsList: ArrayList<EmployeeResponseModel.DataXXX>): RecyclerView.Adapter<EmployeeListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<EmployeeResponseModel.DataXXX> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }

    private var onItemClickListener: ((EmployeeResponseModel.DataXXX, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (EmployeeResponseModel.DataXXX, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onResetBtnClick: ((EmployeeResponseModel.DataXXX, String, AlertDialog) -> Unit)? = null

    fun setOnResetBtnClickListener(listener: (EmployeeResponseModel.DataXXX, String, AlertDialog) -> Unit) {
        onResetBtnClick = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(EmployeeListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {

            tvEmployeeName.text = current.firstName + " " + current.lastName
            tvID.text = "E - " +current.id
            tvEmail.text = current.Email
            tvEmail.tooltipText = current.Email
            tvDesignation.text = current.position
            tvPhone.text = current.Mobile
            tvRole.text = current.role
            tvZone.text = current.zone
            tvStatus.text = "Active"

            if (Prefs.getString(Global.Employee_role, "") == "admin"){
                threeDotsLayout.visibility = View.VISIBLE
            }else{
                threeDotsLayout.visibility = View.GONE
            }

            
            threeDotsLayout.setOnClickListener {
                showPopupMenu(holder.binding.threeDotsLayout, current.id, current)
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(current, position)
                }

            }


        }


    }

    private fun showPopupMenu(view: View, id: String, current: EmployeeResponseModel.DataXXX) {
        var popupMenu : PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.employee_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    val intent = Intent(context, UpdateEmployeeActivity::class.java)
                    intent.putExtra("id", id)
                    context.startActivity(intent)
                    true
                }
                R.id.resetPassword ->{
                    openResetButtonDialog(current)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun openResetButtonDialog(current: EmployeeResponseModel.DataXXX) {
        val builder = AlertDialog.Builder(context,  R.style.CustomAlertDialog).create()
        val view = LayoutInflater.from(context).inflate(R.layout.reset_password_layout_popup, null)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        builder.window?.setGravity(Gravity.CENTER)
        builder.setView(view)

        val iv_cancel = view.findViewById<ImageView>(R.id.iv_cancel)
        val saveResetPasswordBtn = view.findViewById<MaterialButton>(R.id.saveResetPasswordBtn)
        var Password = view.findViewById<TextInputEditText>(R.id.Password)

        if (current.password.isNotEmpty()){
            Password.setText(current.password)
        }



        iv_cancel.setOnClickListener {
            builder.cancel()
        }

        saveResetPasswordBtn.setOnClickListener {
            onResetBtnClick?.let { click ->
                click(current, Password.text.toString(), builder)
            }
        }

        builder.setCancelable(true)
        builder.show()


    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: EmployeeListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    //todo search filter..
    fun filter(charText: String) {
        var charText = charText
        charText = charText.lowercase(Locale.getDefault())
        AllitemsList.clear()
        if (charText.length == 0) {
            AllitemsList.addAll(tempList)
        } else {
            for (st in tempList) {
                if (st.firstName != null && !st.firstName.isEmpty()) {
                    if (st.id.toLowerCase(Locale.getDefault()).contains(charText)) {
                        AllitemsList.add(st)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }


}