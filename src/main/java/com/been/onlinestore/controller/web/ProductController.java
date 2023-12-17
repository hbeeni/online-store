package com.been.onlinestore.controller.web;

import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/products")
@Controller
public class ProductController {

	private final ProductService productService;
	private final ImageStore imageStore;

	@GetMapping
	public String getProductsOnSale(
		@RequestParam(required = false) String searchName,
		@PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
		Model model
	) {
		model.addAttribute("products", productService.findProductsOnSaleForUser(searchName, pageable));
		return "user/products";
	}

	@GetMapping(value = "/img/{imageName}")
	public Resource getProductImage(@PathVariable String imageName) throws MalformedURLException {
		return new UrlResource("file:" + imageStore.getFullPath(imageName));
	}
}
