package com.sarftec.messiwallpapers.data.firebase.extra

import com.google.firebase.firestore.DocumentSnapshot

sealed class FirebaseKey(val direction: Direction) {
    class ID(direction: Direction, val id: Long = INITIAL_ID) : FirebaseKey(direction) {
        companion object {
            const val INITIAL_ID = -1L
        }
    }
   class Snapshot(direction: Direction, val ref: DocumentSnapshot) : FirebaseKey(direction)

    enum class Direction { NEXT, PREVIOUS }

    companion object {
        fun getInitialKey() : ID = ID(Direction.NEXT)

        fun isDefaultKey(key: FirebaseKey) : Boolean {
            if(key !is ID) return false
            return key.id == ID.INITIAL_ID
        }
    }
}