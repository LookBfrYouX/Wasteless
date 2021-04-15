package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDaoHibernateImpl implements ProductDao {


    @Override
    public void saveProduct(Product product) {

    }

    @Override
    public List<Product> getProducts(int businessId) {
        return null;
    }
}
