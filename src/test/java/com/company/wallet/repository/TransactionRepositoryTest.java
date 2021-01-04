package com.company.wallet.repository;


import com.company.wallet.entities.CurrencyType;
import com.company.wallet.entities.Transaction;
import com.company.wallet.entities.TransactionType;
import com.company.wallet.entities.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TransactionRepository tests
 * Use in-memory h2database
 * @author Elena Medvedeva
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@PropertySource("classpath:application.properties")
public class TransactionRepositoryTest {
    public static final String TEST_CURRENCY = "RIAL";
    public static final String LAST_UPDATED_BY = "user";
    public static final String USER = "user";
    public static final String credit = "CREDIT";
    public static final String debit = "DEBIT";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    private Wallet wallet1;
    private Wallet wallet2;
    private CurrencyType currencyType;
    private TransactionType typeCredit;
    private TransactionType typeDebit;
    private Transaction transaction;

    static int globalIdCounter = 1;

    public static final Integer CURRENCY_ID = 1;


    @Before
    public void before(){
        currencyType = CurrencyType.RIAL;

        wallet1 = new Wallet( USER ,CurrencyType.RIAL,new BigDecimal(0),LAST_UPDATED_BY);
        wallet2 = new Wallet( USER ,CurrencyType.RIAL,new BigDecimal(0),LAST_UPDATED_BY);

        entityManager.persist(wallet1);
        entityManager.persist(wallet2);
        entityManager.flush();

        typeCredit = TransactionType.CREDIT;
        typeDebit = TransactionType.DEBIT;
        transaction = new Transaction(String.valueOf(globalIdCounter++),typeCredit,new BigDecimal(20),wallet1, currencyType,"Credit transaction");
        entityManager.persist(transaction);
        entityManager.flush();

    }

    @Test
    public void testFindByWallet() {
        List<Transaction> trns = transactionRepository.findByWallet(wallet1);
        assertTrue(trns.size() > 0);
        assertTrue(trns.get(0).getWallet().getId().equals(wallet1.getId()));
        assertTrue(trns.get(0).getId().equals(transaction.getId()));
    }

    @Test
    public void testSave_Credit() {
        int counter = globalIdCounter++;
        Transaction transaction = new Transaction(String.valueOf(counter),typeCredit,new BigDecimal(20),wallet2, currencyType,"Credit transaction");
        Transaction found = transactionRepository.save(transaction);
        assertNotNull(found);
        assertTrue(found.getCurrencyType().name().equals(TEST_CURRENCY));
        assertTrue(found.getAmount().equals(new BigDecimal(20)));
        assertTrue(found.getType().name().equals(credit));
        assertTrue(found.getGlobalId().equals(String.valueOf(counter)));
        assertTrue(found.getWallet().getId().equals(wallet2.getId()));
    }

    @Test
    public void testSave_Debit() {
        int counter = globalIdCounter++;
        Transaction transactionDebit = new Transaction(String.valueOf(counter),typeCredit,new BigDecimal(-10),wallet1, currencyType,"Credit transaction");
        Transaction found = transactionRepository.save(transactionDebit );
        assertNotNull(found);
        assertTrue(found.getCurrencyType().name().equals(TEST_CURRENCY));
        assertTrue(found.getAmount().equals(new BigDecimal(-10)));
        assertTrue(found.getType().name().equals(credit));
        assertTrue(found.getGlobalId().equals(String.valueOf(counter)));
        assertTrue(found.getWallet().getId().equals(wallet1.getId()));
    }

/*    @Test
    public void whenSave_FailWrongCurrency() {
        CurrencyType currencyType = currencyRepository.findByName("AAA");
        int counter = globalIdCounter++;
        Transaction transaction = new Transaction(String.valueOf(counter),typeCredit,new BigDecimal(20),wallet2, currencyType,"Credit transaction");
        try{
            Transaction found = transactionRepository.save(transaction);
            fail();
        } catch(ConstraintViolationException ex){
            assertTrue( ex.getMessage().contains("Transaction currency must be provided"));
        }
    }*/

    @Test
    public void whenSave_NotUniqueGlobalId() {
        int counter = globalIdCounter - 1;
        Transaction transaction = new Transaction(String.valueOf(counter),typeCredit,new BigDecimal(20),wallet2, currencyType,"Credit transaction");
        try{
            Transaction found = transactionRepository.save(transaction);
            entityManager.flush();
            fail();
        } catch(DataIntegrityViolationException ex){
            assertTrue( ex.getMessage().contains("could not execute statement"));
        }
    }

    @Test
    public void whenSave_NoBalance() {
        int counter =  globalIdCounter++;
        Transaction transaction = new Transaction(String.valueOf(counter),typeCredit,null,wallet2, currencyType,"Credit transaction");
        try{
            Transaction found = transactionRepository.save(transaction);
            entityManager.flush();
            fail();
        } catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("Transaction amount must be provided"));

        }
    }

    @Test
    public void whenSave_FailWrongWallet() {
        Wallet wallet = walletRepository.getOne(100);
        int counter = globalIdCounter++;
        Transaction transaction = new Transaction(String.valueOf(counter),typeCredit,new BigDecimal(20),wallet, currencyType,"Credit transaction");
        try{
            Transaction found = transactionRepository.save(transaction);
            entityManager.flush();
            fail();
        } catch(DataIntegrityViolationException ex){
            assertTrue( ex.getMessage().contains("could not execute statement"));
        }
    }

/*    @Test
    public void whenSave_FailWrongType() {
        TransactionType type = transactionTypeRepository.getOne("wrong");
        int counter = globalIdCounter++;
        Transaction transaction = new Transaction(String.valueOf(counter),type,new BigDecimal(20),wallet2, currencyType,"Credit transaction");
        try{
            Transaction found = transactionRepository.save(transaction);
            entityManager.flush();
            fail();
        } catch(DataIntegrityViolationException ex){
            assertTrue( ex.getMessage().contains("could not execute statement"));
        }
    }*/
}
