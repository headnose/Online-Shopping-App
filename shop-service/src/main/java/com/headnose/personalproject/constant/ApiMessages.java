package com.headnose.personalproject.constant;

public final class ApiMessages {

    private ApiMessages() {
    }

    // Success
    public static final String PRODUCT_CREATED = "Product created successfully.";
    public static final String PRODUCT_UPDATED = "Product updated successfully.";
    public static final String ORDER_PLACED = "Order placed successfully.";
    public static final String ORDER_CANCELED = "Order canceled.";
    public static final String ORDER_COMPLETED = "Order completed.";
    public static final String PRODUCT_ADDED_TO_WATCHLIST = "Product added to watchlist.";
    public static final String PRODUCT_REMOVED_FROM_WATCHLIST = "Product removed from watchlist.";
    public static final String USER_REGISTERED = "User registered successfully.";

    //  Watchlist view
    public static final String WATCHLIST_EMPTY = "Your watchlist is empty.";
    public static final String WATCHLIST_ITEMS_COUNT = "Watchlist has %d item(s).";

    //  Errors
    public static final String INVALID_CREDENTIALS = "Incorrect credentials, please try again.";
    public static final String PRODUCT_NOT_FOUND = "Product not found.";
    public static final String PRODUCT_NOT_AVAILABLE = "Product not available.";
    public static final String PRODUCT_OUT_OF_STOCK_WATCHLIST =
            "Product is out of stock and cannot be added to watchlist.";
    public static final String NOT_ENOUGH_INVENTORY_FORMAT = "Not enough inventory for product ID: %s";
    public static final String ORDER_NOT_FOUND = "Order not found.";
    public static final String ORDER_ONLY_PROCESSING_CAN_CANCEL = "Only PROCESSING orders can be canceled.";
    public static final String ORDER_ONLY_PROCESSING_CAN_COMPLETE = "Only PROCESSING orders can be completed.";
    public static final String ORDER_ACCESS_DENIED = "Access denied: not your order.";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists.";
    public static final String USER_NOT_FOUND = "User not found.";
}
