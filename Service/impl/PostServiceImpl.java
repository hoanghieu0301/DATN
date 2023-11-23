package com.poly.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.poly.dao.PostDao;
import com.poly.entity.Post;
import com.poly.service.PostService;

@Service
public class PostServiceImpl implements PostService{
	@Autowired
	PostDao postDao;
	@Autowired
	HttpServletRequest request;
	@Override
	public List<Post> findAll() {
		// TODO Auto-generated method stub
		return postDao.findAll();
	}

	@Override
	public Post findById(Integer id) {
		// TODO Auto-generated method stub
		return postDao.findById(id).get();
	}

	@Override
	public List<Post> findAllStatus() {
		// TODO Auto-generated method stub
		return postDao.findAllStatus();
	}
	@Override
	public File save(MultipartFile file, String path) throws IOException {
		if(!file.isEmpty()) {
			String path1 = "E:\\netbean\\GoalShop\\src\\main\\resources\\static";
			File directory = new File(path1 + path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String fileName = file.getOriginalFilename();
			File f = new File(directory , fileName);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(f));
			byte[] data = file.getBytes();
			bufferedOutputStream.write(data);
			bufferedOutputStream.close();
			
			File dir = new File(request.getServletContext().getRealPath(path));
			if(!dir.exists()) {
				dir.mkdirs();
			}
			try {
				File savedFile = new File(dir, file.getOriginalFilename());
				file.transferTo(savedFile);
				return savedFile;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public Post update(Post post) {
		// TODO Auto-generated method stub
		return postDao.save(post);
	}


}
