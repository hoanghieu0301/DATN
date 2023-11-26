package com.poly.service.impl;

import com.poly.dao.VoucherDao;
import com.poly.dao.VoucherDetailDao;
import com.poly.entity.Voucher;
import com.poly.entity.VoucherDetail;
import com.poly.service.VoucherDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherDetailServiceImpl implements VoucherDetailService {
	@Autowired
	VoucherDetailDao voucherDetailDao;

	@Override
	public List<VoucherDetail> findAll() {
		return voucherDetailDao.findAll();
	}

	@Override
	public VoucherDetail findById(Integer id) {
		return voucherDetailDao.findById(id).get();
	}

	@Override
	public VoucherDetail update(VoucherDetail voucher) {
		return voucherDetailDao.save(voucher);
	}


}
