package com.company.wallet.service;

import com.company.wallet.entities.CurrencyType;
import com.company.wallet.entities.Transaction;
import com.company.wallet.entities.TransactionType;
import com.company.wallet.exceptions.WalletException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Service for managing transactions
 * @author EhsanASaDev
 */
public interface TransactionService {
    public List<Transaction> getTransactionsByWalletId(@NotNull Integer walletId) throws WalletException;
    public Transaction createTransaction(@NotBlank String globalId, @NotNull CurrencyType currencyType, @NotBlank String walletId, @NotNull TransactionType transactionType, @NotBlank String amount, String description) throws WalletException;

}
