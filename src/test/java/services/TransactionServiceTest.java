package services;

import com.example.dto.PaymentType;
import com.example.entities.Seller;
import com.example.entities.Transaction;
import com.example.dto.requests.TransactionRequest;
import com.example.repositories.SellerRepository;
import com.example.repositories.TransactionRepository;
import com.example.services.TransactionService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransactionServiceUnitTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private SellerRepository sellerRepository;

  @InjectMocks private TransactionService transactionService;

  private Seller seller;

  @BeforeEach
  void setUp() {
    seller = new Seller();
    seller.setId(1);
    seller.setName("Andrey");
  }

  @Test
  void getAllTransactions_returnsList() {
    Transaction t1 = new Transaction(seller, 100.0, PaymentType.CASH);
    Transaction t2 = new Transaction(seller, 200.0, PaymentType.CARD);

    when(transactionRepository.findAll()).thenReturn(List.of(t1, t2));

    List<Transaction> result = transactionService.getAllTransactions();

    assertEquals(2, result.size());
    verify(transactionRepository).findAll();
  }

  @Test
  void getTransactionById_validId_returnsTransaction() {
    Transaction t = new Transaction(seller, 150.0, PaymentType.CASH);
    when(transactionRepository.findById(1)).thenReturn(Optional.of(t));

    Optional<Transaction> result = transactionService.getTransactionById(1);

    assertTrue(result.isPresent());
    assertEquals(150.0, result.get().getAmount());
    verify(transactionRepository).findById(1);
  }

  @Test
  void getTransactionById_invalidId_throwsException() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> transactionService.getTransactionById(0));
    assertEquals("id must be positive", ex.getMessage());
  }

  @Test
  void createTransaction_validRequest_returnsTransaction() {
    TransactionRequest request = new TransactionRequest();
    request.setSellerId(1);
    request.setAmount(123.0);
    request.setPaymentType(PaymentType.CASH);

    when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));

    Transaction savedTransaction = new Transaction(seller, 123.0, PaymentType.CASH);
    savedTransaction.setTransactionDate(LocalDateTime.now());

    when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

    Transaction result = transactionService.createTransaction(request);

    assertNotNull(result);
    assertEquals(123.0, result.getAmount());
    assertEquals(PaymentType.CASH, result.getPaymentType());

    verify(sellerRepository).findById(1);
    verify(transactionRepository).save(any(Transaction.class));
  }

  @Test
  void createTransaction_sellerNotFound_throwsEntityNotFound() {
    TransactionRequest request = new TransactionRequest();
    request.setSellerId(99);
    request.setAmount(123.0);
    request.setPaymentType(PaymentType.CASH);

    when(sellerRepository.findById(99)).thenReturn(Optional.empty());

    EntityNotFoundException ex =
        assertThrows(
            EntityNotFoundException.class, () -> transactionService.createTransaction(request));
    assertEquals("Seller not found with id: 99", ex.getMessage());
  }

  @Test
  void createTransaction_invalidAmount_throwsIllegalArgument() {
    TransactionRequest request = new TransactionRequest();
    request.setSellerId(1);
    request.setAmount(-1.0);
    request.setPaymentType(PaymentType.CASH);

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> transactionService.createTransaction(request));
    assertEquals("amount must be positive", ex.getMessage());
  }

  @Test
  void getAllTransactionsBySellerId_validSeller_returnsList() {
    Transaction t1 = new Transaction(seller, 50.0, PaymentType.CARD);
    when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));
    when(transactionRepository.findBySellerId(1)).thenReturn(List.of(t1));

    List<Transaction> result = transactionService.getAllTransactionsBySellerId(1);

    assertEquals(1, result.size());
    assertEquals(50.0, result.get(0).getAmount());

    verify(sellerRepository).findById(1);
    verify(transactionRepository).findBySellerId(1);
  }

  @Test
  void getAllTransactionsBySellerId_sellerNotFound_throwsEntityNotFound() {
    when(sellerRepository.findById(99)).thenReturn(Optional.empty());

    EntityNotFoundException ex =
        assertThrows(
            EntityNotFoundException.class,
            () -> transactionService.getAllTransactionsBySellerId(99));
    assertEquals("Seller not found with id: 99", ex.getMessage());
  }

  @Test
  void getAllTransactionsBySellerId_invalidSellerId_throwsIllegalArgument() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.getAllTransactionsBySellerId(0));
    assertEquals("sellerId must be positive", ex.getMessage());
  }
}
