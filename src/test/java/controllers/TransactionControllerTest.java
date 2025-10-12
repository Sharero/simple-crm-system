package controllers;

import com.example.controllers.TransactionController;
import com.example.dto.PaymentType;
import com.example.dto.requests.TransactionRequest;
import com.example.entities.Seller;
import com.example.entities.Transaction;
import com.example.errors.GlobalExceptionHandler;
import com.example.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransactionControllerUnitTest {

  private MockMvc mockMvc;

  @Mock private TransactionService transactionService;

  @InjectMocks private TransactionController transactionController;

  private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private Transaction transaction;
  private Seller seller;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(transactionController)
            .setControllerAdvice(globalExceptionHandler)
            .build();

    seller = new Seller();
    seller.setId(1);
    seller.setName("Sasha");

    transaction = new Transaction();
    transaction.setId(1);
    transaction.setSeller(seller);
    transaction.setAmount(100.0);
  }

  @Test
  void getAllTransactions_returnsOk() throws Exception {
    when(transactionService.getAllTransactions()).thenReturn(List.of(transaction));

    mockMvc
        .perform(get("/transactions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(transaction.getId()))
        .andExpect(jsonPath("$[0].amount").value(transaction.getAmount()));

    verify(transactionService).getAllTransactions();
  }

  @Test
  void getTransactionById_found_returnsOk() throws Exception {
    when(transactionService.getTransactionById(1)).thenReturn(Optional.of(transaction));

    mockMvc
        .perform(get("/transactions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(transaction.getId()))
        .andExpect(jsonPath("$.amount").value(transaction.getAmount()));

    verify(transactionService).getTransactionById(1);
  }

  @Test
  void getTransactionById_notFound_returns404() throws Exception {
    when(transactionService.getTransactionById(99)).thenReturn(Optional.empty());

    mockMvc.perform(get("/transactions/99")).andExpect(status().isNotFound());

    verify(transactionService).getTransactionById(99);
  }

  @Test
  void createTransaction_returnsOk() throws Exception {
    TransactionRequest request = new TransactionRequest();
    request.setSellerId(1);
    request.setAmount(100.0);
    request.setPaymentType(PaymentType.CASH);

    when(transactionService.createTransaction(any(TransactionRequest.class)))
        .thenReturn(transaction);

    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(transaction.getId()))
        .andExpect(jsonPath("$.amount").value(transaction.getAmount()));

    verify(transactionService).createTransaction(any(TransactionRequest.class));
  }

  @Test
  void getAllTransactionsBySellerId_returnsOk() throws Exception {
    when(transactionService.getAllTransactionsBySellerId(1)).thenReturn(List.of(transaction));

    mockMvc
        .perform(get("/transactions/sellers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(transaction.getId()))
        .andExpect(jsonPath("$[0].amount").value(transaction.getAmount()));

    verify(transactionService).getAllTransactionsBySellerId(1);
  }
}
