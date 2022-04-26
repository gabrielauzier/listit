package com.example.listit.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.listit.R
import com.example.listit.databinding.FragmentHomeBinding
import com.example.listit.ui.adapter.ViewPagerAdapter
import com.example.listit.ui.tabs.DoingFragment
import com.example.listit.ui.tabs.DoneFragment
import com.example.listit.ui.tabs.TodoFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        configTabLayout()

        initClicks()
    }

    private fun initClicks() {
        binding.imgBtnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_authentication)
    }

    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // add fragments
        adapter.addFragment(TodoFragment(), "To-do")
        adapter.addFragment(DoingFragment(), "Doing")
        adapter.addFragment(DoneFragment(), "Done")

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        // i don't know what this does
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}