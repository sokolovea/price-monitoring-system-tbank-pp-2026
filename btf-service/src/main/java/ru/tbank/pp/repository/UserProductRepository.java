package ru.tbank.pp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.entity.UserProductId;

import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, UserProductId> {
    List<UserProduct> findByUserId(Long userId);
}
