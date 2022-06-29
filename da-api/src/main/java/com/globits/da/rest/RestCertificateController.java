package com.globits.da.rest;

import com.globits.da.dto.CertificateDto;
import com.globits.da.dto.search.CertificateSearchDto;
import com.globits.da.service.CertificateService;
import com.globits.da.utils.ErrorMessage;
import com.globits.da.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates")
public class RestCertificateController {

    @Autowired
    CertificateService certificateService;

    @GetMapping
    public Response<List<CertificateDto>> getAll() {
        return certificateService.getAll();
    }

    private Response<CertificateDto> handleError(BindingResult bindingResult) {
        String fieldError = bindingResult.getFieldError().getField();
        if(fieldError.equals("code")) {
            return new Response<>(
                    null,
                    ErrorMessage.CODE_IS_NULL.getCode(),
                    ErrorMessage.CODE_IS_NULL.getMessage()
            );
        }
        return new Response<>(
                null,
                ErrorMessage.NAME_IS_NULL.getCode(),
                ErrorMessage.NAME_IS_NULL.getMessage()
        );
    }

    @PostMapping
    public Response<CertificateDto> create(
            @RequestBody @Valid CertificateDto dto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return handleError(bindingResult);
        }

        return certificateService.save(dto);
    }

    @PutMapping("/{id}")
    public Response<CertificateDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid CertificateDto dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return handleError(bindingResult);
        }

        return certificateService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> delete(@PathVariable UUID id) {
        return certificateService.deleteById(id);
    }

    @GetMapping("/{pageIndex:[\\d]+}/{pageSize:[\\d]+}")
    public Response<Page<CertificateDto>> getPage(
            @PathVariable int pageIndex, @PathVariable int pageSize
    ) {
        return certificateService.getPage(pageIndex, pageSize);
    }

    @PostMapping("/search")
    public Response<Page<CertificateDto>> search(@RequestBody CertificateSearchDto dto) {
        return certificateService.search(dto);
    }
}
