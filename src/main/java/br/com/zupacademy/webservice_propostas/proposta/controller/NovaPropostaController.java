package br.com.zupacademy.webservice_propostas.proposta.controller;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zupacademy.webservice_propostas.config.metrics.CustomMetrics;
import br.com.zupacademy.webservice_propostas.proposta.EstadoProposta;
import br.com.zupacademy.webservice_propostas.proposta.Proposta;
import br.com.zupacademy.webservice_propostas.proposta.analisefinanceira_client.AnaliseFinanceiraClient;
import br.com.zupacademy.webservice_propostas.proposta.analisefinanceira_client.ResultadoAnalise;
import br.com.zupacademy.webservice_propostas.proposta.analisefinanceira_client.SolicitacaoAnalise;
import br.com.zupacademy.webservice_propostas.shared.ExecutorTransacao;
import br.com.zupacademy.webservice_propostas.shared.Log;
import br.com.zupacademy.webservice_propostas.shared.exceptionhandler.Erro;
import br.com.zupacademy.webservice_propostas.shared.exceptionhandler.ErroFormulario;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Validated
@RequestMapping("/propostas")
public class NovaPropostaController {
	
	@PersistenceContext private EntityManager manager;
	@Autowired private ExecutorTransacao transaction;
	@Autowired private AnaliseFinanceiraClient analiseFinanceiraClient;
	@Autowired private CustomMetrics metrics;
	@Autowired private Tracer tracer;
	
	private final Logger logger = LoggerFactory.getLogger(Log.class);
	
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Proposta criada"),
		@ApiResponse(responseCode = "422", description = "Documento duplicado", 
				content = { @Content(schema = @Schema(implementation = ErroFormulario.class)) })
	})
	@PostMapping
	@CacheEvict(cacheNames = "listaDePropostas", allEntries = true)
	public ResponseEntity<?> novaProposta(
			@RequestBody @Valid PropostaRequest propostaRequest,
			UriComponentsBuilder uriBuilder) {
		
		Proposta proposta = propostaRequest.converter();
		transaction.salvaEComita(proposta);
		
		Span activeSpan = tracer.activeSpan();
		activeSpan.setBaggageItem("user.email", proposta.getEmail());
		
		try {
			ResultadoAnalise resultadoAnalise = analiseFinanceiraClient.solicitaAnalise(new SolicitacaoAnalise(proposta));
			EstadoProposta estadoProposta = resultadoAnalise.getEstadoProposta();

			proposta.alteraEstado(estadoProposta);
			
		} catch (FeignClientException e) {
			proposta.alteraEstado(EstadoProposta.NAO_ELEGIVEL);
			
		} catch (FeignServerException e) {
			transaction.removeEComita(proposta);
			return ResponseEntity.status(e.status()).body(new Erro("Houve um erro no processamento do sistema. Tente novamente."));
		}
		transaction.atualizaEComita(proposta);
		
		logger.info("Proposta criada");
		metrics.contadorPropostas();
		
		URI uri = uriBuilder.path("/propostas/{id}").buildAndExpand(proposta.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
}
