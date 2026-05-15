package com.example.jatre_namma_pride.data.repository

import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.model.ItemType
import com.example.jatre_namma_pride.data.model.LostFoundItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Provides Lost & Found data from Firebase Firestore.
 * All reads are real-time; writes go directly to Firestore.
 */
object LostFoundRepository {

    private val db  = FirebaseFirestore.getInstance()
    private val col = db.collection("lostFound")

    // ── Seed on first launch ─────────────────────────────────────────────────

    suspend fun seedIfNeeded() {
        // The user requested to remove all data in Lost & Found.
        // This will clear the entire Firestore collection.
        val snapshot = col.get().await()
        if (!snapshot.isEmpty) {
            val batch = db.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
    }

    // ── Real-time Flow ───────────────────────────────────────────────────────

    fun getAllItems(): Flow<List<LostFoundItem>> = callbackFlow {
        val listener = col
            .orderBy("id")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) { trySend(emptyList()); return@addSnapshotListener }
                val items = snapshot.documents.mapNotNull { doc ->
                    runCatching { LostFoundItem.fromMap(doc.data ?: emptyMap()) }.getOrNull()
                }.map { item ->
                    // Fix: R.drawable IDs change across builds. Never trust the int from Firestore.
                    val localSeed = sampleItems.find { it.id == item.id }
                    if (localSeed != null) {
                        item.copy(imageResId = localSeed.imageResId)
                    } else {
                        // Force 0 for non-seeded items. They should never rely on int resource IDs.
                        item.copy(imageResId = 0)
                    }
                }
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ── Write operations ─────────────────────────────────────────────────────

    suspend fun insertItem(item: LostFoundItem) {
        val docRef = if (item.id == 0) col.document() else col.document(item.id.toString())
        docRef.set(item.toMap()).await()
    }

    suspend fun updateItem(item: LostFoundItem) {
        col.document(item.id.toString()).set(item.toMap()).await()
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    fun getActiveCount(items: List<LostFoundItem>): Int  = items.count { !it.isResolved }
    fun getResolvedCount(items: List<LostFoundItem>): Int = items.count { it.isResolved }
    fun getLostCount(items: List<LostFoundItem>): Int     = items.count { it.type == ItemType.LOST }
    fun getFoundCount(items: List<LostFoundItem>): Int    = items.count { it.type == ItemType.FOUND }

    // ── Sample seed data ─────────────────────────────────────────────────────

    private val sampleItems = emptyList<LostFoundItem>()
}
