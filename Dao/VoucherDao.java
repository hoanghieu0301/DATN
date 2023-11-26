package com.poly.dao;

import com.poly.entity.Post;
import com.poly.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoucherDao extends JpaRepository<Voucher, Integer>{
    Voucher findByVoucherName(String voucherName);
    @Query(value = "select * from Vouchers where Voucher_name like %:kw%"  , nativeQuery = true)
    List<Voucher> finbyIdOrName(@Param("kw") String keyword);
}
