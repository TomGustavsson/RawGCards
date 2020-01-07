package com.tomgu.rawgcards.main.account

class Account(val email: String? = null, val photo: String? = null, val name: String? = null, val uid: String? = null) {

    var friends: MutableList<Account> = mutableListOf()
}