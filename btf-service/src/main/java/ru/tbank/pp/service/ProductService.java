package ru.tbank.pp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.mapper.ProductMapper;
import ru.tbank.pp.model.ProductsProduct;
import ru.tbank.pp.repository.ProductRepository;
import ru.tbank.pp.repository.UserProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserProductRepository userProductRepository;
    private final UserService userService;
    private final ProductMapper productMapper;


    public List<ProductsProduct> getAllUserProducts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        Long userId = user.getId();

        List<UserProduct> userProducts = userProductRepository.findByUserId(userId);

        return userProducts.stream()
                .map(up -> productMapper.toProductsProduct(up.getProduct()))
                .toList();
    }
}
