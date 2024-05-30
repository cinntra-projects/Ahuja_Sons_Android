package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.ServceTicketAdapterBinding
import com.ahuja.sons.newapimodel.DataCheckList
import java.util.*


class CheckListTicketAdapter(val AllitemsList: ArrayList<DataCheckList>) : RecyclerView.Adapter<CheckListTicketAdapter.Category_Holder>() {

    private lateinit var context: Context


    private var onItemClickListener: ((DataCheckList) -> Unit)? = null

    fun setOnItemClickListener(listener: (DataCheckList) -> Unit) {
        onItemClickListener = listener
    }


    private var onYesNoSpinnerClickListener: ((String, Int) -> Unit)? = null

    fun setOnYesNoSpinnerClickListener(listener: (String, Int) -> Unit) {
        onYesNoSpinnerClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {

        context = parent.context
        return Category_Holder(ServceTicketAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {
            title.text = current.Name
            message.text = current.Description


           /* val gson = Gson()
            try {
                val person = gson.fromJson(current.Data, Array<DataFromJsonCheckList>::class.java).toList()
                for (instruction in person) {
                    println("Desc: ${instruction.desc}")
                    println("Status: ${instruction.status}")
                    println("Remark: ${instruction.remark}")
                    println()
                }
            } catch (e: Exception) {
                Log.e("TAG", "onBindViewHolderERROR: ${e.message}")
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }*///todo comment by me--


            // Log.e("TAG", "JSON=====>>>>: $person")
            //   val dataset = DataFromJsonCheckList()
            // Log.e("TAG", "JSONObject=====>>>>: ${dataset.desc}")

            if (current.Status == 1 || current.Status == 2) {
                tickGreen.background = context.resources.getDrawable(R.drawable.tick_square_green)
                divider.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                linearCheckList.setBackgroundColor(context.resources.getColor(R.color.green_light))
            } else {
                tickGreen.background = context.resources.getDrawable(R.drawable.tick_square_grey)
                divider.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
                linearCheckList.setBackgroundColor(context.resources.getColor(R.color.red_light))
            }



            holder.itemView.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(current)

                }

//                when(ticketstatus){
//                    "Accepted"->{
//                        if( tickethistorydata[position].Status=="False") {
//                            if (Global.TicketStartDate.isNotEmpty()) {
//                                openCommentDialog(tickethistorydata[position])
//
//                            } else {
//                                Global.warningdialogbox(context,"Start timer to working on it")
//                            }
//                        }
//                    }
//                    "Pending"->{
//                        Global.warningdialogbox(context,"Your ticket is in pending state,Kindly accept it")
//                    }
//                    "Rejected"->{
//                        Global.warningdialogbox(context,"Your ticket will be rejected")
//                    }
//                }
            }

            clickview.setOnClickListener {


//                when (ticketstatus) {
//                    "Accepted" -> {
//                        if (tickethistorydata[position].Status == "False") {
//                            if (Global.TicketStartDate.isNotEmpty()) {
//                                openCommentDialog(tickethistorydata[position])
//
//                            } else {
//                                Global.warningdialogbox(context,"Start timer to working on it")
//                            }
//                        }
//                    }
//                    "Pending" -> {
//                        Global.warningdialogbox(
//                            context,
//                            "Your ticket is in pending state,Kindly accept it"
//                        )
//                    }
//                    "Rejected" -> {
//                        Global.warningdialogbox(context, "Your ticket will be rejected")
//                    }
//
//                }
            }


        }


//        val f = File(AllitemsList[position].file)

//        holder.name.text = AllitemsList[position].File
//
//
//        holder.itemView.setOnClickListener {
//            val extension: String = AllitemsList[position].File.substring(AllitemsList[position].File.lastIndexOf("."))
//            if(extension=="jpg"||extension=="jpeg"||extension=="png"||extension=="HEIC"){
//                val i = Intent(context, OpenPdfView::class.java)
//                i.putExtra("PDFLink", AllitemsList[position].File)
//                context.startActivity(i)
//            }else{
//                val pdf_url = Global.Image_URL + AllitemsList[position].File
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
//                context.startActivity(browserIntent)
//            }
//        }


    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: ServceTicketAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)


}
