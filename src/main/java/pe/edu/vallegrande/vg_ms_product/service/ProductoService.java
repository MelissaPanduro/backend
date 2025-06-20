package pe.edu.vallegrande.vg_ms_product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_product.model.ProductoModel;
import pe.edu.vallegrande.vg_ms_product.repository.ProductoRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Mono<ProductoModel> createProduct(ProductoModel product) {
        if (product.getExpiryDate() != null && product.getEntryDate() != null &&
            product.getExpiryDate().isBefore(product.getEntryDate())) {
            return Mono.error(new IllegalArgumentException("La fecha de caducidad no puede ser anterior a la fecha de entrada"));
        }
        return productoRepository.save(product);
    }

    public Flux<ProductoModel> getAllProducts() {
        return productoRepository.findAll();
    }

    public Mono<Void> deleteProduct(Long id) {
        return productoRepository.deleteById(id);
    }

    public Mono<ProductoModel> softDeleteProduct(Long id) {
        return productoRepository.findById(id)
            .flatMap(product -> {
                product.setStatus("I");
                return productoRepository.save(product);
            });
    }

    public Mono<ProductoModel> restoreProduct(Long id) {
        return productoRepository.findByIdAndStatus(id, "I")
            .flatMap(product -> {
                product.setStatus("A");
                return productoRepository.save(product);
            });
    }

    public Mono<ProductoModel> updateProduct(Long id, ProductoModel productDetails) {
        return productoRepository.findById(id)
            .flatMap(existingProduct -> {
                if (productDetails.getExpiryDate() != null && productDetails.getEntryDate() != null &&
                    productDetails.getExpiryDate().isBefore(productDetails.getEntryDate())) {
                    return Mono.error(new IllegalArgumentException("La fecha de caducidad no puede ser anterior a la fecha de entrada"));
                }

                existingProduct.setType(productDetails.getType());
                existingProduct.setDescription(productDetails.getDescription());
                existingProduct.setPackageWeight(productDetails.getPackageWeight());
                existingProduct.setStock(productDetails.getStock());
                existingProduct.setEntryDate(productDetails.getEntryDate());
                existingProduct.setExpiryDate(productDetails.getExpiryDate());
                existingProduct.setTypeProduct(productDetails.getTypeProduct());
                existingProduct.setStatus(productDetails.getStatus());

                return productoRepository.save(existingProduct);
            });
    }

    // Aumentar stock y cambiar estado si era inactivo
    public Mono<ProductoModel> increaseStock(Long id, int quantityAdded) {
        return productoRepository.findById(id)
            .flatMap(product -> {
                if (quantityAdded <= 0) {
                    return Mono.error(new IllegalArgumentException("La cantidad debe ser mayor que cero."));
                }

                int nuevoStock = product.getStock() + quantityAdded;
                product.setStock(nuevoStock);

                // Si el producto estaba inactivo por falta de stock, reactivarlo
                if ("I".equals(product.getStatus()) && nuevoStock > 0) {
                    product.setStatus("A"); // Activo si ya hay stock
                }

                return productoRepository.save(product);
            });
    }

    // Ajustar stock (positivo = aumentar, negativo = disminuir)
    public Mono<ProductoModel> adjustStock(Long id, int quantityChange) {
        return productoRepository.findById(id)
            .flatMap(product -> {
                if (quantityChange == 0) {
                    return Mono.just(product); // No hay cambios
                }

                int nuevoStock = product.getStock() + quantityChange;

                if (nuevoStock < 0) {
                    return Mono.error(new IllegalArgumentException("Stock insuficiente para realizar la operación."));
                }

                product.setStock(nuevoStock);

                // Actualizar estado según el stock
                if (nuevoStock == 0 && "A".equals(product.getStatus())) {
                    product.setStatus("I"); // Inactivo si ya no hay stock
                } else if (nuevoStock > 0 && "I".equals(product.getStatus())) {
                    product.setStatus("A"); // Activo si ya hay stock
                }

                return productoRepository.save(product);
            });
    }

    // Disminuir stock específico (reduceStock)
    public Mono<ProductoModel> reduceStock(Long id, int quantity) {
        if (quantity <= 0) {
            return Mono.error(new IllegalArgumentException("La cantidad a reducir debe ser mayor que cero."));
        }
        return productoRepository.findById(id)
            .flatMap(product -> {
                int nuevoStock = product.getStock() - quantity;
                if (nuevoStock < 0) {
                    return Mono.error(new IllegalArgumentException("Stock insuficiente para reducir la cantidad solicitada."));
                }
                product.setStock(nuevoStock);

                // Actualizar estado si stock queda en 0
                if (nuevoStock == 0 && "A".equals(product.getStatus())) {
                    product.setStatus("I"); // Inactivo si ya no hay stock
                }

                return productoRepository.save(product);
            });
    }
}
