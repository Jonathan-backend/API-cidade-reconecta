package br.com.reconecta.service;

import br.com.reconecta.model.Categoria;
import br.com.reconecta.model.InformacaoUrbana;
import br.com.reconecta.repository.RepositorioInformacao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ServicoInformacao {
    private final RepositorioInformacao repositorio;

    public ServicoInformacao(RepositorioInformacao repositorio) {
        this.repositorio = repositorio;
    }

    public InformacaoUrbana cadastrar(InformacaoUrbana dados) {
        InformacaoUrbana informacao = new InformacaoUrbana();
        copiarDados(dados, informacao);
        return repositorio.save(informacao);
    }

    @Transactional(readOnly = true)
    public List<InformacaoUrbana> listar(Categoria categoria) {
        if (categoria == null) {
            return repositorio.findAll(Sort.by("identificador"));
        }
        return repositorio.listarPorCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public InformacaoUrbana buscarPorIdentificador(Long identificador) {
        Optional<InformacaoUrbana> resultado = repositorio.findById(identificador);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Informação urbana não encontrada.");
        }
        return resultado.get();
    }

    public InformacaoUrbana atualizar(Long identificador, InformacaoUrbana dados) {
        InformacaoUrbana informacao = buscarPorIdentificador(identificador);
        copiarDados(dados, informacao);
        return repositorio.save(informacao);
    }

    public void excluir(Long identificador) {
        InformacaoUrbana informacao = buscarPorIdentificador(identificador);
        repositorio.delete(informacao);
    }

    // Centraliza a cópia para usar as mesmas regras no cadastro e na atualização.
    private void copiarDados(InformacaoUrbana origem, InformacaoUrbana destino) {
        destino.definirTitulo(origem.obterTitulo().strip());
        destino.definirDescricao(origem.obterDescricao().strip());
        destino.definirCategoria(origem.obterCategoria());
        destino.definirCidade(origem.obterCidade().strip());
        destino.definirBairro(origem.obterBairro().strip());
        destino.definirPropostaMelhoria(origem.obterPropostaMelhoria().strip());
    }
}
