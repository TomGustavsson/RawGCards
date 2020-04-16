package com.tomgu.rawgcards.account.models

data class FriendRequest(val id: String? = null, val state: State? = null)

enum class State{
    Pending,
    Asked
}