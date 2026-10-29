package com.embarkx.shop.product;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/")
	public String home() {
		return "redirect:/products/1";
	}

	/** Unknown ids render templates/error/404.html. */
	@GetMapping("/products/{id}")
	public String product(@PathVariable String id, Model model) {
		Product product = this.productService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAttribute("product", product);
		return "product";
	}

}
