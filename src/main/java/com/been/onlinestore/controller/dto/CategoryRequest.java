package com.been.onlinestore.controller.dto;

import com.been.onlinestore.service.request.CategoryServiceRequest;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record CategoryRequest() {

    @Builder
    public record Create(
            @NotBlank @Size(max = 20)
            String name,
            @Length(max = 100)
            String description
    ) {

        public CategoryServiceRequest.Create toServiceRequest() {
            return CategoryServiceRequest.Create.of(
                    name,
                    description
            );
        }
    }

    @Builder
    public record Update(
            @Length(max = 20)
            String name,
            @Length(max = 100)
            String description
    ) {

        public CategoryServiceRequest.Update toServiceRequest() {
            return CategoryServiceRequest.Update.of(
                    name,
                    description
            );
        }
    }
}
