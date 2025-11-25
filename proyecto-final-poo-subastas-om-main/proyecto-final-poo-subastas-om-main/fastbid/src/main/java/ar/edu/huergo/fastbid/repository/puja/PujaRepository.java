package ar.edu.huergo.fastbid.repository.puja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.fastbid.entity.subastas.Puja;

@Repository
public interface PujaRepository extends JpaRepository<Puja, Long> {
}
