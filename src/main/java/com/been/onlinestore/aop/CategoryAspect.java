package com.been.onlinestore.aop;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.been.onlinestore.service.CategoryService;
import com.been.onlinestore.service.response.CategoryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class CategoryAspect {

	private final CategoryService categoryService;

	@AfterReturning("execution(* com.been.onlinestore.controller.web..*.*(..))")
	public void getCategories(JoinPoint joinPoint) throws Throwable {
		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof Model model) {
				if (arg instanceof RedirectAttributesModelMap) {
					continue;
				}
				List<CategoryResponse> categories = categoryService.findCategoriesForUser();
				model.addAttribute("categories", categories);
				log.info("categories = {}", categories);
			}
		}
	}
}
