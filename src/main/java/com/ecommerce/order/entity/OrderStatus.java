package com.ecommerce.order.entity;

public enum OrderStatus {
  PENDING("Order is pending"),    // 주문 대기 중
  PROCESSING("Order is being processed"), // 주문 처리 중
  SHIPPED("Order has been shipped"),    // 주문 배송 중
  DELIVERED("Order has been delivered"),  // 주문 배송 완료
  CANCELED("Order has been canceled");   // 주문 취소

  private final String message;

  OrderStatus(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return this.message;
  }
}
