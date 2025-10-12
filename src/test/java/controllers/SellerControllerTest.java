package controllers;

import com.example.controllers.SellerController;
import com.example.errors.GlobalExceptionHandler;
import com.example.dto.responses.TopSellerResponse;
import com.example.entities.Seller;
import com.example.services.SellerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SellerControllerUnitTest {

  private MockMvc mockMvc;

  @Mock private SellerService sellerService;

  @InjectMocks private SellerController sellerController;

  private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerController)
            .setControllerAdvice(globalExceptionHandler)
            .build();
  }

  @Test
  void getAllSellers_returnsOk() throws Exception {
    Seller s1 = new Seller();
    s1.setId(1);
    s1.setName("Ivan");
    s1.setContactInfo("labuba@gmail.com");

    Seller s2 = new Seller();
    s2.setId(2);
    s2.setName("Anna");
    s2.setContactInfo("la@gmail.com");

    when(sellerService.getAllSellers()).thenReturn(List.of(s1, s2));

    mockMvc
        .perform(get("/sellers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Ivan"))
        .andExpect(jsonPath("$[0].contactInfo").value("labuba@gmail.com"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].name").value("Anna"))
        .andExpect(jsonPath("$[1].contactInfo").value("la@gmail.com"));

    verify(sellerService).getAllSellers();
  }

  @Test
  void getSellerById_found_returnsOk() throws Exception {
    Seller s1 = new Seller();
    s1.setId(1);
    s1.setName("Ivan");
    s1.setContactInfo("labuba@gmail.com");

    when(sellerService.getSellerById(1)).thenReturn(Optional.of(s1));

    mockMvc
        .perform(get("/sellers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Ivan"))
        .andExpect(jsonPath("$.contactInfo").value("labuba@gmail.com"));

    verify(sellerService).getSellerById(1);
  }

  @Test
  void getSellerById_notFound_returns404() throws Exception {
    when(sellerService.getSellerById(999)).thenReturn(Optional.empty());

    mockMvc.perform(get("/sellers/999")).andExpect(status().isNotFound());

    verify(sellerService).getSellerById(999);
  }

  @Test
  void createSeller_returns201AndLocation() throws Exception {
    Seller request = new Seller();
    request.setName("Yaroslav");
    request.setContactInfo("yar@gmail.com");

    Seller created = new Seller();
    created.setId(10);
    created.setName("Yaroslav");
    created.setContactInfo("yar@gmail.com");

    when(sellerService.createSeller(any(Seller.class))).thenReturn(created);

    mockMvc
        .perform(
            post("/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/sellers/10")))
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.name").value("Yaroslav"));

    verify(sellerService).createSeller(any(Seller.class));
  }

  @Test
  void updateSeller_returnsOk() throws Exception {
    Seller request = new Seller();
    request.setName("Yaroslav");
    request.setContactInfo("yar@gmail.com");

    Seller updated = new Seller();
    updated.setId(1);
    updated.setName("Yaroslav");
    updated.setContactInfo("yar@gmail.com");

    when(sellerService.updateSeller(eq(1), any(Seller.class))).thenReturn(updated);

    mockMvc
        .perform(
            put("/sellers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Yaroslav"));

    verify(sellerService).updateSeller(eq(1), any(Seller.class));
  }

  @Test
  void deleteSeller_returnsNoContent() throws Exception {
    doNothing().when(sellerService).deleteSeller(1);

    mockMvc.perform(delete("/sellers/1")).andExpect(status().isNoContent());

    verify(sellerService).deleteSeller(1);
  }

  @Test
  void findTopSellerByAmount_returnsOk() throws Exception {
    Seller top = new Seller();
    top.setId(5);
    top.setName("Andrey");

    TopSellerResponse resp = new TopSellerResponse(top, 1234.56);

    when(sellerService.findTopSellerByAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(resp);

    String start = "2025-10-01T00:00:00";
    String end = "2025-10-12T00:00:00";

    mockMvc
        .perform(get("/sellers/top-seller").param("start", start).param("end", end))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.seller.id").value(5))
        .andExpect(jsonPath("$.totalAmount").value(1234.56));

    verify(sellerService).findTopSellerByAmount(any(LocalDateTime.class), any(LocalDateTime.class));
  }

  @Test
  void findTopSellerByAmount_noContent() throws Exception {
    when(sellerService.findTopSellerByAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(null);

    String start = "2025-10-01T00:00:00";
    String end = "2025-10-12T00:00:00";

    mockMvc
        .perform(get("/sellers/top-seller").param("start", start).param("end", end))
        .andExpect(status().isNoContent());

    verify(sellerService).findTopSellerByAmount(any(LocalDateTime.class), any(LocalDateTime.class));
  }

  @Test
  void getSellersBelow_returnsOk() throws Exception {
    Seller s1 = new Seller();
    s1.setId(3);
    s1.setName("Andrey");
    TopSellerResponse r1 = new TopSellerResponse(s1, 100.0);

    when(sellerService.findSellersWithTotalLessThan(
            any(LocalDateTime.class), any(LocalDateTime.class), any(BigDecimal.class)))
        .thenReturn(List.of(r1));

    mockMvc
        .perform(
            get("/sellers/sellers-below")
                .param("start", "2025-10-01T00:00:00")
                .param("end", "2025-10-12T00:00:00")
                .param("limit", "2000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
        .andExpect(jsonPath("$[0].seller.id").value(3))
        .andExpect(jsonPath("$[0].totalAmount").value(100.0));

    verify(sellerService)
        .findSellersWithTotalLessThan(
            any(LocalDateTime.class), any(LocalDateTime.class), any(BigDecimal.class));
  }

  @Test
  void getSellersBelow_missingStart_returnsBadRequest() throws Exception {
    mockMvc
        .perform(
            get("/sellers/sellers-below")
                .param("end", "2025-10-12T00:00:00")
                .param("limit", "2000"))
        .andExpect(status().isBadRequest());
  }
}
