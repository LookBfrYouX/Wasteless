package com.navbara_pigeons.wasteless.dao;


import com.navbara_pigeons.wasteless.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionDao extends CrudRepository<Transaction, Long> {

}
