package br.com.zupacademy.webservice_proposta.proposta.controller;

import java.math.BigDecimal;

import br.com.zupacademy.webservice_proposta.proposta.EstadoProposta;
import br.com.zupacademy.webservice_proposta.proposta.Proposta;

public class PropostaResponse {
	
	private String documento;
	
	private String email;
	
	private String nome;
	
	private String endereco;
	
	private BigDecimal salario;
	
	private EstadoProposta estado;
	
	public PropostaResponse(Proposta proposta) {
		this.documento = proposta.getDocumento();
		this.email = proposta.getEmail();
		this.nome = proposta.getNome();
		this.endereco = proposta.getEndereco();
		this.salario = proposta.getSalario();
		this.estado = proposta.getEstado();
	}

	public String getDocumento() {
		return documento;
	}

	public String getEmail() {
		return email;
	}

	public String getNome() {
		return nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public BigDecimal getSalario() {
		return salario;
	}
	
	public EstadoProposta getEstado() {
		return estado;
	}
}
