package com.poly.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.PostDao;
import com.poly.entity.Account;
import com.poly.entity.Order;
import com.poly.entity.Post;
import com.poly.service.PostService;

@Controller
public class PostAdminController {
	@Autowired
	PostService postService;
	@Autowired
	PostDao postDao;
	
	@GetMapping("admin/post/list")
	public String index(Model model,HttpServletRequest request
			,RedirectAttributes redirect) {
		request.getSession().setAttribute("postlist", null);
		if(model.asMap().get("success") != null)
			redirect.addFlashAttribute("success",model.asMap().get("success").toString());
		return "redirect:/admin/post/list/page/1";
	}
	@GetMapping("/admin/post/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request,
			@PathVariable int pageNumber, Model model) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("postlist");
		int pagesize = 9;
		List<Post> list = postService.findAll();
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
		request.getSession().setAttribute("postlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/post/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/post/list";
	}
	@GetMapping("/admin/post/findIdorName")
	public String search(@RequestParam("keyword") String keyword, Model model) {
		if (keyword.equals("")) {
			return "redirect:/admin/post/list";
		}
		model.addAttribute("items", postDao.finbyIdOrName(keyword));
		return "list";
	}
	@RequestMapping("/admin/post/findIdorName/{pageNumber}")
	public String findIdorName(Model model ,  @RequestParam("keyword") String keyword, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (keyword.equals("")) {
			return "redirect:/admin/post/list";
		}
		List<Post> list = postDao.finbyIdOrName(keyword);
		if (list == null) {
			return "redirect:/admin/post/list";
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
		request.getSession().setAttribute("postlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/post/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/post/list";
	}
	@RequestMapping("/admin/post/findallkeyword")
	public String findallkeyword(Model model) {
		List<Post> list = postDao.findAll();
		model.addAttribute("items", list);
		return "list";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping("/admin/post/findByAllkeyword/{pageNumber}")
	public String findByAllkeyword(Model model,
			@RequestParam(value = "Minday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date minday,
			@RequestParam(value = "Maxday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date maxday,
			@RequestParam("Status") String Status,
			HttpServletRequest request, @PathVariable int pageNumber) {
		if (minday == (null)) {
			minday = new Date(1999 - 01 - 23);
		}
		if (maxday == (null)) {
			maxday = new Date();
		}

		List<Order> list = postDao.findByAllKeyWord( minday, maxday, Status);
		model.addAttribute("posts", list);
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("postlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("items", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/post/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/post/list";

	}
	@RequestMapping("admin/post/edit")
	public String edit(Model model, @RequestParam("post_id") Integer post_id) {
		try {
			Post post = postDao.findById(post_id).get();
			
			model.addAttribute("post", post);
			model.addAttribute("message", "Hiện chi tiết bài viết thành công");
			return "admin/post/edit";
		} catch (Exception e) {
			model.addAttribute("message", "Hiện chi tiết bài viết thất bại");
			return "redirect:/admin/post/list";
		}
	}
	@RequestMapping("admin/post/update")
	public String update(Post post, Model model, @RequestParam("photo_img") MultipartFile file) throws IOException {
		if (postDao.existsById(post.getPost_id())) {
			File saveFile = postService.save(file, "/assets/images");
			
			String filename = file.getOriginalFilename();
		
			if (filename == "") {
				postService.update(post);
				model.addAttribute("message", "Cập nhập thành công");
				return "redirect:/admin/post/edit?post_id=" + post.getPost_id();
			} else {
				post.setImage(filename);
				postService.update(post);
				model.addAttribute("message", "Cập nhập thành công");
				return "redirect:/admin/post/edit?post_id=" + post.getPost_id();
			}
		} else {
			model.addAttribute("message", "Cập nhập thất bại");
			return "redirect:/admin/post/edit?post_id=" + post.getPost_id();
		}
	}
	@RequestMapping("admin/post/delete/form/{post_id}")
	public String deleteform(Model model, @PathVariable("post_id") Integer post_id) {
		try {
			postDao.deleteById(post_id);
			model.addAttribute("message", "Xoá thành công");
			return "redirect:/admin/post/list";
		} catch (Exception e) {
			model.addAttribute("message", "Xoá thất bại");
			return "redirect:/admin/post/list";
		}

	}
	@RequestMapping("/admin/post/delete/{post_id}")
	public String delete(Model model,@PathVariable("post_id") Integer post_id) {
		try {
			postDao.deleteById(post_id);
		model.addAttribute("message", "Xoá thành công");
		return "redirect:/admin/post/list";
		}
		catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("message", "Xóa thất bại ");
			return "redirect:/admin/post/list";
		}
		
	}
	
	@RequestMapping("/admin/post/create")
	public String create(Post post, Model model, @RequestParam("photo_img") MultipartFile file) throws IOException {
		File saveFile = postService.save(file, "assets/images");
	
		String filename1 = file.getOriginalFilename();
	
		if (filename1 == "") {
	
			postDao.save(post);
			model.addAttribute("message", "Thêm mới thành công");
			return "redirect:/account/post";
		} else {
			post.setImage(filename1);
	
	
			postDao.save(post);
			model.addAttribute("message", "Thêm mới thành công");
			return "redirect:/admin/post/detail";
		}
	}
	@RequestMapping("/admin/post/detail")
	public String detail(Model model) {
		Post post = new Post();
		post.setPost_Name("");
		post.setImage("");
		post.setPost_date( new Date());
		post.setStatus(true);
		post.setPost_conten("");
	
	
		
		model.addAttribute("post", post);
		return "admin/post/detail";
	}
}
