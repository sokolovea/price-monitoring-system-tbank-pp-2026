package ru.tbank.pp.repository;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.ProductPriceId;

import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, ProductPriceId> {

    List<ProductPrice> findByProductIdOrderByIdCheckDateDesc(Long productId);

    @Query(value = """
        SELECT * FROM (
        SELECT *, ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY check_date DESC) as rownum
        FROM product_price
        WHERE check_date < :timestamp
        ) t WHERE t.rownum = 1
    """, nativeQuery = true)
    List<ProductPrice> findLatestPricesBefore(@Param("timestamp") Instant timestamp);
}
