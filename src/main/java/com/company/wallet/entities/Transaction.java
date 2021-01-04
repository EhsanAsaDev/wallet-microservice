package com.company.wallet.entities;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  Transaction entity.
 *
 *  @author EhsanASaDev
 */
@Entity
@Table(name = "TRANSACTION")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Transaction globalId must not be empty")
    @NotNull(message = "Transaction globalId must be provided")
    @Column(name = "global_id", unique = true, nullable = false)
    private String globalId;

    @NotNull(message = "Transaction type must be provided")
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    @NotNull(message = "Transaction amount must be provided")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Transaction wallet must be provided")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @NotNull(message = "Currency Type must be provided")
    @Column(name = "currency_type")
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @Column(name = "description")
    String description;

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    public Transaction(){ }

    public Transaction(String globalId, TransactionType type, BigDecimal amount, Wallet wallet, CurrencyType currencyType, String description) {
        this.globalId = globalId;
        this.type = type;
        this.amount = amount;
        this.wallet = wallet;
        this.currencyType = currencyType;
        this.description = description;
        this.lastUpdated = new Date();
    }

    public Transaction(String globalId, TransactionType type, BigDecimal amount, Wallet wallet, CurrencyType currencyType, String description, String lastUpdatedBy) {
       this(globalId,type,amount,wallet, currencyType,description);
       this.lastUpdatedBy = lastUpdatedBy;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet destWallet) {
        this.wallet = wallet;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

}
