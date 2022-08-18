package com.api.parkingmanager.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.parkingmanager.model.Vaga;
import com.api.parkingmanager.repository.ParkinManagerRepository;

@Service
public class ParkingManagerService {

	final ParkinManagerRepository parkinManagerRepository;

	public ParkingManagerService(ParkinManagerRepository parkinManagerRepository) {
		this.parkinManagerRepository = parkinManagerRepository;
	}

	public boolean existsByPlacaVeiculo(String plavaVeiculo) {
		return parkinManagerRepository.existsByPlacaVeiculo(plavaVeiculo);
	}

	public boolean existsByNumero(String numero) {
		return parkinManagerRepository.existsByNumero(numero);
	}

	public boolean existsByApartamentoAndBloco(String apartamento, String bloco) {
		return parkinManagerRepository.existsByApartamentoAndBloco(apartamento, bloco);
	}

	@Transactional
	public Vaga save(Vaga vaga) {
		return parkinManagerRepository.save(vaga);
	}

	public Page<Vaga> findAll(Pageable pageable) {

		return parkinManagerRepository.findAll(pageable);
	}

	public Optional<Vaga> findById(UUID id) {

		return parkinManagerRepository.findById(id);
	}

	@Transactional
	public void delete(Vaga vaga) {
		 parkinManagerRepository.delete(vaga);
	}

}
