package com.ecommerce.product.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.service.CategoryService;
import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.dto.AddProductRequest;
import com.ecommerce.product.dto.DeliveryType;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.producer.KafkaProducer;
import com.ecommerce.product.repository.ProductDocumentRepository;
import com.ecommerce.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

class ProductServiceTest {

  @Mock
  private CategoryService categoryService;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private ProductDocumentRepository productDocumentRepository;
  @Mock
  private ElasticsearchOperations elasticsearchOperations;
  @Mock
  private KafkaProducer kafkaProducer;
  @Mock
  private EntityManager entityManager;
  @InjectMocks
  private ProductService productService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(productService, "entityManager", entityManager); // 수동 주입
  }

  @DisplayName("상품 저장에 성공한다.")
  @Test
  void save() {
    // given
    AddProductRequest request = new AddProductRequest(
        1L,
        "Test Product",
        new BigDecimal("10000.00"),
        "thumb.jpg",
        "detail.jpg",
        "Test Brand",
        10,
        2500,
        0 // fastDelivery는 Integer 타입이지만 값이 null일 수 있어서 0으로 설정
    );

    Category category = Category.builder()
        .id(1L)
        .name("Test Category")
        .sortKey(100)
        .build();

    when(categoryService.findCategoryById(anyLong())).thenReturn(category);
    when(productRepository.save(any())).thenAnswer((Answer<Product>) invocation -> {
      Product productArgument = invocation.getArgument(0);
      return Product.builder()
          .id(1L)
          .category(productArgument.getCategory())
          .name(productArgument.getName())
          .price(productArgument.getPrice())
          .thumbImg(productArgument.getThumbImg())
          .detailImg(productArgument.getDetailImg())
          .brand(productArgument.getBrand())
          .stock(productArgument.getStock())
          .deliveryFee(productArgument.getDeliveryFee())
          .fastDelivery(productArgument.getFastDelivery()).build();
    });

    // when
    Product savedProduct = productService.save(request);

    // then
    assertThat(savedProduct).isNotNull();
    assertThat(savedProduct.getId()).isEqualTo(1L);
    assertThat(savedProduct.getName()).isEqualTo(request.getName());
    assertThat(savedProduct.getPrice()).isEqualByComparingTo(request.getPrice());
    assertThat(savedProduct.getThumbImg()).isEqualTo(request.getThumbImg());
    assertThat(savedProduct.getDetailImg()).isEqualTo(request.getDetailImg());
    assertThat(savedProduct.getBrand()).isEqualTo(request.getBrand());
    assertThat(savedProduct.getStock()).isEqualTo(request.getStock());
    assertThat(savedProduct.getDeliveryFee()).isEqualTo(request.getDeliveryFee());
    assertThat(savedProduct.getFastDelivery()).isEqualTo(
        0); // fastDelivery는 Integer이지만 값을 0으로 초기화했음을 확인

    verify(productRepository, times(1)).save(any(Product.class));
    verify(kafkaProducer, times(1)).sendProduct(any(Product.class));
  }

  @DisplayName("썸네일 변경에 성공한다.")
  @Test
  void updateThumbImg() {
    // given
    Long productId = 1L;
    String newThumbImg = "new_thumb_img_url";
    Product existingProduct = Product.builder()
        .id(productId)
        .name("Test Product")
        .price(BigDecimal.valueOf(100))
        .thumbImg("old_thumb_img_url")
        .detailImg("detail_img_url")
        .brand("TestBrand")
        .stock(10)
        .deliveryFee(0)
        .fastDelivery(0)
        .build();

    // Mock repository의 findById 메소드가 호출될 때 리턴할 객체 설정
    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

    // when
    Product updatedProduct = productService.updateThumbImg(productId, newThumbImg);

    // then
    verify(productRepository, times(1)).findById(productId); // findById 메소드가 1번 호출됐는지 확인
    verify(kafkaProducer, times(1)).sendProduct(
        updatedProduct); // kafkaProduct의 sendProduct 메소드가 1번 호출됐는지 확인

    // 썸네일이 업데이트되었는지 확인
    assert updatedProduct != null;
    assert updatedProduct.getThumbImg().equals(newThumbImg);
  }

  @DisplayName("상품 검색에 성공한다.")
  @Test
  void search() {
    String keyword = "keyword";
    DeliveryType deliveryType = DeliveryType.FAST;
    SortType sortType = SortType.HIGHPRICE;

    SearchHits<ProductDocument> mockSearchHits = mock(SearchHits.class);
    given(elasticsearchOperations.search(any(Query.class), any(Class.class))).willReturn(
        mockSearchHits);

    SearchHits<ProductDocument> result = productService.search(keyword, deliveryType, sortType);

    verify(elasticsearchOperations).search(any(Query.class), any(Class.class));
    assertThat(result).isEqualTo(mockSearchHits);
  }

  @DisplayName("카테고리별 상품 조회에 성공한다.")
  @Test
  void readProductsSuccess() {
    // given
    // Mock data
    long categoryId = 1L;
    Category category = Category.builder()
        .id(categoryId)
        .name("Test Category")
        .build();
    SortType sortType = SortType.HIGHPRICE; // Replace with your desired sort type
    Product product1 = Product.builder()
        .category(category)
        .id(1L)
        .name("Test Product1")
        .price(new BigDecimal(10000))
        .build(); // Initialize with appropriate values
    Product product2 = Product.builder()
        .category(category)
        .id(2L)
        .name("Test Product2")
        .price(new BigDecimal(5000))
        .build();// Initialize with appropriate values
    List<Product> mockProductList = Arrays.asList(product1, product2);

    // Mocking CriteriaBuilder, CriteriaQuery and Root
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    CriteriaQuery<Product> query = mock(CriteriaQuery.class);
    Root<Product> product = mock(Root.class);

    Path<Object> categoryPath = mock(Path.class);
    Path<Object> idPath = mock(Path.class);
    TypedQuery<Product> typedQuery = mock(TypedQuery.class);
    Predicate predicate = mock(Predicate.class);

    // Mocking entity manager behavior
    when(entityManager.getCriteriaBuilder()).thenReturn(cb);
    when(cb.createQuery(Product.class)).thenReturn(query);
    when(query.from(Product.class)).thenReturn(product);

    when(product.get("category")).thenReturn(categoryPath);
    when(categoryPath.get("id")).thenReturn(idPath);
    when(cb.equal(idPath, categoryId)).thenReturn(predicate);
    when(query.where(predicate)).thenReturn(query);
    when(entityManager.createQuery(query)).thenReturn(typedQuery);
    when(typedQuery.getResultList()).thenReturn(mockProductList);

    // when
    List<Product> result = productService.readProducts(categoryId, sortType);

    // then
    assertEquals(mockProductList, result);
  }

  @DisplayName("잘못된 카테고리 id 요청으로 상품 조회에 실패한다.")
  @Test
  void readProductsFailure() {
    // Mock data
    long invalidCategoryId = 999L; // Using an invalid category ID
    SortType sortType = SortType.HIGHPRICE;

    // Mocking CriteriaBuilder, CriteriaQuery, and Root
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    CriteriaQuery<Product> query = mock(CriteriaQuery.class);
    Root<Product> productRoot = mock(Root.class);
    Path<Object> categoryPath = mock(Path.class);
    Path<Object> idPath = mock(Path.class);
    TypedQuery<Product> typedQuery = mock(TypedQuery.class);
    Predicate predicate = mock(Predicate.class);

    // Mocking entity manager behavior
    when(entityManager.getCriteriaBuilder()).thenReturn(cb);
    when(cb.createQuery(Product.class)).thenReturn(query);
    when(query.from(Product.class)).thenReturn(productRoot);
    when(productRoot.get("category")).thenReturn(categoryPath);
    when(categoryPath.get("id")).thenReturn(idPath);
    when(cb.equal(idPath, invalidCategoryId)).thenReturn(predicate);
    when(query.where(predicate)).thenReturn(query);
    when(entityManager.createQuery(query)).thenReturn(typedQuery);
    when(typedQuery.getResultList()).thenReturn(
        Arrays.asList()); // Returning empty list for invalid category ID

    // Invoke the method
    List<Product> result = productService.readProducts(invalidCategoryId, sortType);

    // Assert the result
    assertEquals(0, result.size()); // Expecting empty list for invalid category ID
  }
}