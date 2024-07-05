package com.ahuja.sons.ahujaSonsClasses.fragments.surgeryperson

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.FragmentDetailSurgeryPersonBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DetailSurgeryPersonFragment : Fragment() {
    lateinit var binding: FragmentDetailSurgeryPersonBinding
    var chipGroupStartScreen: LinearLayout? = null
    var chipGroupEndScreen: LinearLayout? = null
    var btnStartTime: Chip? = null
    var btnEndTime: Chip? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailSurgeryPersonBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "DetailSurgeryPersonFrag"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipGroupStartScreen = requireActivity().findViewById(R.id.counterBtnLayoutSurgery)
        chipGroupEndScreen = requireActivity().findViewById(R.id.deliveryBtnLayoutSurgery)
        btnStartTime = requireActivity().findViewById(R.id.chipStart)
        btnEndTime = requireActivity().findViewById(R.id.endTripChipSurgery)

        binding.apply {


            orderUpArrow.setOnClickListener {
                if (orderDetailsLayout.visibility == View.VISIBLE) {
                    orderDetailsLayout.visibility = View.GONE
                    orderUpArrow.setImageResource(R.drawable.down_arrow_icon)
                } else {
                    orderDetailsLayout.visibility = View.VISIBLE
                    orderUpArrow.setImageResource(R.drawable.arrow_up_icon)
                }
            }


            dependencyUpArrow.setOnClickListener {
                if (dependencyLayout.visibility == View.VISIBLE) {
                    dependencyLayout.visibility = View.GONE
                    dependencyUpArrow.setImageResource(R.drawable.down_arrow_icon)
                } else {
                    dependencyLayout.visibility = View.VISIBLE
                    dependencyUpArrow.setImageResource(R.drawable.arrow_up_icon)
                }
            }


        }


        btnStartTime!!.setOnClickListener {
            startTimer()
        }

        btnEndTime!!.setOnClickListener {
            stopTimer()
        }


    }


    private var isRunning = false
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    private fun startTimer() {
        isRunning = true
        //   binding.btnTrip.text = "End trip"
        binding.apply {
            tvCountText.visibility = View.VISIBLE
            cardSurgeryTime.visibility = View.VISIBLE
        }
        chipGroupStartScreen!!.visibility = View.GONE
        chipGroupEndScreen!!.visibility = View.VISIBLE
        handler.post(timerRunnable)

    }

    private fun stopTimer() {
        isRunning = false
        //  binding.btnTrip.text = "Start trip"
        binding.apply {
            tvCountText.visibility = View.GONE
            cardSurgeryTime.visibility = View.VISIBLE
            tvSurgeryTimeCalculated.visibility = View.VISIBLE
        }
        chipGroupStartScreen!!.visibility = View.GONE
        chipGroupEndScreen!!.visibility = View.GONE

        handler.removeCallbacks(timerRunnable)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime += 1000
                val hours = (elapsedTime / (1000 * 60 * 60)).toInt()
                val minutes = ((elapsedTime / (1000 * 60)) % 60).toInt()
                val seconds = ((elapsedTime / 1000) % 60).toInt()
                binding.tvCountText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }
}