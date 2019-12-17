package com.egrocerx.ui.dashboard

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.egrocerx.R
import com.egrocerx.base.BaseActivity
import com.egrocerx.ui.payment.MakePaymentFragment
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.item_toolbar.*

class DashboardActivity : BaseActivity() {

    private lateinit var appBarConfig: AppBarConfiguration

    private lateinit var navHostFragment: NavHostFragment

    private var doubleBackToExitPressedOnce = false

    private val navController: NavController by lazy {
        Navigation.findNavController(
            this,
            R.id.mainNavFragment
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary1)
        }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavFragment) as NavHostFragment
        setupToolbar()
        setupBottomNavigation()

    }


    private fun setupToolbar() {
        setSupportActionBar(customToolbar)

        appBarConfig = AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.categoriesFragment, R.id.searchFragment,
            R.id.offersFragment, R.id.basketFragment
        )
            .build()


    }

    private fun setupBottomNavigation() {


        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        NavigationUI.setupWithNavController(customToolbar, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->


            if (destination.id == R.id.makePaymentFragment) {
                customToolbar.navigationIcon = null
            }

            if (destination.id == R.id.homeFragment2) {
                bottomNavigationView.visibility = View.VISIBLE
                customToolbar.title = getString(R.string.app_name)
                customToolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.ic_home)
                customToolbar.contentInsetStartWithNavigation = 0
                return@addOnDestinationChangedListener
            }

            if (
                destination.id == R.id.categoriesFragment2 ||
                destination.id == R.id.karloffDailyFragment2 ||
                destination.id == R.id.offersFragment2 ||
                destination.id == R.id.nav_graph_profile ||
                destination.id == R.id.subcategoryFragment2
            ) {
                bottomNavigationView.visibility = View.VISIBLE
            } else {
                bottomNavigationView.visibility = View.GONE
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return item.onNavDestinationSelected(navController)
                || super.onOptionsItemSelected(item)

    }

    /**
     * Check if fragment manager has back stack then pops the, on back click
     * else closes the activity
     */
    override fun onBackPressed() {

        val backStackEntryCount = navHostFragment.childFragmentManager.backStackEntryCount

        if (navHostFragment.childFragmentManager.findFragmentById(R.id.mainNavFragment) is MakePaymentFragment) {
            return
        }
        if (backStackEntryCount > 0) {
            navController.popBackStack()
            return
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}
