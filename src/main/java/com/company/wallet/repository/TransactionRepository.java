package com.company.wallet.repository;

import com.company.wallet.entities.Transaction;
import com.company.wallet.entities.Wallet;
import com.company.wallet.exceptions.WalletException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Transaction JPA repository
 *  <p> Generates SQL queries to access the database to manage Transaction entities</p>
 * @author EhsanASaDev
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByWallet(Wallet wallet);
    Transaction findByGlobalId(String globalId);
}
