package com.egrocerx.ui.landingscreen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.egrocerx.R
import kotlinx.android.synthetic.main.fragment_landing_screen.*

class LandingScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing_screen, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navController =
            Navigation.findNavController(activity as AppCompatActivity, R.id.landingNavFragment)
        bottomNavigationView.setupWithNavController(navController)
    }


}
