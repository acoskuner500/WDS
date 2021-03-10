package com.example.wds.fragments.verify

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.wds.Entry
import com.example.wds.R
import com.example.wds.databinding.FragmentVerifyBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import com.yuyakaido.android.cardstackview.*

@RequiresApi(Build.VERSION_CODES.O)
class VerifyFragment : Fragment(R.layout.fragment_verify), CardStackListener {
    private lateinit var binding: FragmentVerifyBinding

    private lateinit var myManager: CardStackLayoutManager
    private lateinit var myAdapter: CardStackAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var collection: CollectionReference

    private val acceptList = ArrayList<DocumentReference>()
    private val rejectList = ArrayList<DocumentReference>()
    private val actionList = ArrayList<Boolean>()

    companion object {
        private const val TAG = "DEBUG"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Log.d(TAG, "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVerifyBinding.bind(view)
        initialize()
        setupButtons()
    }

    private fun initialize() {
//        Log.d(TAG, "initialize()")
        db = FirebaseFirestore.getInstance()
        collection = db.collection("Verify")
        val query = collection.orderBy("textTime")
        val options = FirestoreRecyclerOptions
            .Builder<Entry>()
            .setQuery(query, Entry::class.java)
            .build()
        myAdapter = CardStackAdapter(options)
        myManager = CardStackLayoutManager(context, this)
        myManager.apply {
            setStackFrom(StackFrom.Bottom)
            setVisibleCount(5)
            setTranslationInterval(8.0f)
            setScaleInterval(0.95f)
            setSwipeThreshold(0.3f)
            setMaxDegree(20.0f)
            setDirections(Direction.HORIZONTAL)
            setCanScrollHorizontal(true)
            setCanScrollVertical(true)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
            setRewindAnimationSetting(
                RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
            )
        }
        binding.cardStackView.apply {
            setHasFixedSize(true)
            adapter = myAdapter
            layoutManager = myManager
            itemAnimator.apply {
                if (this is DefaultItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
        }
    }

    private fun setupButtons() {
//        Log.d(TAG, "setupButtons()")
        with(binding) {
            undoBtn.setOnClickListener {
                if (actionList.size > 0) {
                    myManager.setRewindAnimationSetting(
                        RewindAnimationSetting.Builder()
                            .setDirection(Direction.Bottom)
                            .setDuration(Duration.Fast.duration)
                            .setInterpolator(DecelerateInterpolator())
                            .build()
                    )
                    cardStackView.rewind()
                }
            }

            acceptBtn.setOnClickListener {
                if (myAdapter.itemCount > 0) {
//                    Log.d(TAG, "acceptBtn clicked")
                    myManager.setSwipeAnimationSetting(
                        SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Right)
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(AccelerateInterpolator())
                            .build()
                    )
                    cardStackView.swipe()
                }
            }

            rejectBtn.setOnClickListener {
                if (myAdapter.itemCount > 0) {
//                    Log.d(TAG, "rejectBtn clicked")
                    myManager.setSwipeAnimationSetting(
                        SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Left)
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(AccelerateInterpolator())
                            .build()
                    )
                    cardStackView.swipe()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myAdapter.startListening()
    }

    override fun onPause() {
//        Log.d(TAG, "onPause()")
        while (actionList.size > 0) {
            val entry: DocumentReference
            if (actionList[0]) {
                entry = acceptList[0]
                moveDocument(entry)
                acceptList.removeAt(0)
            } else {
                entry = rejectList[0]
                deleteDocument(entry)
                rejectList.removeAt(0)
            }
            actionList.removeAt(0)
        }
        myAdapter.stopListening()
        super.onPause()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
//        Log.d(TAG, "onCardDragging: d=${direction?.name} ratio=$ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
//        Log.d(TAG, "onCardSwiped: d = $direction")
        val topEntry = myAdapter.getEntry(myManager.topPosition-1)
        if (direction == Direction.Right) {
            actionList.add(0, true)
            acceptList.add(0, topEntry)
            Log.d(TAG, "onCardSwiped: Accept")
        }
        if (direction == Direction.Left) {
            actionList.add(0, false)
            rejectList.add(0, topEntry)
            Log.d(TAG, "onCardSwiped: Reject")
        }
        bgtvVisibility()
    }

    override fun onCardRewound() {
//        Log.d(TAG, "onCardRewound: ${myManager.topPosition}")
        if (actionList.size > 0) {
            if (actionList[0]) {
                acceptList.removeAt(0)
            } else {
                rejectList.removeAt(0)
            }
            actionList.removeAt(0)
        }
        bgtvVisibility()
    }

    override fun onCardCanceled() {
//        Log.d(TAG, "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d(TAG, "onCardAppeared: $position")
        bgtvVisibility()
    }

    override fun onCardDisappeared(view: View?, position: Int) {
//        Log.d(TAG, "onCardDisappeared: $position")
    }

    private fun bgtvVisibility() {
//        Log.d(TAG, "bgtvVisibility() itemCount:${myAdapter.itemCount}")
        binding.tvVerify.text =
            if (!checkConnection()) getString(R.string.msg_disconnected)
            else getString(R.string.msg_verification_done)
        binding.tvVerify.visibility =
            if (myAdapter.itemCount == actionList.size) View.VISIBLE
            else View.INVISIBLE
    }

    @Suppress("DEPRECATION")
    private fun checkConnection(): Boolean {
        val cm =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }

    private fun moveDocument(fromPath: DocumentReference) {
        fromPath.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    db.collection("Log").add(document.data!!)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                            deleteDocument(fromPath)
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                } else {
                    Log.d(TAG, "No such document")
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }
    }

    private fun deleteDocument(fromPath: DocumentReference) {
        fromPath.delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }
}