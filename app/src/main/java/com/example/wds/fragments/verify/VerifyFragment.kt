package com.example.wds.fragments.verify

import android.content.Context
import android.content.SharedPreferences
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
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuyakaido.android.cardstackview.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import kotlinx.coroutines.withContext
//import java.lang.Exception
//import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


@RequiresApi(Build.VERSION_CODES.O)
class VerifyFragment : Fragment(R.layout.fragment_verify), CardStackListener {
    private lateinit var binding: FragmentVerifyBinding
    private lateinit var entryViewModel: EntryViewModel
    private lateinit var myManager: CardStackLayoutManager
    private lateinit var myAdapter: CardStackAdapter
    private lateinit var topEntry: Entry
    private val acceptList = ArrayList<Entry>()
    private val rejectList = ArrayList<Entry>()
    private val actionList = ArrayList<Boolean>()
//    private val storageRef = Firebase.storage("gs://403_photos/").reference

    companion object {
        private const val prefsKey = "prefsKey"
        private const val cardStackKey = "cardStackKey"

        //        private const val baseURL = "https://storage.googleapis.com/genuine-cirrus-294302.appspot.com/"
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
//        getFiles()
    }

    override fun onPause() {
//        println("DEBUG onPause()")
        while (acceptList.size > 0) {
            entryViewModel.insert(acceptList[0])
            acceptList.removeAt(0)
        }
        acceptList.clear()
        rejectList.clear()
        actionList.clear()
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
    }

    private fun setupButtons() {
//        println("DEBUG setupButtons()")
        with(binding) {
            btnAdd.setOnClickListener {
                val old = myAdapter.getEntries()
                val new = old.plus(get10())
                val callback = EntryDiffCallback(old, new)
                val result = DiffUtil.calculateDiff(callback)
                myAdapter.setEntries(new)
                result.dispatchUpdatesTo(myAdapter)
                topEntry = myAdapter.getEntries()[myManager.topPosition]
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

    @SuppressLint("SimpleDateFormat")
    private fun get10(): List<Entry> {
//        println("DEBUG addNewEntry()")
        val entries = ArrayList<Entry>()
        repeat(10) {
            val textTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
            val num = Random.nextInt(1084)
            val imgSrc = "https://picsum.photos/400/300/?image=$num"
            val textAnimal = "Pic $num"
            entries.add(Entry(0, imgSrc, textAnimal, textTime))
        }
        return entries
    }

    private fun bgtvVisibility() {
//        println("DEBUG bgtvVisibility() itemCount:${myAdapter.itemCount}")
        binding.tvVerify.visibility =
            if (myAdapter.itemCount == actionList.size) View.VISIBLE else View.INVISIBLE
    }

    private fun saveData() {
        prefs().edit().apply {
            putString(
                cardStackKey, Gson().toJson(
                    myAdapter.getEntries().subList(myManager.topPosition, myAdapter.itemCount)
                )
            )
        }.apply()
    }

    private fun loadData() {
        val json = prefs().getString(cardStackKey, null)
        val type = object : TypeToken<ArrayList<Entry>>() {}.type
        if (json != null) myAdapter.setEntries(Gson().fromJson(json, type))
    }

    private fun prefs(): SharedPreferences {
        return requireContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
    }

//    @SuppressLint("SimpleDateFormat")
//    private fun getFiles() = CoroutineScope(Dispatchers.IO).launch {
//        println("DEBUG getFiles()")
//        try {
//            val entries = storageRef/*child("verify/").*/.listAll().await()
//            val entryList = ArrayList<Entry>()
//            for (entry in entries.items) {
//                val imgSrc = entry.downloadUrl.await().toString()
//                val metadata = entry.metadata.await()
//                val textTime = SimpleDateFormat(format).format(metadata.creationTimeMillis)
////                val textAnimal = metadata.getCustomMetadata("animalType").toString()
//                val name = entry.name
//                val textAnimal = name.substring(name.length-4)
//                entryList.add(Entry(0,imgSrc,textAnimal, textTime))
////                withContext(Dispatchers.Main) {
////                    cardStackList.add(Entry(0,imgSrc,textAnimal, textTime))
////                }
//            }
//            withContext(Dispatchers.Main) {
//                cardStackList.clear()
//                cardStackList.addAll(entryList)
//            }
//        } catch(e: Exception) {
//            println("DEBUG Error when fetching from Cloud Storage")
//        }
//    }
}