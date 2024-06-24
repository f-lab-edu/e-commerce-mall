package com.ecommerce.product.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
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
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductDocumentRepository productDocumentRepository;
  private final ElasticsearchOperations elasticsearchOperations;
  private final CategoryService categoryService;
  private final KafkaProducer kafkaProducer;
  @PersistenceContext
  private EntityManager entityManager;


  /**
   * 상품 저장
   *
   * @param productRequest
   * @return saveProduct
   */
  @Transactional
  public Product save(AddProductRequest productRequest) {
    Category category = categoryService.findCategoryById(productRequest.getCategoryId());
    Product product = Product.builder()
        .category(category)
        .name(productRequest.getName())
        .price(productRequest.getPrice())
        .thumbImg(productRequest.getThumbImg())
        .detailImg(productRequest.getDetailImg())
        .brand(productRequest.getBrand())
        .stock(productRequest.getStock())
        .deliveryFee(productRequest.getDeliveryFee())
        .fastDelivery(productRequest.getFastDelivery())
        .build();
    // 1. DB 저장
    Product saveProduct = productRepository.save(product);

    // 2. producer에 데이터 전달
    kafkaProducer.sendProduct(saveProduct);
    return saveProduct;
  }

  /**
   * 썸네일 변경
   *
   * @param id
   * @param thumbImg
   * @return
   */
  @Transactional
  public Product updateThumbImg(Long id, String thumbImg) {
    Product product = productRepository.findById(id).orElseThrow();
    product.updateThumbImg(thumbImg);
    System.out.println("썸네일 저장되는 값 :: " + thumbImg);

    kafkaProducer.sendProduct(product);
    return product;
  }

  /**
   * 검색 service
   *
   * @param keyword
   * @return map
   */
  public SearchHits<ProductDocument> search(String keyword, DeliveryType deliveryType,
      SortType sortKey) {

    // 쿼리문 만들기
    Query query = makeSearchQuery(keyword, deliveryType, sortKey);

    // 검색 실행
    return elasticsearchOperations.search(query, ProductDocument.class);
  }

  /**
   * 검색 쿼리문 생성
   *
   * @return query
   */
  private Query makeSearchQuery(String keyword, DeliveryType deliveryType, SortType sortKey) {
    // 쿼리 생성

    // 키워드 적용할 필드 리스트
    List<String> fields = new ArrayList<>();
    fields.add("categoryName");
    fields.add("name");
    fields.add("brand");

    List<NativeQuery> queries = new ArrayList<>();
    // multi_match 쿼리문 생성
    NativeQuery multiMathQuery = makeMultiMathQuery(keyword, fields);
    queries.add(multiMathQuery);
    if (deliveryType != null) {
      // match 쿼리문 생성
      NativeQuery termQuery = makeTermQuery(deliveryType.getFieldName(),
          deliveryType.getFieldValue());
      queries.add(termQuery);
    }

    return NativeQuery.builder()
        .withQuery(q -> q.bool(b -> b.must(queries.stream().map(NativeQuery::getQuery).toList())))
        .withSort(s -> s
            .field(
                p -> p
                    .field(sortKey.getFieldName())
                    .order(sortKey.getSortOrder())
            )
        ).build();
  }

  private NativeQuery makeMultiMathQuery(String keyword, List<String> fields) {
    return NativeQuery.builder().withQuery(q -> q.multiMatch(m -> m.query(keyword).fields(fields)))
        .build();
  }

  private NativeQuery makeTermQuery(String fieldName, FieldValue value) {
    return NativeQuery.builder().withQuery(q -> q.term(t -> t.field(fieldName).value(value)))
        .build();
  }


  @Transactional
  public void sync() {
    List<Product> products = productRepository.findAll();

    for (Product product : products) {
      ProductDocument productDocument = new ProductDocument(product);
      productDocumentRepository.save(productDocument);
    }
  }

  public List<Product> readProducts(long id, SortType sortKey) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> query = cb.createQuery(Product.class);
    Root<Product> product = query.from(Product.class);

    query.where(cb.equal(product.get("category").get("id"), id));

    if (sortKey != null) {
      if (sortKey.isAsc()) {
        query.orderBy(cb.asc(product.get(sortKey.getFieldName())));
      } else {
        query.orderBy(cb.desc(product.get(sortKey.getFieldName())));
      }
    } else {
      query.orderBy(cb.desc(product.get(SortType.RANK.getFieldName())));
    }

    return entityManager.createQuery(query).getResultList();
  }
}
