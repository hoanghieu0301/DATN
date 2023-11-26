package com.poly.service.impl;

import com.poly.dao.PostDao;
import com.poly.dao.VoucherDao;
import com.poly.entity.Post;
import com.poly.entity.Voucher;
import com.poly.service.PostService;
import com.poly.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class VoucherServiceImpl implements VoucherService {
	@Autowired
	VoucherDao voucherDao;

	@Override
	public List<Voucher> findAll() {
		return voucherDao.findAll();
	}

	@Override
	public Voucher findById(Integer id) {
		return voucherDao.findById(id).get();
	}

	@Override
	public Voucher update(Voucher voucher) {
		voucher.setStatus(true);
		return voucherDao.save(voucher);
	}

	@Override
	public void deleteById(int id) {
		voucherDao.deleteById(id);
	}

	@Override
	public Voucher findByVoucherName(String voucherName) {
		return voucherDao.findByVoucherName(voucherName);
	}

	@Override
	public Integer checkIsvalidVoucher(String code,Double total) {
		if(code.equals("undefined") || code.equals("")) return 1000;
		Voucher voucher= voucherDao.findByVoucherName(code);
		if(Objects.isNull(voucher)){
			return 1001; //  Voucher không tồn tại.
		}else{
			Date currentDate = new Date();
			if (!(currentDate.after(voucher.getCreateDate()) && currentDate.before(voucher.getEndDate()))) {
				return 1002; // Voucher hết hạn
			}else if(!voucher.isStatus()){
				return 1003;  // Voucher không hợp lệ
			}else if(voucher.getEstimate()!=null){
				if(total<voucher.getEstimate()) return  1004; // Số tiền đơn hàng ko đủ để áp dụng
			}

			return 1000; // Voucher hợp lệ
		}

	}


}
