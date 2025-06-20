package pe.edu.vallegrande.vg_ms_product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_product.model.Sale;
import pe.edu.vallegrande.vg_ms_product.service.SaleService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController 
@RequestMapping("/sales") // Define la URL base para todos los endpoints de esta clase
@RequiredArgsConstructor // Genera automáticamente un constructor para inyectar dependencias final (como saleService)
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen (CORS)
public class SaleRest {

    private final SaleService saleService; // Servicio que maneja la lógica de negocio relacionada a ventas

    // Crear una nueva venta
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Sale> createSale(@RequestBody Sale sale) {
        return saleService.createSale(sale);
    }

    // Actualizar una venta existente por su ID
    @PutMapping("/{id}")
    public Mono<Sale> updateSale(@PathVariable Long id, @RequestBody Sale sale) {
        return saleService.updateSale(id, sale);
    }

    // Eliminar una venta por su ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSale(@PathVariable Long id) {
        return saleService.deleteSale(id);
    }

    // Obtener todas las ventas registradas
    @GetMapping
    public Flux<Sale> getAllSales() {
        return saleService.getAllSales();
    }

    // Obtener una venta específica por su ID
    @GetMapping("/{id}")
    public Mono<Sale> getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }

    // Buscar una venta por el número de documento (RUC o DNI)
    @GetMapping("/search-by-document/{document}")
    public Mono<Sale> getSaleByDocument(@PathVariable String document) {
        return saleService.getSaleByDocument(document);
    }
}
