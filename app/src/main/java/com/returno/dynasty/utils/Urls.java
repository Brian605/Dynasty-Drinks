package com.returno.dynasty.utils;

public interface Urls {
    String BASE_URL="https://themarket.co.ke/dynasty/api/";
    String OFFERS_URL=BASE_URL+"get_offers.php";
    String OFFERS_PREVIEW_URL=BASE_URL+"get_offers_preview.php";
    String DRINKS_CATEGORY_URL = BASE_URL + "get_drinks_by_category.php";
    String DRINKS_SEARCH_URL =BASE_URL+"get_drinks_by_search.php";
    String ADD_OFFERS_URL = BASE_URL + "add_offer_item.php";
    String ADD_DRINKS_URL = BASE_URL + "add_drinks.php";
    String DELETE_URL = BASE_URL + "delete_item.php";
    String DRINKS_URL = BASE_URL + "get_all_drinks.php";
    String GET_MESSAGES_URL = BASE_URL + "get_all_messages.php";
    String SEND_MESSAGE_URL = BASE_URL + "send_message.php";
    String NOTIFICATION_URL ="https://fcm.googleapis.com/cm/send";
    String API_KEY_URL =BASE_URL+"get_api_key.php" ;
    String DELETE_MESSAGE_URL =BASE_URL+ "delete_message.php";
    String GET_ANALYTICS_URL =BASE_URL+ "get_analytics.php";
    String ADD_USER_URL = BASE_URL + "add_user.php";
    String GET_API_KEY_URL = BASE_URL + "get_sms_key.php";

    String SMS_URL = "https://sms.movesms.co.ke/api/compose";
    String GET_USER_DETAILS = BASE_URL + "get_user_details.php";
    String GET_BALANCE_URL = BASE_URL + "get_balance.php";
    String GET_ORDERS_URL = BASE_URL + "get_orders.php";
    String EDIT_DETAILS_URL = BASE_URL +"edit_details.php";
    String M_PESA_FALL_BACK_URL=BASE_URL +"api_fall_back.php";

    String SAVE_ORDER_URL = BASE_URL+"save_order.php";
    String EDIT_ORDERS_STATUS_URL = BASE_URL +"edit_order_status.php";
    String ADD_ANALYTICS_URL = BASE_URL + "add_analytics.php";
    String GET_USER_CASHBACK_URL = BASE_URL + "get_user_cashbacks.php";
    String REQUEST_CASHBACK_URL = BASE_URL + "add_cashback_request.php";
    String GET_ALL_CASHBACK_URL = BASE_URL + "get_requested_cashbacks.php";
    String DELETE_CASHBACK_URL = BASE_URL + "delete_request.php";
}
