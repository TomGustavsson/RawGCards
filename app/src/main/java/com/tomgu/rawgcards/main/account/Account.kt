package com.tomgu.rawgcards.main.account

data class Account(val email: String? = null, val photo: String? = null, val name: String? = null, val uid: String? = null) {

    var friends: MutableList<String>? = mutableListOf()

    var friendRequests: MutableList<String>? = mutableListOf()
}