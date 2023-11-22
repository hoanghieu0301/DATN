package com.poly.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.dao.AccountDao;
import com.poly.dao.OrderDao;
import com.poly.dao.PostDao;
import com.poly.dao.ProductDao;
import com.poly.dao.ReportDao;
import com.poly.dao.ReportProductbyDayDao;
import com.poly.entity.Account;
import com.poly.entity.Order;
import com.poly.entity.Product;
import com.poly.entity.Report;
import com.poly.entity.ReportProductbyDay;
import com.poly.service.AccountService;
import com.poly.service.ReportService;


@Controller
public class HomeAdminController{
	@Autowired
	ProductDao pdao;
	@Autowired
	AccountService accservice;
	@Autowired
	ReportDao rDao;
	@Autowired
	OrderDao odao;
	@Autowired
	AccountDao adao;
	@Autowired
	ReportService reportdao;
	@Autowired
	ReportProductbyDayDao pReportProductbyDayDao;
	@Autowired
	PostDao postDao;
	
	
	AccountDao dao;


		@RequestMapping("/admin/char/charyear")
		public String charyear(Model model, @RequestParam("year") int year) { 
			 Map<String,Double> barChartData = new TreeMap<>();
   		  Map<String,Long> barChartData1 = new TreeMap<>();
   		List<Report> list = rDao.revenueByyear(year);
   		for(int i=0 ; i<list.size();i++) {
        	barChartData.put(list.get(i).getGroup().toString(),list.get(i).getSum());
        }
        for(int i=0 ; i<list.size();i++) {
        	barChartData1.put(list.get(i).getGroup().toString(),list.get(i).getCount());
        
        }
        model.addAttribute("barChartData", barChartData);
        model.addAttribute("barChartData1", barChartData1);
			
			
			model.addAttribute("items", list);
			return "admin/char/charyear";
		}

	
	
	
}
