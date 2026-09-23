package br.com.reconecta.repository;

import br.com.reconecta.model.Categoria;
import br.com.reconecta.model.InformacaoUrbana;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioInformacao extends JpaRepository<InformacaoUrbana, Long> {
    // A consulta usa o nome da classe e seus atributos, não os nomes da tabela.
    @Query("SELECT informacao FROM InformacaoUrbana informacao WHERE informacao.categoria = :categoria ORDER BY informacao.identificador")
    List<InformacaoUrbana> listarPorCategoria(@Param("categoria") Categoria categoria);
}
