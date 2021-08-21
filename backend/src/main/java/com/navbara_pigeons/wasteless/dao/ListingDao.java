package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ListingDao extends ListingDaoHibernate, JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

}
