package com.ecommerce.product.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.dto.DeliveryType;
import com.ecommerce.product.dto.ProductsSearchResponse;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductDocumentRepository;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductDocumentRepository productDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 검색 service
     *
     * @param keyword
     * @return map
     */
    public Map<String, Object> search(String keyword, DeliveryType deliveryType, SortType sortKey) {

        // 쿼리문 만들기
        Query query = makeSearchQuery(keyword, deliveryType, sortKey);

        // 검색 실행
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);


        Map<String, Object> result = new HashMap<>();

        result.put("count", searchHits.getTotalHits());

        List<ProductsSearchResponse> productsSearchResponses = searchHits.getSearchHits().stream().map(hit -> new ProductsSearchResponse(hit.getContent())).toList();

        result.put("data", productsSearchResponses);

        return result;
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
            NativeQuery termQuery = makeTermQuery(deliveryType.getFieldName(), deliveryType.getFieldValue());
            queries.add(termQuery);
        }

        return NativeQuery.builder().withQuery(q -> q.bool(b -> b.must(queries.stream().map(NativeQuery::getQuery).toList())))
                .withSort(s -> s
                        .field(
                                p -> p
                                .field(sortKey.getFieldName())
                                .order(sortKey.getSortOrder())
                        )
                ).build();
    }

    private NativeQuery makeMultiMathQuery(String keyword, List<String> fields) {
        return NativeQuery.builder().withQuery(q -> q.multiMatch(m -> m.query(keyword).fields(fields))).build();
    }

    private NativeQuery makeTermQuery(String fieldName, FieldValue value) {
        return NativeQuery.builder().withQuery(q -> q.term(t -> t.field(fieldName).value(value))).build();
    }


    @Transactional
    public void sync() {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            ProductDocument productDocument = new ProductDocument(product);
            productDocumentRepository.save(productDocument);
        }
    }
}
