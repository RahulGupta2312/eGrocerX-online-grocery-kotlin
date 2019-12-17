package com.egrocerx.network;

/**
 * @author rahul
 */

public enum ApiMode {
    LOGIN("login"),
    REGISTER("register"),
    FORGOT_PASSWORD(""),
    HOME("home"),
    NOTIFICATIONS("notifications"),
    CATEGORIES("categories"),
    SUBCATEGORIES("subcategories"),
    SEARCH("search"),
    OFFERS("offers"),
    CART("getCart"),
    USER_PROFILE("getUserProfile"),
    ALL_ADDRESSES("myAddresses"),
    ADD_ADDRESS("addAddress"),
    DELETE_ADDRESS("deleteAddress"),
    ADD_TO_CART("addToCart"),
    WALLET_HISTORY("walletHistory"),
    REMOVE_FROM_CART("removeFromCart"),
    CHANGE_PASSWORD("changePassword"),
    PRODUCT_DETAIL("productDetail"),
    DAILY_PRODUCTS("subscribableProducts"),
    SUBSCRIPTION_FREQUENCY("subscriptionFrequency"),
    SAVE_SUBSCRIPTION("saveSubscription"),
    SUBSCRIPTIONS("subscriptions"),
    SEND_MONEY("sendMoney"),
    APPLY_COUPON("applyCoupon"),
    AVAILABLE_SLOTS("availableSlots"),
    INVOICE_BREAKUP("invoiceBreakup"),
    PLACE_ORDER("placeOrder"),
    UPDATE_ORDER("updateOrder"),
    ORDER_HISTORY("myOrders"),
    ORDER_DETAIL("orderDetail"),
    CANCEL_ORDER("cancelOrder"),
    UPDATE_PROFILE("updateProfile"),
    REWARD_POINTS("rewardPoints"),
    CONVERT_REWARD_POINTS("convertRewardPoints"),
    UPGRADE_MEMBERSHIP("upgradeMembership"),
    MEMBERSHIPS("memberships"),
    UPDATE_MOBILE_VERIFIED("updateMobileVerified"),
    SAVE_TRANSACTION("saveTransaction"),
    RECHARGE_WALLET("rechargeKarlpay"),
    VERIFY_USER_BY_MOBILE("verifyUserByMobile"),
    RESET_PASSWORD("resetPassword"),
    SEND_MSG("sendMessage"),
    NOTIFY_ME("notifyMe"),
    UPDATE_SUBSCRIPTION("updateSubscription"),
    DELETE_SUBSCRIPTION("deleteSubscription"),
    LOGOUT("logout"),
    PRODUCTS("products");


    private String action;

    ApiMode(String name) {
        this.action = name;
    }

    public String getName() {
        return this.action;
    }

}
