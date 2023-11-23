package com.poly.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.poly.dtos.BillDTO;
import com.poly.entity.*;
import com.poly.utils.NumberFormater;
import com.poly.utils.PathUtils;
import com.poly.utils.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.dao.ProductDao;
import com.poly.dao.ReportProductbyDayDao;
import com.poly.service.ProductService;
import com.poly.service.ReportService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class ReportServiceImql implements ReportService {
	
	@Autowired
	ReportService reportdao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	ReportProductbyDayDao reportproductbydaydao;

	@Override
	public List<ReportCategory> getReportCategory() {
		return productDao.getReportCategory();
	}

	@Override
	public List<ReportProductbyDay> getReportProductbyDaynoMinMax() {
		// TODO Auto-generated method stub
		return reportproductbydaydao.reportProdctByDaynoMinMax();
	}

	@Override
	public List<ReportProductbyDay> getReportProductbyDayMinMax(Date minday,Date Max) {
		// TODO Auto-generated method stub
		return reportproductbydaydao.reportProdctByDay( minday,  Max);
	}

	@Override
	public List<ReportCategory> revenueByMonth() {
		// TODO Auto-generated method stub
		return reportdao.revenueByMonth();
	}

	@Override
	public List<ReportTrademark> getReportTrademark() {
		// TODO Auto-generated method stub
		return productDao.getReportTrademark();
	}

	@Override
	public String createPdf(BillDTO billDTO) {
		Document document = new Document(PageSize.A4,15,15,45,30);//margin l,r,t,b
		try {
			String filePath =  new StringBuilder(PathUtils.getProjectRootPath()).append("/bills").toString();
			UUID randomUUID = UUID.randomUUID();
			File file = new File(filePath);
			boolean exists = new File(filePath).exists();
			if(!exists){
				new File(filePath).mkdirs();
			}
			String pdfPath = new StringBuilder("/").append("bill_").append(randomUUID).append(".pdf").toString();
			PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(file+pdfPath));
			document.open();
			BaseFont baseFontKey = null;
			try {
				String fontTextKeyPath = new StringBuilder(PathUtils.getProjectRootPath())
						.append("/src/main/resources/static/assets/fonts/Roboto-Light.ttf").toString();
				baseFontKey = BaseFont.createFont(fontTextKeyPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Font fontKey= new Font(baseFontKey, 12);
			Paragraph paragraph = new Paragraph("Thông Tin Hóa Đơn",fontKey);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			paragraph.setIndentationLeft(50);
			paragraph.setIndentationRight(50);
			paragraph.setSpacingAfter(6);
			document.add(paragraph);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String formattedDate = sdf.format(billDTO.getOrder().getCreateDate());

			Paragraph dateCreated = new Paragraph("Ngày tạo: " + formattedDate,fontKey);
			document.add(dateCreated);

//            main Info table
			Paragraph nameUser = new Paragraph("Tên khách hàng: "+billDTO.getOrder().getAccount().getUsername(),fontKey);
			document.add(nameUser);

			Paragraph address = new Paragraph("Địa chỉ: " + billDTO.getOrder().getAddress(),fontKey);
			document.add(address);

			Paragraph phone = new Paragraph("Số điện thoại: " + billDTO.getOrder().getPhone(),fontKey);
			document.add(phone);

			Paragraph totalAll = new Paragraph("Tổng tiền trước khi áp mã khuyến mại: " + NumberFormater.convertVND(billDTO.getOrder().getPrice()),fontKey);
			document.add(totalAll);

			Paragraph voucher = new Paragraph("Mã giảm giá: " + NumberFormater.convertVND(billDTO.getOrder().getVoucher_price()),fontKey);
			document.add(voucher);

			var totalAmount_ = billDTO.getOrder().getPrice() - billDTO.getOrder().getVoucher_price();
			Paragraph totalAmount = new Paragraph("Tổng tiền phải trả: " + NumberFormater.convertVND(totalAmount_),fontKey);
			document.add(totalAmount);

			Paragraph money_give = new Paragraph("Tiền nhận từ khách: " + NumberFormater.convertVND(billDTO.getOrder().getMoney_give()),fontKey);
			document.add(money_give);

			Paragraph money_send = new Paragraph("Tiền trả lại khách: " + NumberFormater.convertVND(billDTO.getOrder().getMoney_send()),fontKey);
			money_send.setSpacingAfter(20f);
			document.add(money_send);



			PdfPTable pTable = new PdfPTable(5);
			pTable.setWidthPercentage(100);
			pTable.setSpacingBefore(10f);
			pTable.setSpacingAfter(10f);

			float[] columnsWidth = {3f,2f,3f,1f,4f};
			pTable.setWidths(columnsWidth);

			//header columns
			PdfPCell Item = PdfUtils.createCell("Tên sản phẩm",fontKey);
			pTable.addCell(Item);

			PdfPCell Quantity = PdfUtils.createCell("Số lượng",fontKey);
			pTable.addCell(Quantity);

			PdfPCell Rate = PdfUtils.createCell("Giá",fontKey);
			pTable.addCell(Rate);

			PdfPCell Discount = PdfUtils.createCell("Giảm giá",fontKey);
			pTable.addCell(Discount);
			
			PdfPCell Amount = PdfUtils.createCell("Thành tiền",fontKey);
			pTable.addCell(Amount);

//              loop to add data row  in body
			for (OrderDetail orderDetail : billDTO.getOrderDetails()){
				PdfPCell ItemVal = PdfUtils.createCell(orderDetail.getProduct().getName(),fontKey);
				pTable.addCell(ItemVal);

				PdfPCell QuantityVal = PdfUtils.createCell(orderDetail.getQuantity().toString(),fontKey);
				pTable.addCell(QuantityVal);

				PdfPCell priceVal = PdfUtils.createCell(NumberFormater.convertVND(orderDetail.getPrice()),fontKey);
				pTable.addCell(priceVal);

				PdfPCell discountVal = PdfUtils.createCell(orderDetail.getProduct().getDistcount().toString() + " %",fontKey);
				pTable.addCell(discountVal);

				Double amount = (orderDetail.getPrice()*orderDetail.getQuantity())*(1-orderDetail.getProduct().getDistcount()/100);
				PdfPCell AmountVal = PdfUtils.createCell(NumberFormater.convertVND(amount),fontKey);
				pTable.addCell(AmountVal);
			}

//          Footer row

			PdfPCell blankCell1 = PdfUtils.createCell("",fontKey);
			pTable.addCell(blankCell1);
			PdfPCell blankCell2 = PdfUtils.createCell("",fontKey);
			pTable.addCell(blankCell2);
			PdfPCell blankCell3 = PdfUtils.createCell("",fontKey);
			pTable.addCell(blankCell3);
			PdfPCell total = PdfUtils.createCell("Tổng tiền: ",fontKey);
			pTable.addCell(total);

			PdfPCell totalVal = PdfUtils.createCell(NumberFormater.convertVND(billDTO.getOrder().getPrice()),fontKey);
			pTable.addCell(totalVal);
			pTable.setSpacingBefore(0);
			document.add(pTable);
			document.close();
			writer.close();
			return pdfPath;

		} catch (DocumentException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
