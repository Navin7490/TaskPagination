package com.example.taskpagination.util

import android.os.AsyncTask
import java.net.InetSocketAddress
import java.net.InterfaceAddress
import java.net.Socket
import java.util.function.Consumer

class InternetCheck(private val consumer:Consumer): AsyncTask<Void,Void,Boolean>() {

    interface Consumer{
        fun accepted(isConnected:Boolean?)
    }


    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        consumer.accepted(result)
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        try {
            val socket=Socket()
            socket.connect(InetSocketAddress("google.com",80),1500)
            socket.close()
            return true
        }catch (e:Exception){
            return false

        }
    }

}