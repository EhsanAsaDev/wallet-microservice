package com.company.wallet.repository;

import com.company.wallet.entities.CurrencyType;
import com.company.wallet.entities.Wallet;

import javax.validation.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
/**
 * WalletRepository tests
 * Use in-memory h2database
 * @author Elena Medvedeva
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class WalletRepositoryTest {
    public static final String TEST_CURRENCY = "RIAL";
    public static final String LAST_UPDATED_BY = "user";
    public static final String USER = "user";
    public static final Integer CURRENCY_ID = 1;


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletRepository walletRepository2;


    private Wallet wallet1;
    private Wallet wallet2;
    private CurrencyType currencyType;


    @Before
    public void before(){
        currencyType = CurrencyType.RIAL;

        wallet1 = new Wallet( USER ,CurrencyType.RIAL,new BigDecimal(0),LAST_UPDATED_BY);

        wallet2 = new Wallet(USER,CurrencyType.RIAL,new BigDecimal(0),LAST_UPDATED_BY);


        entityManager.persist(wallet1);
        entityManager.persist(wallet2);
        entityManager.flush();
    }

    @Test
    public void whenFindById_thenReturnWallet() {
        // when
        Optional<Wallet> found = walletRepository.findById(wallet2.getId());
        assertTrue(found.isPresent());

        // then
        assertTrue(found.get().getCurrencyType().name().equals(TEST_CURRENCY));
        assertTrue(found.get().getUserId().equals(USER));
        assertTrue(found.get().getBalance().equals(new BigDecimal(0)));
    }

    @Test
    public void whenFindById_NoWallet() {
        Optional<Wallet> found = walletRepository.findById(10);
        assertTrue(!found.isPresent());
    }

    @Test
    public void whenFindByUserId_thenReturnWallet() {
        // when
        List<Wallet> found = walletRepository.findByUserId(USER);
        //then
        assertNotNull(found);
        assertTrue(found.size() == 2);
        assertTrue(found.get(0).getUserId().equals(USER));
        assertTrue(found.get(1).getUserId().equals(USER));
    }

    @Test
    public void whenFindByUserId_NotFound() {
        // when
        List<Wallet> found = walletRepository.findByUserId("wrongUser");
        //then
        assertNotNull(found);
        assertTrue(found.size() == 0);
    }

    @Test
    public void testFindAllByOrderByIdAsc() {
        List<Wallet> found = walletRepository.findAllByOrderByIdAsc();
        assertNotNull(found);
        assertTrue(!found.isEmpty());
        assertTrue(found.size() >= 2);
        System.out.println(found.get(0).getId());
        System.out.println(found.get(1).getId());
        assertTrue(found.get(0).getId().equals(wallet1.getId()));
        assertTrue(found.get(1).getId().equals(wallet2.getId()));
    }

    @Test
    public void whenSave_Success() {
        Wallet wallet = new Wallet(USER,CurrencyType.RIAL,new BigDecimal(0),LAST_UPDATED_BY);
        Wallet found = walletRepository.save(wallet);
        assertNotNull(found);
        assertTrue(found.getCurrencyType().name().equals(TEST_CURRENCY));
        assertTrue(found.getBalance().equals(new BigDecimal(0)));
    }

    /*@Test
    public void whenSave_FailWrongCurrency() {
        CurrencyType currencyType = currencyRepository.findByName("AAA");
        Wallet wallet = new Wallet(USER, currencyType,new BigDecimal(0),LAST_UPDATED_BY);
        try{
        Wallet found = walletRepository.save(wallet);
        fail();
        } catch(ConstraintViolationException ex){
            assertTrue( ex.getMessage().contains("Wallet currency must be provided"));
        }
    }*/

    /*@Test
    public void whenSave_FailWrongCurrencyLong() {
        CurrencyType currencyType = currencyRepository.findByName("AAA+++");
        Wallet wallet = new Wallet(USER, currencyType,new BigDecimal(0),LAST_UPDATED_BY);
        try{
            Wallet found = walletRepository.save(wallet);
            fail();
        } catch(ConstraintViolationException ex){
            assertTrue( ex.getMessage().contains("Wallet currency must be provided"));
        }
    }*/



    @Test
    public void update_Balance() {
        Optional<Wallet> found = walletRepository.findById(wallet1.getId());
        Wallet updated = found.get();
        updated.setBalance(new BigDecimal(300));
        Wallet found1 = walletRepository.save(updated);
        assertNotNull(found1);
        assertTrue(found1.getBalance().equals(new BigDecimal(300)));
    }

    @Test
    public void update_Balance_checkVersion() {
        try {
            Optional<Wallet> found = walletRepository.findById(wallet1.getId());
            Wallet updated = found.get();

            Wallet dirtyWallet = new Wallet(updated.getUserId(), updated.getCurrencyType(), updated.getBalance().add(BigDecimal.valueOf(400)), updated.getLastUpdatedBy());
            dirtyWallet.setId(updated.getId());
            dirtyWallet.setVersion(updated.getVersion());

            updated.setBalance(updated.getBalance().add(BigDecimal.valueOf(300)));
            Wallet found1 = walletRepository.save(updated);
            assertNotNull(found1);
            assertTrue(found1.getBalance().equals(new BigDecimal(300)));
            entityManager.flush();

            walletRepository.save(dirtyWallet);
            fail();
        } catch (ObjectOptimisticLockingFailureException x) {
            assertTrue(x.getMessage().contains("Row was updated or deleted by another transaction"));
        }
    }

    @Test
    public void update_BalanceNegative() {
        Optional<Wallet> found = walletRepository.findById(wallet2.getId());
        Wallet updated = found.get();
        updated.setBalance(new BigDecimal(-300));
        Wallet found1 = walletRepository.save(updated);
        try{
            entityManager.flush();
            fail();
        } catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("must be greater than or equal to 0"));
        }
    }

    @Test
    public void update_BalanceNull() {
        Optional<Wallet> found = walletRepository.findById(wallet2.getId());
        Wallet updated = found.get();
        updated.setBalance(null);
        Wallet found1 = walletRepository.save(updated);
        try{
            entityManager.flush();
            fail();
        } catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            System.out.println(ex.getConstraintViolations().iterator().next().getMessage());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("Wallet balance must be provided"));
        }
    }

    @After
    public void after(){
    }

}
