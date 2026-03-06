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
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val db: FirebaseFirestore
) : ChatRepository {

    private val cachedChats = mutableMapOf<String, List<Chat>>()
    private val chatsCollection = db.collection("chats")

    override suspend fun getExistingChat(userId: String, hotelId: String): Chat? {
        val snapshot = chatsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("hotelId", hotelId)
            .limit(1)
            .get()
            .await()

        if (snapshot.isEmpty) return null

        return snapshot.documents.first()
            .toObject(ChatDto::class.java)
            ?.toDomain()
    }

    override suspend fun createChat(
        userId: String,
        hotelId: String,
        adminId: String,
        firstMessage: String
    ): Chat {
        val batch = db.batch()

        val chatRef = chatsCollection.document()
        val chatId = chatRef.id
        val currentTime = System.currentTimeMillis()

        val chatDto = ChatDto(
            chatId = chatId,
            userId = userId,
            hotelId = hotelId,
            adminId = adminId,
            lastMessage = firstMessage,
            lastTimestamp = currentTime,
            lastSenderId = userId,
            createdAt = currentTime
        )
        batch.set(chatRef, chatDto)

        val messageRef = chatRef.collection("messages").document()
        val messageDto = ChatMessageDto(
            messageId = messageRef.id,
            senderId = userId,
            content = firstMessage,
            timestamp = currentTime
        )
        batch.set(messageRef, messageDto)

        batch.commit().await()

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
        cachedChats[userId]?.let { trySendBlocking(it) }

        val listener = db.collection("chats")
            .whereEqualTo("userId", userId)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                try {
                    if (error != null) {
                        Log.e("CHAT_REPO", "Firestore listener error", error)
                        trySendBlocking(emptyList())
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        trySendBlocking(emptyList())
                        return@addSnapshotListener
                    }

                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatDto::class.java)?.toDomain()
                    }

                    cachedChats[userId] = list

                    trySendBlocking(list)

                } catch (t: Throwable) {
                    Log.e("CHAT_REPO", "Unhandled error", t)
                    trySendBlocking(emptyList())
                }
            }

        awaitClose { listener.remove() }
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

    override fun listenAdminChats(adminId: String): Flow<List<Chat>> {
        return chatsCollection
            .whereEqualTo("adminId", adminId)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(ChatDto::class.java).map { it.toDomain() }
            }
    }
}