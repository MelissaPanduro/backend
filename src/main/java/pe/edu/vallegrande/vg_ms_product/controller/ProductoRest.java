package pe.edu.vallegrande.vg_ms_product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_product.model.ProductoModel;
import pe.edu.vallegrande.vg_ms_product.service.ProductoService;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/NPH/products")
@CrossOrigin(origins = "*")
public class ProductoRest {

    @Autowired
    private ProductoService productoService;

    // Crear un nuevo producto
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductoModel> createProduct(@RequestBody ProductoModel product) {
        return productoService.createProduct(product);
    }

    // Obtener todos los productos (activos e inactivos, dependiendo de la lógica en el service)
    @GetMapping
    public Flux<ProductoModel> getAllProducts() {
        return productoService.getAllProducts();
    }

    // Eliminar físicamente un producto por su ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productoService.deleteProduct(id);
    }

    // Eliminación lógica del producto (cambia su estado a inactivo)
    @PutMapping("/logic/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductoModel> softDeleteProduct(@PathVariable Long id) {
        return productoService.softDeleteProduct(id);
    }

    // Restaurar un producto previamente eliminado lógicamente (cambia su estado a activo)
    @PutMapping("/restore/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductoModel> restoreProduct(@PathVariable Long id) {
        return productoService.restoreProduct(id);
    }

    // Actualizar los datos de un producto existente
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductoModel> updateProduct(@PathVariable Long id, @RequestBody ProductoModel productDetails) {
        return productoService.updateProduct(id, productDetails);
    }

    // Aumentar el stock de un producto; si el producto está inactivo, se puede activar
    @PutMapping("/increase-stock/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductoModel> increaseStock(@PathVariable Long id, @RequestParam int quantity) {
        return productoService.increaseStock(id, quantity);
    }

    // Ajustar el stock (puede ser aumento o reducción) según el valor recibido
    @PutMapping("/adjust-stock/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductoModel> adjustStock(@PathVariable Long id, @RequestParam int quantityChange) {
        return productoService.adjustStock(id, quantityChange);
    }

    // Disminuir el stock de un producto en una cantidad específica
    @PutMapping("/reduce-stock/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductoModel> reduceStock(@PathVariable Long id, @RequestParam int quantity) {
        return productoService.reduceStock(id, quantity);
    }

}
