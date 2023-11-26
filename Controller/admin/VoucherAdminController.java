package com.poly.controller.admin;

import com.poly.dao.PostDao;
import com.poly.dao.VoucherDao;
import com.poly.entity.Order;
import com.poly.entity.Post;
import com.poly.entity.Voucher;
import com.poly.service.PostService;
import com.poly.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class VoucherAdminController {
	@Autowired
	VoucherService voucherService;
	@Autowired
	VoucherDao voucherDao;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}


	@GetMapping("admin/voucher/list")
	public String index(Model model,HttpServletRequest request
			,RedirectAttributes redirect) {
		request.getSession().setAttribute("voucherlist", null);
		if(model.asMap().get("success") != null)
			redirect.addFlashAttribute("success",model.asMap().get("success").toString());
		return "redirect:/admin/voucher/list/page/1";
	}
	@GetMapping("/admin/voucher/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request,
			@PathVariable int pageNumber, Model model) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("voucherlist");
		int pagesize = 9;
		List<Voucher> list = voucherService.findAll();
		model.addAttribute("sizepro", list.size());
		if (pages == null) {
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
		} else {
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
		}
		request.getSession().setAttribute("voucherlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/voucher/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/voucher/list";
	}
	@GetMapping("/admin/voucher/findIdorName")
	public String search(@RequestParam("keyword") String keyword, Model model) {
		if (keyword.equals("")) {
			return "redirect:/admin/voucher/list";
		}
		model.addAttribute("items", voucherDao.finbyIdOrName(keyword));
		return "list";
	}
	@RequestMapping("/admin/voucher/findIdorName/{pageNumber}")
	public String findIdorName(Model model ,  @RequestParam("keyword") String keyword, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (keyword.equals("")) {
			return "redirect:/admin/voucher/list";
		}
		List<Voucher> list = voucherDao.finbyIdOrName(keyword);
		if (list == null) {
			return "redirect:/admin/voucher/list";
		}
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("postlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("voucherlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/voucher/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/voucher/list";
	}
	@RequestMapping("/admin/voucher/findallkeyword")
	public String findallkeyword(Model model) {
		List<Voucher> list = voucherDao.findAll();
		model.addAttribute("items", list);
		return "list";
	}

	@RequestMapping("admin/voucher/edit")
	public String edit(Model model, @RequestParam("voucher_id") Integer post_id) {
		try {
			Voucher post = voucherDao.findById(post_id).get();
			
			model.addAttribute("voucher", post);
			model.addAttribute("message", "Hiện chi tiết voucher thành công");
			return "admin/voucher/edit";
		} catch (Exception e) {
			model.addAttribute("message", "Hiện chi tiết voucher thất bại");
			return "redirect:/admin/voucher/list";
		}
	}
	@RequestMapping("admin/voucher/update")
	public String update(Voucher voucher, Model model) throws IOException {
		if (voucherDao.existsById(voucher.getVoucher_id())) {
			voucherService.update(voucher);
			model.addAttribute("message", "Cập nhập thành công");
			return "redirect:/admin/voucher/edit?voucher_id=" + voucher.getVoucher_id();
		} else {
			model.addAttribute("message", "Cập nhập thất bại");
			return "redirect:/admin/voucher/edit?voucher_id=" + voucher.getVoucher_id();
		}
	}
	@RequestMapping("admin/voucher/delete/form/{voucher_id}")
	public String deleteform(Model model, @PathVariable("voucher_id") Integer voucher_id) {
		try {
			voucherDao.deleteById(voucher_id);
			model.addAttribute("message", "Xoá thành công");
			return "redirect:/admin/voucher/list";
		} catch (Exception e) {
			model.addAttribute("message", "Xoá thất bại");
			return "redirect:/admin/voucher/list";
		}

	}
	@RequestMapping("/admin/voucher/delete/{voucher_id}")
	public String delete(Model model,@PathVariable("voucher_id") Integer voucher_id) {
		try {
			voucherDao.deleteById(voucher_id);
		model.addAttribute("message", "Xoá thành công");
		return "redirect:/admin/voucher/list";
		}
		catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("message", "Xóa thất bại ");
			return "redirect:/admin/voucher/list";
		}
		
	}
	
	@RequestMapping("/admin/voucher/create")
	public String create(Voucher voucher, Model model) throws IOException {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//		try {
//			Date stateDtate =  dateFormat.parse(voucher.getCreateDate().toString());
//			Date endDate =  dateFormat.parse(voucher.getEndDate().toString());
//			voucher.setCreateDate(stateDtate);
//			voucher.setEndDate(endDate);
//		} catch (ParseException e) {
//           e.printStackTrace();
//		}
		voucherDao.save(voucher);
		model.addAttribute("message", "Thêm mới thành công");
		return "redirect:/admin/voucher/detail";
	}
	@RequestMapping("/admin/voucher/detail")
	public String detail(Model model) {
		Voucher voucher = new Voucher();
		voucher.setVoucherName("");
		voucher.setVoucher_content("");
		voucher.setCreateDate( new Date());
		voucher.setEndDate( new Date());
		voucher.setStatus(true);
		model.addAttribute("voucher", voucher);
		return "admin/voucher/detail";
	}
}
