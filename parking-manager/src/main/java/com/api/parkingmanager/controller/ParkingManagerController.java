package com.api.parkingmanager.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	public ResponseEntity<List<Vaga>> listarVagas() {
		return ResponseEntity.status(HttpStatus.OK).body(parkingManagerService.findAll());
	}
	
	@GetMapping( "/{id}")
	public ResponseEntity<Object> buscarVaga(@PathVariable(value = "id") UUID id){
		Optional<Vaga> vagaOptional = parkingManagerService.findById(id);
		
		if (!vagaOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
		}
			return ResponseEntity.status(HttpStatus.OK).body(vagaOptional.get());
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
