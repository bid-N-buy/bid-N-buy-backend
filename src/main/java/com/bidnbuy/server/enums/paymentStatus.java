package com.bidnbuy.server.enums;

public class paymentStatus {

    public enum PaymentMethod {
        VIRTUAL_ACCOUNT, SIMPLE_PAY, GAME_GIFT, TRANSFER, BOOK_GIFT, CULTURE_GIFT, CARD, MOBILE
    }

    public enum PaymentStatus {
        SUCCESS, FAIL, CANCEL, REFUND, PENDING
    }

}
