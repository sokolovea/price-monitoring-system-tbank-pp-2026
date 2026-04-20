package ru.tbank.pp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.entity.UserProductId;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, UserProductId> {
    List<UserProduct> findByUserId(Long userId);

    //Даже не представляю как это сделать не sql запросом
    @Query("SELECT up.id.userId " +
            "FROM UserProduct up " +
            "WHERE up.id.productId = :productId " +
                "AND :price <= up.thresholdPrice " +
                "AND up.notify = true")
    List<Long> findUserIdsForNotification(@Param("productId") Long productId, @Param("price") BigDecimal price);
}
