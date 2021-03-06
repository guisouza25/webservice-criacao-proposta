package br.com.zupacademy.webservice_propostas.proposta.controller;

import java.math.BigDecimal;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.zupacademy.webservice_propostas.proposta.DocumentoLimpo;
import br.com.zupacademy.webservice_propostas.proposta.Proposta;
import br.com.zupacademy.webservice_propostas.proposta.ValidaCPFOuCNPJ;
import br.com.zupacademy.webservice_propostas.shared.validators.UniqueValue;

public class PropostaRequest {
	
	@NotBlank @ValidaCPFOuCNPJ
	@UniqueValue(domainClass = Proposta.class, fieldName = "documento")
	private String documento;
	
	@NotBlank @Email
	private String email;
	
	@NotBlank
	private String nome;
	
	@NotBlank
	private String endereco;
	
	@NotNull @Positive
	private BigDecimal salario;
	
	public PropostaRequest(@NotEmpty @NotBlank String documento, @NotEmpty @NotBlank @Email String email,
			@NotEmpty @NotBlank String nome, @NotEmpty @NotBlank String endereco, @Positive BigDecimal salario) {
		this.documento = documento;
		this.email = email;
		this.nome = nome;
		this.endereco = endereco;
		this.salario = salario;
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

	public Proposta converter() {
		return new Proposta(new DocumentoLimpo(documento), email, nome, endereco, salario);
	}
	
	
}
