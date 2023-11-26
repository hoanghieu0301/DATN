package com.poly.Service;

import com.poly.entity.VoucherDetail;

import java.util.List;

public interface VoucherDetailService {

    List<VoucherDetail> findAll();

    VoucherDetail findById(Integer id);

    VoucherDetail update(VoucherDetail voucher);

}
