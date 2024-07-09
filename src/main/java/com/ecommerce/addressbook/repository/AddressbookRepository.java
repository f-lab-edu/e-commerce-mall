package com.ecommerce.addressbook.repository;

import com.ecommerce.addressbook.entity.Addressbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressbookRepository extends JpaRepository<Addressbook, Long> {

  Addressbook findByMemberIdAndDefaultValue(Long memberId, Integer defaultValue);

}
