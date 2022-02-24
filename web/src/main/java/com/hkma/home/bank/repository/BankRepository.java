package com.hkma.home.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.bank.entity.BankEntity;

public interface BankRepository extends JpaRepository<BankEntity,Long> {
}
