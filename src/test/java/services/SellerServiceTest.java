package services;

import com.example.dto.responses.TopSellerResponse;
import com.example.entities.Seller;
import com.example.repositories.SellerRepository;
import com.example.services.SellerService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SellerServiceUnitTest {

  @Mock private SellerRepository sellerRepository;

  @InjectMocks private SellerService sellerService;

  private Seller seller;

  @BeforeEach
  void setUp() {
    seller = new Seller();
    seller.setId(1);
    seller.setName("Ivan");
    seller.setContactInfo("ivan@gmail.com");
  }

  @Test
  void getAllSellers_returnsList() {
    Seller s2 = new Seller();
    s2.setId(2);
    s2.setName("Anna");

    when(sellerRepository.findAll()).thenReturn(List.of(seller, s2));

    List<Seller> result = sellerService.getAllSellers();

    assertEquals(2, result.size());
    verify(sellerRepository).findAll();
  }

  @Test
  void getSellerById_validId_returnsSeller() {
    when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));

    Optional<Seller> result = sellerService.getSellerById(1);

    assertTrue(result.isPresent());
    assertEquals("Ivan", result.get().getName());
    verify(sellerRepository).findById(1);
  }

  @Test
  void getSellerById_invalidId_throwsIllegalArgument() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> sellerService.getSellerById(0));
    assertEquals("id must be positive", ex.getMessage());
  }

  @Test
  void createSeller_validSeller_returnsSaved() {
    seller.setId(10);
    when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

    Seller result = sellerService.createSeller(seller);

    assertNotNull(result);
    assertEquals(10, result.getId());
    verify(sellerRepository).save(any(Seller.class));
  }

  @Test
  void createSeller_nullSeller_throwsException() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> sellerService.createSeller(null));
    assertEquals("seller must not be null", ex.getMessage());
  }

  @Test
  void updateSeller_existingSeller_updatesSuccessfully() {
    Seller update = new Seller();
    update.setName("Kolya");
    update.setContactInfo("kol@gmail.com");

    when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));
    when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

    Seller result = sellerService.updateSeller(1, update);

    assertEquals("Kolya", result.getName());
    assertEquals("kol@gmail.com", result.getContactInfo());
    verify(sellerRepository).findById(1);
    verify(sellerRepository).save(any(Seller.class));
  }

  @Test
  void updateSeller_notFound_throwsEntityNotFound() {
    when(sellerRepository.findById(99)).thenReturn(Optional.empty());

    Seller update = new Seller();
    update.setName("Vanya");

    EntityNotFoundException ex =
        assertThrows(EntityNotFoundException.class, () -> sellerService.updateSeller(99, update));
    assertEquals("Seller not found with id: 99", ex.getMessage());
  }

  @Test
  void deleteSeller_existingSeller_callsRepository() {
    doNothing().when(sellerRepository).deleteById(1);

    sellerService.deleteSeller(1);

    verify(sellerRepository).deleteById(1);
  }

  @Test
  void deleteSeller_notFound_throwsEntityNotFound() {
    doThrow(new org.springframework.dao.EmptyResultDataAccessException(1))
        .when(sellerRepository)
        .deleteById(99);

    EntityNotFoundException ex =
        assertThrows(EntityNotFoundException.class, () -> sellerService.deleteSeller(99));

    assertEquals("Seller not found while deleting with id: 99", ex.getMessage());
  }

  @Test
  void findTopSellerByAmount_returnsTopSeller() {
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();

    Seller seller = new Seller();
    seller.setId(1);
    seller.setName("Ivan");

    Object[] row = new Object[] {1, 1234.0};
    List<Object[]> rows = new ArrayList<>();
    rows.add(row);

    when(sellerRepository.findTopSellerByAmount(any(Timestamp.class), any(Timestamp.class)))
        .thenReturn(rows);
    when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));

    TopSellerResponse result = sellerService.findTopSellerByAmount(start, end);

    assertEquals("Ivan", result.getSeller().getName());
    assertEquals(1234.0, result.getTotalAmount());
  }

  @Test
  void findTopSellerByAmount_noRows_throwsEntityNotFound() {
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();

    when(sellerRepository.findTopSellerByAmount(any(Timestamp.class), any(Timestamp.class)))
        .thenReturn(Collections.emptyList());

    EntityNotFoundException ex =
        assertThrows(
            EntityNotFoundException.class, () -> sellerService.findTopSellerByAmount(start, end));

    assertEquals("No transactions found in given period", ex.getMessage());
  }

  @Test
  void findSellersWithTotalLessThan_returnsList() {
    Seller seller = new Seller();
    seller.setId(1);
    seller.setName("Ivan");

    Object[] row = new Object[] {1, 100.0};
    List<Object[]> rows = new ArrayList<>();
    rows.add(row);

    when(sellerRepository.findSellersWithTotalLessThan(
            any(Timestamp.class), any(Timestamp.class), any(BigDecimal.class)))
        .thenReturn(rows);

    when(sellerRepository.findAllById(anyList())).thenReturn(List.of(seller));

    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();

    List<TopSellerResponse> result =
        sellerService.findSellersWithTotalLessThan(start, end, BigDecimal.valueOf(200));

    assertEquals(1, result.size());
    assertEquals(100.0, result.get(0).getTotalAmount());
    assertEquals("Ivan", result.get(0).getSeller().getName());
  }

  @Test
  void findSellersWithTotalLessThan_endBeforeStart_throwsIllegalArgument() {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().minusDays(1);

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> sellerService.findSellersWithTotalLessThan(start, end, BigDecimal.TEN));

    assertEquals("end must be after start", ex.getMessage());
  }
}
