package com.poly.service;

import com.poly.entity.Voucher;
import java.util.List;

public interface VoucherService {

	List<Voucher> findAll();

	Voucher findById(Integer id);

	Voucher update(Voucher voucher);

	void  deleteById(int id);

	Voucher findByVoucherName(String voucherName);

	Integer checkIsvalidVoucher(String code,Double total);
}
