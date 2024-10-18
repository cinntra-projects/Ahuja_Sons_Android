package com.ahuja.sons.ahujaSonsClasses.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.ChatActivity
import com.ahuja.sons.ahujaSonsClasses.activity.DeliveryCoordinatorActivity
import com.ahuja.sons.ahujaSonsClasses.activity.ParticularOrderDetailActivity
import com.ahuja.sons.ahujaSonsClasses.activity.SalesPersonRoleDetailActivity
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.demo.ChildAdapter
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalSelectedOrder
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderListModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.databinding.ItemWorkQueueBinding
import com.ahuja.sons.databinding.RouteInnerItemsLayoutBinding
import com.ahuja.sons.globals.Global
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.lid.lib.LabelTextView
import com.pixplicity.easyprefs.library.Prefs
import java.util.*

class OrderListForDeliveryCoordinatorAdapter(var AllitemsList: ArrayList<AllWorkQueueResponseModel.Data>,
    var where: String, var isMultiOrderCardSelectEnabled: Boolean, var checkBox: CheckBox) :
    RecyclerView.Adapter<OrderListForDeliveryCoordinatorAdapter.Category_Holder>() {

    private lateinit var context: Context
    var tempList = ArrayList<AllWorkQueueResponseModel.Data>()

    var isAllSelectedMethodisWorking = false
    private var onItemClickListener: ((AllWorkQueueResponseModel.Data, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (AllWorkQueueResponseModel.Data, Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_work_queue, parent, false)
        val binding = RouteInnerItemsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        tempList.clear()
        tempList.addAll(AllitemsList)
        context = parent.context
        return Category_Holder(binding)

    }


    fun isUpdated(isMultiOrder: Boolean) {
        this.isMultiOrderCardSelectEnabled = isMultiOrder
        notifyDataSetChanged()
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        val model = AllitemsList[position]

        holder.bind(model, context)

        // todo Initialize child RecyclerView
      /*  val childAdapter = DeliveryCoordinatorIDsAdapter(model.childItemList)
        holder.binding.deliveryIdRecyclerView.adapter = childAdapter
        holder.binding.deliveryIdRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
*/

        //todo dropdown arrows for delivery id's--
        holder.binding.deliveryIDUpArrow.setOnClickListener {
            holder.binding.deliveryIdRecyclerView.visibility = View.GONE
            holder.binding.deliveryIDUpArrow.visibility = View.GONE
            holder.binding.deliveryIDDownArrow.visibility = View.VISIBLE
        }

        holder.binding.deliveryIDDownArrow.setOnClickListener {
            holder.binding.deliveryIdRecyclerView.visibility = View.VISIBLE
            holder.binding.deliveryIDDownArrow.visibility = View.GONE
            holder.binding.deliveryIDUpArrow.visibility = View.VISIBLE
        }

        if (isMultiOrderCardSelectEnabled) {
            holder.binding.profilePic.visibility = View.INVISIBLE
            holder.binding.checkBoxOrder.visibility = View.VISIBLE

        } else {
            holder.binding.checkBoxOrder.visibility = View.INVISIBLE
            holder.binding.profilePic.visibility = View.VISIBLE

        }

        with(AllitemsList[position]){
            holder.binding.tvOrderId.text = "Order ID : "+OrderRequest?.id
            holder.binding.tvOrderName.text = OrderRequest?.CardName
            if (OrderRequest!!.Doctor.isNotEmpty()){
                holder.binding.tvOrderDoctorName.text = OrderRequest!!.Doctor[0].DoctorFirstName + " "+OrderRequest!!.Doctor[0].DoctorLastName
            }
            holder.binding.tvSurgeryDateTime.text = "Surgery Date:${Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(OrderRequest?.SurgeryDate)}\n Surgery Time: ${OrderRequest?.SurgeryTime}"
            if (is_errands == true){
                holder.binding.tvStatusOrder.text = "Created"
            }else{
                holder.binding.tvStatusOrder.text = OrderRequest?.Status
            }
//            holder.binding.tvStatusOrder.text = OrderRequest?.Status
            holder.binding.tvOrderInfo.text = OrderRequest?.OrderInformation
        }



        holder.binding.profilePic.setOnClickListener {

            if (AllitemsList.size > 0) {
                /*  if (AllitemsList[position].Status != "Resolved") {
                      SelectEmployeeDialogFragment.newInstance("ID", AllitemsList[position].id.toString()).show((context as AppCompatActivity).supportFragmentManager, SelectEmployeeDialogFragment.TAG)
                  } else {
                      Global.warningdialogbox(context, "Your ticket is resolved")

                  }*///todo comment
            }


        }


        if (AllitemsList[position].is_return == true){
            holder.binding.deliveriesLayoutView.visibility = View.GONE
            holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.return_icon))
        }
        else if (!AllitemsList[position].DeliveryNote.isNullOrEmpty() && AllitemsList[position].is_return == false){
            holder.binding.deliveriesLayoutView.visibility = View.VISIBLE
            holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.dispatched_icon))
        }



        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor

        if (model.CardName.isNotEmpty()) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    model.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            holder.binding.profilePic.setImageDrawable(drawable)
        } else {
            holder.binding.profilePic.background = ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
        }


    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filter(newText: String) {
        Log.e("AllSiz", AllitemsList.size.toString())
        Log.e("tmpSiz", tempList.size.toString())
        AllitemsList.clear()
        if (newText.isEmpty())
            AllitemsList.addAll(tempList)
        else {
            /*  AllitemsList.addAll(tempList.filter {
                  it.AssignToDetails[0].SalesEmployeeName?.lowercase(Locale.getDefault())?.contains(newText)!!
              })*/

        }
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun statusfilter(newText: String) {
        Log.e("AllSiz", AllitemsList.size.toString())
        Log.e("tmpSiz", tempList.size.toString())
        AllitemsList.clear()
        if (newText.isEmpty())
            AllitemsList.addAll(tempList)
        else {
//            AllitemsList.addAll(tempList.filter { it.Status == newText })
        }
        notifyDataSetChanged()
    }


    inner class Category_Holder(var binding: RouteInnerItemsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(currentDocLine: AllWorkQueueResponseModel.Data, context: Context) {

            Log.d("ParentAdapter", "Binding child adapter with ${currentDocLine.DeliveryNote.size} items")

            val innerAdapter = DeliveryCoordinatorIDsAdapter(currentDocLine.InspectedDeliverys, isMultiOrderCardSelectEnabled)
            binding.deliveryIdRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            binding.deliveryIdRecyclerView.adapter = innerAdapter

            innerAdapter.notifyDataSetChanged()


            itemView.setOnClickListener {

                if (Prefs.getString(Global.Employee_role, "").equals("Delivery Coordinator")) {
                    val intent = Intent(itemView.context, DeliveryCoordinatorActivity::class.java)
                    intent.putExtra("id", AllitemsList[adapterPosition].id)
                    context.startActivity(intent)
                }


            }


            if (isAllSelectedMethodisWorking) {

            } else {

            }

            if (GlobalClasses.cartListForDeliveryCoordinatorCheck.isNotEmpty()) {

                for ((k, v) in GlobalClasses.cartListForDeliveryCoordinatorCheck) {
                    if (k.equals(currentDocLine.id)) {
                        binding.checkBoxOrder.visibility = View.VISIBLE
                        binding.profilePic.visibility = View.INVISIBLE
                        binding.checkBoxOrder.isChecked = true
                    } else {
                        binding.checkBoxOrder.visibility = View.INVISIBLE
                        binding.profilePic.visibility = View.VISIBLE
                        binding.checkBoxOrder.isChecked = false
                    }
                }

            }


            binding.checkBoxOrder.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    isAllSelectedMethodisWorking = false
                    var localSelectedOrder = LocalSelectedOrder()
                    localSelectedOrder.apply {
                        orderId = currentDocLine.OrderRequest!!.id.toString()//id
                        orderName = currentDocLine.CardName
                    }

                    GlobalClasses.cartListForDeliveryCoordinatorCheck[currentDocLine.OrderRequest!!.id.toString()] = localSelectedOrder

                    val newColorStateList = ColorStateList.valueOf(context.resources.getColor(R.color.blue_light))
                    binding.constraintLayoutWorkQueue.backgroundTintList = newColorStateList

                    //todo trial
                    val itemsToAdd = currentDocLine.InspectedDeliverys.filterNot { GlobalClasses.deliveryIDsList.contains(it) }

                    if (itemsToAdd.isNotEmpty()) {
                        GlobalClasses.deliveryIDsList.addAll(itemsToAdd)
                        Log.e("childItemCheck==>", "onBindViewHolder: Added new items")
                    } else {
                        Log.e("childItemCheck==>", "onBindViewHolder: Already exists")
                    }

                }
                else {
                    isAllSelectedMethodisWorking = false
                    var pos = -1

                    val newColorStateList = ColorStateList.valueOf(Color.WHITE)
                    binding.constraintLayoutWorkQueue.backgroundTintList = newColorStateList

                    GlobalClasses.cartListForDeliveryCoordinatorCheck.remove(currentDocLine.OrderRequest!!.id.toString())

                    //todo trial
                    GlobalClasses.deliveryIDsList.removeAll(currentDocLine.InspectedDeliverys)

                }


                Log.e("SELECTED ORDER>>>>>", "bind: ${GlobalClasses.cartListForDeliveryCoordinatorCheck.size}")

                for (item in GlobalClasses.cartListForDeliveryCoordinatorCheck) {
                    Log.e("SELECTED ORDER>>>>>", "bind: ${item.toString()}")
                }


                //todo trial--
                currentDocLine.InspectedDeliverys.forEach { it.isSelected = b }
                innerAdapter.notifyDataSetChanged()

                Log.e("SELECTED ORDER>>>>>", "bindParent : ${GlobalClasses.deliveryIDsList.size}")

                for (item in GlobalClasses.deliveryIDsList) {
                    Log.e("SELECTED ORDER12>>>>>", "bindParent: ${item.toString()}")
                }


            }


        }


    }


    }