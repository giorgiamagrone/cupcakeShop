package it.cake.siw.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.cake.siw.model.Cupcake;

@Repository
public interface CupcakeRepository extends CrudRepository<Cupcake, Long> {

    // Metodo per verificare l'esistenza di un Cupcake con lo stesso gusto e prezzo
    public boolean existsByNameAndPrice(String name, Double price);

    // Query per trovare un Cupcake per ID (esempio con un campo aggiuntivo, se necessario in futuro)
    @Query("SELECT c FROM Cupcake c WHERE c.id = :id")
    Optional<Cupcake> findByIdWithDetails(@Param("id") Long id);

    // Query per cercare i Cupcake con un determinato gusto
    List<Cupcake> findByName(String name);
    
    // Query per trovare Cupcake con un prezzo massimo
    @Query("SELECT c FROM Cupcake c WHERE c.price <= :price")
    List<Cupcake> findCupcakesByMaxPrice(@Param("price") Double price);
    boolean existsByNameAndPrice(String name, double price);
}
