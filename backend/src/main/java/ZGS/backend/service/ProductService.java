//CRUD
package ZGS.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ZGS.backend.dto.ProductDto;
import ZGS.backend.entity.Product;
import ZGS.backend.mapper.ProductMapper;
import ZGS.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // 獲取
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // 查找
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    // 創建
    public Product create(ProductDto dto) {
        Product product = productMapper.toEntity(dto);
        return productRepository.save(product);

    }

    // 更新
    public Product update(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productMapper.updateEntityFormDto(dto, product);
        return productRepository.save(product);
    }

    // 刪除
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> getPaged(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}