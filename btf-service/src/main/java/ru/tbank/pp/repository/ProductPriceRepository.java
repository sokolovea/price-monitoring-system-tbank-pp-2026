package ru.tbank.pp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.ProductPriceId;

import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, ProductPriceId> {

    List<ProductPrice> findByProductIdOrderByIdCheckDateDesc(Long productId);
}
