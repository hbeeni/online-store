package com.been.onlinestore.aop;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

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

	@Around("execution(* com.been.onlinestore.controller.web..*.*(..))")
	public Object showCategories(ProceedingJoinPoint joinPoint) throws Throwable {
		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof Model model) {
				List<CategoryResponse> categories = categoryService.findCategoriesForUser();
				model.addAttribute("categories", categories);
				log.info("categories = {}", categories);
			}
		}
		return joinPoint.proceed();
	}
}
