package com.egrocerx.util

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.crashlytics.android.Crashlytics
import com.egrocerx.R
import com.egrocerx.core.MyApplication
import com.egrocerx.data.AccountItemModel
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AppUtils {

    companion object {
        fun openGmail(subject: String, receiverEmail: String) {
            val emailIntent = Intent(Intent.ACTION_VIEW)

            emailIntent.data = Uri.parse("mailto:")

            emailIntent.putExtra(
                android.content.Intent.EXTRA_EMAIL,
                arrayOf(receiverEmail)
            )
            emailIntent.putExtra(
                android.content.Intent.EXTRA_SUBJECT,
                subject
            )
            try {
                startActivity(MyApplication.instance.getContext(), emailIntent, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun openDialer(phone: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            startActivity(MyApplication.instance.getContext(), intent, null)
        }

        @JvmStatic
        fun shortToast(msg: String?) {
            Toast.makeText(MyApplication.instance.getContext(), msg, Toast.LENGTH_SHORT).show()
        }

        @SuppressLint("HardwareIds")
        fun getDeviceId(): String {
            return Settings.Secure.getString(
                MyApplication.instance.getContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

        fun showException(ex: Exception? = null) {

            if (ex != null) {
                Crashlytics.logException(ex)
            }
            Toast.makeText(
                MyApplication.instance.getContext(),
                "Something went wrong. Please try again!",
                Toast.LENGTH_SHORT
            ).show()
        }


        fun getCurrentDate(): String {
            return SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        }

        fun getCurrentTime(): String {
            return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        }

        fun getFullImageUrl(image: String?): String {
            return "http://karloffpizza.com/karloffgo/$image"
        }

        fun getAccountItemList(): List<AccountItemModel> {

            val list = ArrayList<AccountItemModel>()
            list.add(
                AccountItemModel(
                    1,
                    "My Profile",
                    R.id.action_myAccountFragment2_to_myProfileFragment2,
                    R.drawable.ic_boy
                )
            )
            list.add(
                AccountItemModel(
                    2,
                    "My Orders",
                    R.id.action_myAccountFragment2_to_myOrdersFragment2,
                    R.drawable.ic_package
                )
            )
            list.add(
                AccountItemModel(
                    10,
                    "Subscriptions",
                    R.id.action_myAccountFragment2_to_subscriptionListFragment,
                    R.drawable.ic_go_daily
                )
            )
            list.add(
                AccountItemModel(
                    2,
                    "KarlPay Wallet",
                    R.id.action_myAccountFragment2_to_walletFragment2,
                    android.R.drawable.ic_menu_call
                )
            )
            list.add(
                AccountItemModel(
                    3,
                    "Membership",
                    R.id.action_myAccountFragment2_to_membershipFragment2,
                    R.drawable.ic_membership
                )
            )
            list.add(
                AccountItemModel(
                    4,
                    "My Addresses",
                    R.id.action_global_myAddressesFragment2,
                    R.drawable.ic_map
                )
            )
            list.add(
                AccountItemModel(
                    5,
                    "Notifications",
                    R.id.action_global_notificationsFragment3,
                    R.drawable.ic_notification
                )
            )
            list.add(
                AccountItemModel(
                    6,
                    "Change Password",
                    R.id.action_myAccountFragment2_to_changePasswordFragment2,
                    R.drawable.ic_locked
                )
            )
//            list.add(
//                AccountItemModel(
//                    7,
//                    "About",
//                    R.id.action_myAccountFragment2_to_aboutFragment,
//                    R.drawable.ic_info
//                )
//            )
//            list.add(
//                AccountItemModel(
//                    8,
//                    "Settings",
//                    R.id.action_myAccountFragment2_to_settingFragment,
//                    R.drawable.ic_settings
//                )
//            )
            list.add(AccountItemModel(9, "Logout", null, R.drawable.ic_logout))


            return list

        }

        private fun stringToDate(inputTime: String): Date {
            return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(inputTime)
        }

        fun stringDateToMiliseconds(inputTime: String): Long {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(inputTime).time
        }

        fun get12HourTime(inputTimeInString: String?): String {
            if (inputTimeInString == null)
                return ""
            return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                stringToDate(
                    inputTimeInString
                )
            )
        }

        fun getAmountWithCurrency(cost: String?): String {
            return "${MyApplication.instance.getString(R.string.currency_symbol)} ${cost?.toBigDecimal()
                /*   .round(MathContext(2))*/
                ?.setScale(2, RoundingMode.UP)}"
        }

        fun getAmountWithCurrency(cost: Double): String {
            return "${MyApplication.instance.getString(R.string.currency_symbol)} ${cost.toBigDecimal()
                /*.round(MathContext(2)*/
                .setScale(
                    2, RoundingMode.UP
                )}"
        }

        fun combinedPrice(productMrp: Float, productOfferPrice: Float): String {
            return getStrikeOffText(productMrp.toString()).toString() + " " + getAmountWithCurrency(
                productOfferPrice.toString()
            )
        }

        private fun getStrikeOffText(input: String): Spanned? {
            val str = "<html><strike>${getAmountWithCurrency(input)}</strike></html>"
            return Html.fromHtml(str)
        }

        fun getOrderStatus(input: String): String {
            return when (input) {
                "0" -> "FAILED"
                "1" -> "NEW"
                "2" -> "VIEWED"
                "3" -> "PROCESSING"
                "4" -> "DISPATCHED"
                "5" -> "DELIVERED"
                else -> "CANCELLED"

            }
        }

        fun getFutureDate(days: Int): String {
            val cal = Calendar.getInstance()
            cal.time = Date(System.currentTimeMillis())
            cal.add(Calendar.DATE, days)
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        }
    }
}