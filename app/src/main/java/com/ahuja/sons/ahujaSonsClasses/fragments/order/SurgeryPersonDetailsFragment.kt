package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahuja.sons.databinding.FragmentSurgeryPersonDeatilsBinding

class SurgeryPersonDetailsFragment : Fragment() {


    lateinit var binding : FragmentSurgeryPersonDeatilsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentSurgeryPersonDeatilsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo order arrow-

        binding.orderUpArrow.setOnClickListener {
            binding.orderDetailsLayout.visibility = View.GONE
            binding.orderUpArrow.visibility = View.GONE
            binding.orderDownArrow.visibility = View.VISIBLE
        }

        binding.orderDownArrow.setOnClickListener {
            binding.orderDetailsLayout.visibility = View.VISIBLE
            binding.orderDownArrow.visibility = View.GONE
            binding.orderUpArrow.visibility = View.VISIBLE
        }

        //todo dependency arrow-

        binding.dependencyUpArrow.setOnClickListener {
            binding.dependencyLayout.visibility = View.GONE
            binding.dependencyUpArrow.visibility = View.GONE
            binding.dependencyDownArrow.visibility = View.VISIBLE
        }

        binding.dependencyDownArrow.setOnClickListener {
            binding.dependencyLayout.visibility = View.VISIBLE
            binding.dependencyDownArrow.visibility = View.GONE
            binding.dependencyUpArrow.visibility = View.VISIBLE
        }


        binding.startSurgeryChip.setOnClickListener {
            binding.startAndRescheduleLayout.visibility = View.GONE
            binding.startSurgeryCardViewLayout.visibility = View.VISIBLE
            binding.endSurgeryBtnLayout.visibility = View.VISIBLE
        }

        binding.endTripChip.setOnClickListener {
            binding.chipCardViewBtton.visibility = View.GONE
            binding.endSurgeryTimeViewLayout.visibility = View.VISIBLE
            binding.startSurgeryCardViewLayout.visibility = View.VISIBLE

        }


    }


}