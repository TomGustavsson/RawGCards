package com.tomgu.rawgcards.account.models

import java.io.Serializable

data class Account(val email: String? = null, val photo: String? = null, val name: String? = null, val uid: String? = null): Serializable {

    var friends: MutableList<String>? = mutableListOf()

    var friendRequests: MutableList<FriendRequest>? = mutableListOf()
}