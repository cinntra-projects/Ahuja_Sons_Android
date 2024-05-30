package com.ahuja.sons.globals

import com.ahuja.sons.model.TicketHistoryData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

object Common {
    val VIEWTYPE_DATA: Int = 1
    val VIEWTYPE_GROUP: Int = 0

    var alphabetical_order: MutableList<String> = ArrayList()

    /*   This method add alphabet to list*/

    fun addAlphabet(list: ArrayList<TicketHistoryData>): ArrayList<TicketHistoryData> {
        var i:Int = 0;
        val customList = ArrayList<TicketHistoryData>()
        val member = TicketHistoryData()
        member.Datetime = list[0].Datetime.toString()
        member.viewType = VIEWTYPE_GROUP
        alphabetical_order.add(list[0].Datetime.toString())

        customList.add(member)
        i = 0
        while (i < list.size - 1) {
            val salesModel = TicketHistoryData()
            val name1 = list[i].Datetime
            val name2 = list[i + 1].Datetime
            if (name1 == name2) {
                list[i].viewType = VIEWTYPE_DATA
                customList.add(list[i])
            } else {
                list[i].viewType = VIEWTYPE_DATA
                customList.add(list[i])
                salesModel.Datetime = name2.toString()
                salesModel.viewType = VIEWTYPE_GROUP
                customList.add(salesModel)

            }
            i++

        }
        list[i].viewType = VIEWTYPE_DATA
        customList.add(list[i])
        return customList
    }


    /* Sorting the list */


    fun sortList(list: ArrayList<TicketHistoryData>):ArrayList<TicketHistoryData>{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        list.sortWith(Comparator { person1, person2 ->
            LocalDate.parse(person2!!.Datetime, formatter).compareTo(
                LocalDate.parse(
                    person2.Datetime,
                    formatter
                )
            )
        })
        return list
    }

    /* This function retun index of name in list*/

    fun positionwithname(name: String, list: ArrayList<TicketHistoryData>): Int {
        for (i in list.indices) {
            if (list[i].Type == name)
                return i

        }
        return -1
    }

    /* This function generate alphabet from A to Z */

    fun genrateAlphabet(): ArrayList<String> {
        val result = ArrayList<String>()
        for (i in 65..90) {
            result.add(i.toChar().toString())
        }
        return result
    }


    /*This function add data to sales model  */

    fun addSalesModeldata(id: Int):ArrayList<TicketHistoryData>{
        val saleModelList = ArrayList<TicketHistoryData>()

        return saleModelList
    }
}