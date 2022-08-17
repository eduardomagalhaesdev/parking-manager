package com.api.parkingmanager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.parkingmanager.model.Vaga;

@Repository
public interface ParkinManagerRepository extends JpaRepository<Vaga, UUID> {

	public boolean existsByPlacaVeiculo(String placaVeiculo);
	public boolean existsByNumero(String numero);
	public boolean existsByApartamentoAndBloco(String apartamento, String bloco);
}
