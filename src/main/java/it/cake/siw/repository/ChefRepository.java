package it.cake.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.cake.siw.model.Chef;

@Repository
public interface ChefRepository extends CrudRepository<Chef, Long>{
	
	public boolean existsByNameAndSurname(String name, String surname);	

	@Query(value = "SELECT name FROM President WHERE id NOT IN " +
	        "(SELECT id FROM Team WHERE id = :teamId)",  nativeQuery=true)
	Iterable<Chef> findPresidentsNotInTeam(@Param("teamId") Long teamId);
	Optional<Chef> findByUsername(String username);

	public Optional<Chef> findFirstByOrderById();
}

