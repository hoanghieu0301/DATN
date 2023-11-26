package com.poly.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@Table(name = "VoucherDetails")
public class VoucherDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Voucher_detail_id")
    Integer voucher_detail_id;
    @Column(name = "Voucher_id")
    Integer voucher_id;
    @Column(name = "Order_id")
    Integer order_id;

}
