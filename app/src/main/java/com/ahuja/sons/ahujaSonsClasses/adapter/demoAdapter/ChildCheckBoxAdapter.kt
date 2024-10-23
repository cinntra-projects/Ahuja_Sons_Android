package com.ahuja.sons.ahujaSonsClasses.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.adapter.demoAdapter.ParentCheckBoxAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.fragments.route.OrderForDeliveryCoordinatorFragment
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalSelectedOrder
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.databinding.InspectionDeliveryIdLayoutBinding
import com.pixplicity.easyprefs.library.Prefs

class ChildCheckBoxAdapter(private val items: ArrayList<AllWorkQueueResponseModel.InspectedDelivery>, var isMultiOrderCardSelectEnabled: Boolean, var checkBOxOuter: CheckBox? = null, ) : RecyclerView.Adapter<ChildCheckBoxAdapter.InnerViewHolder>() {

    interface ClickOnDeliveryID {
        fun onClickDeliveryID(mArrayUriList: ArrayList<Uri>)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val binding = InspectionDeliveryIdLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerViewHolder(binding)
    }

    fun isUpdated(isMultiOrder: Boolean) {
        this.isMultiOrderCardSelectEnabled = isMultiOrder
        notifyDataSetChanged()
    }

    // Method to return the list of items
    fun getItems(): MutableList<AllWorkQueueResponseModel.InspectedDelivery> {
        return items
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)


        if (isMultiOrderCardSelectEnabled) {
            holder.binding.checkBoxOrder.visibility = View.VISIBLE

        } else {
            holder.binding.checkBoxOrder.visibility = View.INVISIBLE

        }

        holder.binding.checkBoxOrder.isChecked = item.isSelected

//        holder.binding.checkBoxOrder.visibility = View.VISIBLE


        /*holder.binding.checkBoxOrder.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked

            if (isChecked) {

                if (GlobalClasses.deliveryIDsList.contains(item)){
                    Log.e("childItemCheck==>", "onBindViewHolder: Already exist CI" )
                }else{
                    Log.e("inner adapter add list", "onBindViewHolder: ID Add on Inner adapter")

                    GlobalClasses.deliveryIDsList.add(item)

                    if (!checkBOxOuter!!.isChecked){
                        Log.e("OrderMain===>", "Order Not Checked" )
                        checkBOxOuter!!.isChecked=true
                    }

                    Prefs.putBoolean(GlobalClasses.isOrderShouldSelected, true)

                    Log.e("tttttttt...", "onBindViewHolder: "+Prefs.getBoolean(GlobalClasses.isOrderShouldSelected) )

                }

                (holder.binding.checkBoxOrder.context as? OrderForDeliveryCoordinatorFragment)?.onChildCheckboxSelected(item)


            } else {

                Log.e("Child_Adapter==>", "onBindViewHolder: Item Remove From Inner Adapter")
                GlobalClasses.deliveryIDsList.remove(item)
            }

            Log.e("SELECTED ORDER>>>>>", "bindChild: ${GlobalClasses.deliveryIDsList.size}")

            for (item in GlobalClasses.deliveryIDsList) {
                Log.e("SELECTED ORDER11>>>>>", "bindChild: ${item.toString()}")
            }

        }*/


        // Child checkbox listener (inside child adapter)
        holder.binding.checkBoxOrder.setOnCheckedChangeListener { _, isChecked ->
            val childItem = items[position]

            if (isChecked) {
                childItem.isSelected = true
                if (!GlobalClasses.deliveryIDsList.contains(childItem)) {
                    Log.e("inner adapter add list", "onBindViewHolder: ID Add on Inner adapter")
                    GlobalClasses.deliveryIDsList.add(childItem)

                    // If any child is selected, check the parent
                    // Mark the parent as selected but set the flag to avoid triggering the parent's logic


                    Prefs.putBoolean(GlobalClasses.isChildCheckTriggeredParent, true)

                    checkBOxOuter!!.isChecked = true


                }else{
                    Log.e("childItemCheck==>", "onBindViewHolder: Already exist CI" )
                }


               /* // Check if any child is selected and set the parent checkbox accordingly
                val isAnyChildSelected = items.any { it.isSelected }
                checkBOxOuter!!.isChecked = isAnyChildSelected

                // If all children are selected, update the parent checkbox state
                val areAllChildrenSelected = items.all { it.isSelected }
                if (areAllChildrenSelected) {
                    holder.binding.checkBoxOrder.isChecked = true
                }*/

            } else {
                Log.e("Child_Adapter==>", "onBindViewHolder: Item Remove From Inner Adapter")
                childItem.isSelected = false
                GlobalClasses.deliveryIDsList.remove(childItem)

                // If no child is selected, uncheck the parent
                val isAnyChildSelected = items.any { it.isSelected }
                if (isAnyChildSelected) {
                    checkBOxOuter!!.isChecked = isAnyChildSelected
                }

                // If all children are deselected, uncheck the parent
                val areAllChildrenDeselected = items.none { it.isSelected }
                if (areAllChildrenDeselected) {
                    checkBOxOuter!!.isChecked = false
                    Prefs.putBoolean(GlobalClasses.isChildCheckTriggeredParent, false)
                }
            }

            Log.e("SELECTED ORDER>>>>>", "bindChild: ${GlobalClasses.deliveryIDsList.size}")

            for (item in GlobalClasses.deliveryIDsList) {
                Log.e("SELECTED ORDER11>>>>>", "bindChild: ${item.toString()}")
            }

            // Delay the notifyDataSetChanged() to avoid IllegalStateException
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                notifyDataSetChanged()
            } // Update the adapter for the child items
        }


    }

    override fun getItemCount(): Int = items.size

    inner class InnerViewHolder(var binding: InspectionDeliveryIdLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AllWorkQueueResponseModel.InspectedDelivery) {
            binding.apply {
                innerTitle.text =  item.DocNum
                tvStatus.text = "Status : "+ item.DeliveryStatus
            }



        }
    }
}