package ZGS.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.ProductDto;
import ZGS.backend.entity.Product;
import ZGS.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor

public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<Product>>> getAll() {
        List<Product> productList = productService.getAll();
        return ResponseEntity.ok(BaseResponse.ok(productList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Product>> getById(@PathVariable Long id) {
        Product product = productService.getById(id)
                .orElseThrow(() -> new RuntimeException("找不到該產品"));
        return ResponseEntity.ok(BaseResponse.ok(product));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Product>> create(@Valid @RequestBody ProductDto dto) {
        Product created = productService.create(dto);
        return ResponseEntity.ok(BaseResponse.ok(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Product>> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        Product updated = productService.update(id, dto);
        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("產品刪除成功"));
    }

    @GetMapping("/paged")
    public ResponseEntity<BaseResponse<Page<Product>>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Product> productPage = productService.getPaged(PageRequest.of(page, size));
        return ResponseEntity.ok(BaseResponse.ok(productPage));
    }

}