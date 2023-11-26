package com.poly.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "Vouchers")
public class Voucher implements Serializable {
    /*
    *  Nguời dùng có thể nhận voucher bằng nhiều hình thức , ví dụ như được phát trực tiếp, xem ứng dụng nhận mã giảm giá
    * Sau khi nhận được voucher người dùng có thể nhập để sử dụng nó khi thanh toán hóa đơn.
    * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Voucher_id")
    Integer voucher_id;

    @Column(name = "Voucher_name")
    String voucherName;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "Createdate")
    Date createDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "Enddate")
    Date endDate = new Date();

    @Column(name = "Voucher_price")
    Double voucher_price;

    @Column(name = "Voucher_content")
    String voucher_content;

    @Column(name = "Status")
    boolean status;

    @Column(name = "username")
    String username;

    @Column(name = "estimate")
    Double estimate;
}