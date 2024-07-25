package com.ecommerce.addressbook.repository;

import com.ecommerce.addressbook.entity.Addressbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressbookRepository extends JpaRepository<Addressbook, Long> {

  @Modifying
  @Query("update Addressbook a set a.isDefault = 0 where a.memberId = :memberId")
  void resetIsDefault(Long memberId);

  Addressbook findByMemberIdAndIsDefault(Long memberId, Integer isDefault);

}
