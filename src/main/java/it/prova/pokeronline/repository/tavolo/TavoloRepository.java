package it.prova.pokeronline.repository.tavolo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.prova.pokeronline.model.Tavolo;

public interface TavoloRepository
		extends PagingAndSortingRepository<Tavolo, Long>, JpaSpecificationExecutor<Tavolo>, CustomTavoloRepository {
	
	@Query("from Tavolo t left join fetch t.giocatori left join fetch t.utenteCreazione where t.id = :id")
	Tavolo findByIdEager(@Param("id") Long idTavolo);

	List<Tavolo> findByEsperienzaMinLessThan(Double esperienza);

	Tavolo findByGiocatoriId(Long id);

	@Query(value = "SELECT t.* FROM tavolo t "
			+ "WHERE ((:denominazione IS NULL OR LOWER(t.denominazione) LIKE %:denominazione%)  "
			+ "AND (:esperienzaMin IS NULL OR t.esperienzaMin > :esperienzaMin) "
			+ "AND (:cifraMin IS NULL OR t.cifraMin >= :cifraMin) "
			+ "AND (:datacreazione IS NULL OR t.datacreazione >= :datacreazione) "
			+ "AND (:username IS NULL OR LOWER(t.utentecreazione.username) LIKE %:username%))", countQuery = "SELECT t.* "
					+ "FROM tavolo t "
					+ "WHERE ((:denominazione IS NULL OR LOWER(t.denominazione) LIKE %:denominazione%)  "
					+ "AND (:esperienzaMin IS NULL OR t.esperienzaMin > :esperienzaMin) "
					+ "AND (:cifraMin IS NULL OR t.cifraMin >= :cifraMin) "
					+ "AND (:datacreazione IS NULL OR t.datacreazione >= :datacreazione) "
					+ "AND (:username IS NULL OR LOWER(t.utentecreazione.username) LIKE %:username%))", nativeQuery = true)
	Page<Tavolo> findByExampleNativeWithPagination(@Param("denominazione") String denominazione,
			@Param("esperienzaMin") Double esperienzaMin, @Param("cifraMin") Double cifraMin,
			@Param("datacreazione") LocalDate dataCreazione, @Param("username") String username, Pageable pageable);
}