package com.ahuja.sons.ahujaSonsClasses.demo

data class ParentItemModel(
    var name: String,
    var isSelected: Boolean,
    var childItemList: MutableList<ChildItem>

) {
    data class ChildItem(
        var name: String,
        var isSelected: Boolean,
        var id : String
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParentItemModel) return false

        return name == other.name &&
                isSelected == other.isSelected &&
                childItemList == other.childItemList
    }


    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + childItemList.hashCode()
        return result
    }


}

