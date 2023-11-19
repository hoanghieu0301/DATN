package com.poly.service;

import java.util.Date;
import java.util.List;

import com.poly.dtos.BillDTO;
import com.poly.entity.Product;
import com.poly.entity.ReportCategory;
import com.poly.entity.ReportProductbyDay;
import com.poly.entity.ReportTrademark;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ReportService {
	
	List<ReportCategory> getReportCategory() ;
	
	List<ReportProductbyDay> getReportProductbyDaynoMinMax();
	List<ReportProductbyDay> getReportProductbyDayMinMax(Date minday , Date maxday);

	List<ReportCategory> revenueByMonth();

	List<ReportTrademark> getReportTrademark();
	String createPdf(BillDTO billDTO);
}
