package com.example.wds.fragments.verify

import android.content.Context
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.example.wds.R
import com.example.wds.databinding.FragmentVerifyBinding
import com.example.wds.entry.Entry
import com.example.wds.entry.EntryViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat

//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import kotlin.random.Random


@RequiresApi(Build.VERSION_CODES.O)
class VerifyFragment : Fragment(R.layout.fragment_verify), CardStackListener {
    private lateinit var binding: FragmentVerifyBinding
    private lateinit var entryViewModel: EntryViewModel
    private lateinit var myManager: CardStackLayoutManager
    private lateinit var myAdapter: CardStackAdapter
    private lateinit var topEntry: Entry
    private lateinit var functions: FirebaseFunctions
    private lateinit var storageRef: StorageReference

    private val acceptList = ArrayList<Entry>()
    private val rejectList = ArrayList<Entry>()
    private val actionList = ArrayList<Boolean>()
    private val prefs by lazy {
        requireContext().getSharedPreferences(
            prefsKey,
            Context.MODE_PRIVATE
        )
    }

    companion object {
        private const val prefsKey = "prefsKey"
        private const val cardStackKey = "cardStackKey"
        private const val format = "yyyy/MM/dd EEE HH:mm:ss"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        println("DEBUG onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVerifyBinding.bind(view)
        entryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        initialize()
        loadData()
        setupButtons()
    }

    private fun initialize() {
//        println("DEBUG initialize()")
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
        myAdapter = CardStackAdapter(emptyList())
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
        functions = Firebase.functions
        storageRef = Firebase.storage("gs://deterred/").reference
    }

    private fun setupButtons() {
//        println("DEBUG setupButtons()")
        with(binding) {
            btnAdd.setOnClickListener {
                getFiles()
            }

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
//                    println("DEBUG acceptBtn clicked")
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
//                    println("DEBUG rejectBtn clicked")
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

    override fun onPause() {
//        println("DEBUG onPause()")
        while (actionList.size > 0) {
            val entry: Entry
            if (actionList[0]) {
                entry = acceptList[0]
                moveAccepted(entry.filename)
                entryViewModel.insert(entry)
                acceptList.removeAt(0)
            } else {
                entry = rejectList[0]
                removeRejected(entry.filename)
                rejectList.removeAt(0)
            }
            actionList.removeAt(0)
        }
        saveData()
        super.onPause()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
//        println("DEBUG onCardDragging: d=${direction?.name} ratio=$ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
//        println("DEBUG onCardSwiped: d = $direction")
        if (direction == Direction.Right) {
            actionList.add(0, true)
            acceptList.add(0, topEntry)
        }
        if (direction == Direction.Left) {
            actionList.add(0, false)
            rejectList.add(0, topEntry)
        }
        bgtvVisibility()
    }

    override fun onCardRewound() {
//        println("DEBUG onCardRewound: ${myManager.topPosition}")
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
//        println("DEBUG onCardRewound: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View?, position: Int) {
//        println("DEBUG onCardAppeared: $position")
        topEntry = myAdapter.getEntries()[myManager.topPosition]
        bgtvVisibility()
    }

    override fun onCardDisappeared(view: View?, position: Int) {
//        println("DEBUG onCardDisappeared: $position")
    }

    private fun bgtvVisibility() {
//        println("DEBUG bgtvVisibility() itemCount:${myAdapter.itemCount}")
        binding.tvVerify.visibility =
            if (myAdapter.itemCount == actionList.size) View.VISIBLE else View.INVISIBLE
    }

    private fun saveData() {
        prefs.edit().apply {
            putString(
                cardStackKey, Gson().toJson(
                    myAdapter.getEntries().subList(myManager.topPosition, myAdapter.itemCount)
                )
            )
        }.apply()
    }

    private fun loadData() {
        val json = prefs.getString(cardStackKey, null)
        val type = object : TypeToken<ArrayList<Entry>>() {}.type
        if (json != null) myAdapter.setEntries(Gson().fromJson(json, type))
    }

    @SuppressLint("SimpleDateFormat")
    private fun getFiles() = CoroutineScope(Dispatchers.IO).launch {
//        println("DEBUG getFiles()")
        try {
            val entries = storageRef.child("verify/").listAll().await()
            val entryList = ArrayList<Entry>()
            for (entry in entries.items) {
                val imgSrc = entry.downloadUrl.await().toString()
                val metadata = entry.metadata.await()
                val textTime = SimpleDateFormat(format).format(metadata.creationTimeMillis)
//                val textAnimal = metadata.getCustomMetadata("animalType").toString()
                val filename = entry.name
//                val textAnimal = filename.substring(0,name.length-8)
                entryList.add(Entry(0, imgSrc, filename/*textAnimal*/, textTime))
            }
            withContext(Dispatchers.Main) {
                val old = myAdapter.getEntries()
                val callback = EntryDiffCallback(old, entryList)
                val result = DiffUtil.calculateDiff(callback)
                myAdapter.setEntries(entryList)
                result.dispatchUpdatesTo(myAdapter)
                topEntry = myAdapter.getEntries()[myManager.topPosition]
            }
        } catch (e: Exception) {
            println("DEBUG Error when fetching from Cloud Storage")
        }
    }

    private fun moveAccepted(filename: String): Task<String> {
        val data = hashMapOf("filename" to filename)
        return functions
            .getHttpsCallable("moveAccepted")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }

    private fun removeRejected(filename: String): Task<String> {
        val data = hashMapOf("filename" to filename)
        return functions
            .getHttpsCallable("removeRejected")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }
}