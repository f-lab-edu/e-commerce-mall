package com.ecommerce.order.entity;

public enum Status {
  PENDING,    // 주문 대기 중
  PROCESSING, // 주문 처리 중
  SHIPPED,    // 주문 배송 중
  DELIVERED,  // 주문 배송 완료
  CANCELED;   // 주문 취소

  @Override
  public String toString() {
    switch (this) {
      case PENDING:
        return "Order is pending";
      case PROCESSING:
        return "Order is being processed";
      case SHIPPED:
        return "Order has been shipped";
      case DELIVERED:
        return "Order has been delivered";
      case CANCELED:
        return "Order has been canceled";
      default:
        return "Unknown status";
    }
  }
}
