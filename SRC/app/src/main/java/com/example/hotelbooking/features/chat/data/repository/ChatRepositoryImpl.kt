package com.example.hotelbooking.features.chat.data.repository

import android.util.Log
import com.example.hotelbooking.features.chat.data.dto.ChatDto
import com.example.hotelbooking.features.chat.data.dto.ChatMessageDto
import com.example.hotelbooking.features.chat.data.mapper.toDomain
import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val db: FirebaseFirestore
) : ChatRepository {

    override suspend fun getExistingChat(userId: String, hotelId: String): Chat? {
        val result = db.collection("chats")
            .whereEqualTo("userId", userId)
            .whereEqualTo("hotelId", hotelId)
            .get()
            .await()

        return result.documents.firstOrNull()?.toObject(ChatDto::class.java)?.toDomain()
    }

    override suspend fun createChat(
        userId: String,
        hotelId: String,
        hotelName: String,
        shortAddress: String,
        firstMessage: String
    ): Chat {
        val chatId = "${userId}_${hotelId}"
        val timestamp = System.currentTimeMillis()

        val chatDto = ChatDto(
            chatId = chatId,
            userId = userId,
            hotelId = hotelId,
            hotelName = hotelName,
            shortAddress = shortAddress,
            lastMessage = firstMessage,
            lastTimestamp = timestamp,
            createdAt = timestamp
        )

        db.collection("chats")
            .document(chatId)
            .set(chatDto)
            .await()

        return chatDto.toDomain()
    }

    override suspend fun sendMessage(chatId: String, senderId: String, content: String) {
        val timestamp = System.currentTimeMillis()

        val msg = ChatMessageDto(
            messageId = "",
            senderId = senderId,
            content = content,
            timestamp = timestamp
        )

        val ref = db.collection("chats").document(chatId)

        // Gửi message
        ref.collection("messages")
            .add(msg)
            .await()

        // Update lastMessage
        ref.update(
            mapOf(
                "lastMessage" to content,
                "lastTimestamp" to timestamp,
                "lastSenderId" to senderId
            )
        ).await()
    }

    override fun listenMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(ChatMessageDto::class.java)?.copy(
                        messageId = it.id
                    )?.toDomain()
                } ?: emptyList()

                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun listenUserChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = db.collection("chats")
            .whereEqualTo("userId", userId)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                try {
                    if (error != null) {
                        Log.e("CHAT_REPO", "Firestore listener error", error)
                        // Send nothing or keep previous state? gửi empty to be explicit
                        trySendBlocking(emptyList())
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        Log.w("CHAT_REPO", "Snapshot is null")
                        trySendBlocking(emptyList())
                        return@addSnapshotListener
                    }

                    Log.d("CHAT_REPO", "Snapshot docs count = ${snapshot.documents.size}")
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            // debug raw fields
                            Log.d("CHAT_REPO", "Doc id=${doc.id} data=${doc.data}")
                            doc.toObject(ChatDto::class.java)?.toDomain()
                        } catch (e: Exception) {
                            Log.e("CHAT_REPO", "Failed mapping doc ${doc.id}", e)
                            null
                        }
                    }

                    Log.d("CHAT_REPO", "Mapped chats size = ${list.size}")
                    trySendBlocking(list)
                } catch (t: Throwable) {
                    Log.e("CHAT_REPO", "Unhandled error in snapshot listener", t)
                    // avoid crashing callbackFlow; emit empty
                    trySendBlocking(emptyList())
                }
            }

        awaitClose {
            Log.d("CHAT_REPO", "Listener removed")
            listener.remove()
        }
    }

    override fun listenHotelChats(hotelId: String): Flow<List<Chat>> = callbackFlow {

        val listener = db.collection("chats")
            .whereEqualTo("hotelId", hotelId)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(ChatDto::class.java)?.toDomain() }
                    ?: emptyList()

                trySend(list)
            }

        awaitClose {
            listener.remove()
        }
    }
}