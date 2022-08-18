package com.api.parkingmanager.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingmanager.dto.VagaDTO;
import com.api.parkingmanager.model.Vaga;
import com.api.parkingmanager.service.ParkingManagerService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parkingmanager")
public class ParkingManagerController {

	ParkingManagerService parkingManagerService;

	VagaDTO vagaDTO;

	public ParkingManagerController(ParkingManagerService parkingManagerService) {
		this.parkingManagerService = parkingManagerService;
	}

	@PostMapping
	public ResponseEntity<Object> salvarVaga(@Valid @RequestBody VagaDTO vagaDTO) {

		if (parkingManagerService.existsByPlacaVeiculo(vagaDTO.getPlacaVeiculo())) {

			return ResponseEntity.status(HttpStatus.CONFLICT).body("Placa já cadastrada em outra vaga!");
		}

		if (parkingManagerService.existsByApartamentoAndBloco(vagaDTO.getApartamento(), vagaDTO.getBloco())) {

			return ResponseEntity.status(HttpStatus.CONFLICT).body("Os dados informados já pertencem a outra vaga!");
		}

		if (parkingManagerService.existsByNumero(vagaDTO.getNumero())) {

			return ResponseEntity.status(HttpStatus.CONFLICT).body("Vaga já em uso!");
		}

		var vaga = new Vaga();
		BeanUtils.copyProperties(vagaDTO, vaga);
		vaga.setDataRegistro(LocalDateTime.now(ZoneId.of("UTC")));

		return ResponseEntity.status(HttpStatus.CREATED).body(parkingManagerService.save(vaga));
	}

	@GetMapping
	public ResponseEntity<Page<Vaga>> listarVagas(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(parkingManagerService.findAll(pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> buscarVaga(@PathVariable(value = "id") UUID id) {
		Optional<Vaga> vagaOptional = parkingManagerService.findById(id);

		if (!vagaOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(vagaOptional.get());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable(value = "id") UUID id, 
			@RequestBody @Valid VagaDTO vagaDTO) {

		Optional<Vaga> vagaOptional = parkingManagerService.findById(id);
		if (vagaOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Vaga não pode ser atualizada pois não possui cadastrado.");

		}

		var vaga = vagaOptional.get();
		
		vaga.setNumero(vagaDTO.getNumero());
		vaga.setPlacaVeiculo(vagaDTO.getPlacaVeiculo());
		vaga.setModeloVeiculo(vagaDTO.getModeloVeiculo());
		vaga.setMarcaVeiculo(vagaDTO.getMarcaVeiculo());
		vaga.setCorVeiculo(vagaDTO.getCorVeiculo());
		vaga.setResponsavel(vagaDTO.getResponsavel());
		vaga.setApartamento(vagaDTO.getApartamento());
		vaga.setBloco(vagaDTO.getBloco());
		
		//segunda forma
		
		/*
		 * var vaga = new Vaga(); 
		 * BeanUtils.copyProperties(vagaDTO, vaga);
		 * vaga.setId(vagaOptional.get().getId());
		 * vaga.setDataRegistro(vagaOptional.get().getDataRegistro());
		 */
	
		return ResponseEntity.status(HttpStatus.OK).body(parkingManagerService.save(vaga));

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deletarVaga(@PathVariable(value = "id") UUID id) {

		Optional<Vaga> vagaOptional = parkingManagerService.findById(id);

		if (vagaOptional.isEmpty()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
		}
		parkingManagerService.delete(vagaOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Vaga excluída com sucesso.");
	}

}
