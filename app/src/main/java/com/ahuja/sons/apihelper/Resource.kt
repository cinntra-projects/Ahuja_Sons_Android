package com.ahuja.sons.apihelper

/*it is a kind of abstract class but
we can define which class can inherit from resource class
 */
/*
this class can be used nay where in future project for
handling responses of api
 */
sealed class Resource<T>(
    //in sealed class we can make n number of instance of its subclass
    //but cannot instantiate Parent sealed class
    val data: T? = null, //success data
    val message: String? = null //response message mainly error
) {
    /*
    only these classes are allowed to inherit from Resource.kt class
     */
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()


}