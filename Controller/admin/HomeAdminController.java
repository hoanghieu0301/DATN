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
	@RequestMapping("/admin/home/index")
	public String home(Model model, @RequestParam("page") Optional<Integer> page ) { 
		model.addAttribute("order", odao.getTop10());
		model.addAttribute("protop10", pReportProductbyDayDao.reportProdctTop10());
		int x = page.orElse(0);
		model.addAttribute("page", x);
		int size = 10;
		model.addAttribute("usetop10", rDao.Top10User(PageRequest.of(x, size)));
		model.addAttribute("prosize", pdao.findAll().size());
		model.addAttribute("accsize", adao.findAll().size());
		model.addAttribute("ordersize", odao.findAll().size());
		model.addAttribute("postsize", postDao.findAll().size());
		return "admin/layout/home";
	}
	@RequestMapping("/admin/look")
	public String look() { 
		return "admin/security/look";
	}
	@RequestMapping("/admin/authority")
	public String authority() { 
		return "admin/authority/authority";
	}
		@RequestMapping("/admin/map")
		public String map() { 
			return "admin/layout/map";
		}
		@RequestMapping("/admin/char/chartrak")
		public String chartrak(Model model) {
			model.addAttribute("items", rDao.revenueByTrak());
			return "admin/char/chartrak";
		}

}
