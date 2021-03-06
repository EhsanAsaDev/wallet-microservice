package com.company.wallet.repository;

import com.company.wallet.entities.Wallet;
import com.company.wallet.exceptions.WalletException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Wallet JPA repository
 * <p> Generates SQL queries to access the database to manage Wallet entities</p>
 * @author EhsanASaDev
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    List<Wallet> findAllByOrderByIdAsc();
    List<Wallet> findByUserId(String userId);

}