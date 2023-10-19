package com.poly.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.poly.entity.Post;

public interface PostService {

	List<Post> findAll();

	Post findById(Integer id);

	List<Post> findAllStatus();

	File save(MultipartFile file, String string) throws IOException;

	Post update(Post post);

//	List<Post> findAllStatus();

}
