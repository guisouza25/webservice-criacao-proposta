package br.com.zupacademy.proposta.proposta;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zupacademy.proposta.shared.Log;

@RestController
@Validated	
public class PropostaController {
	
	@PersistenceContext
	private EntityManager manager;
	
	private final Logger logger = LoggerFactory.getLogger(Log.class);
	
	@PostMapping("/proposta/nova")
	@Transactional
	public ResponseEntity<?> cadastrar(
			@RequestBody @Valid PropostaRequest propostaRequest,
			UriComponentsBuilder uriBuilder) {
		
		Proposta proposta = propostaRequest.converter();
		manager.persist(proposta);
		
		logger.info("Proposta crida");
		
		URI uri = uriBuilder.path("/proposta/{id}").buildAndExpand(proposta.getId()).toUri();
		
		return ResponseEntity.created(uri).body(new PropostaResponse(proposta));
	}
	
	@GetMapping("/proposta/{id}") 
	public	 ResponseEntity<?> detalhar(
			@Pattern(regexp = "[\\s]*[0-9]*[1-9]+",message="ID da proposta inválido. Informe um valor numérico positivo")
			@PathVariable("id") 
			String id) {
			
		Proposta proposta = manager.find(Proposta.class, Long.valueOf(id));
		
		if(proposta.equals(null)) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(new PropostaResponse(proposta));
		}
	}
}
