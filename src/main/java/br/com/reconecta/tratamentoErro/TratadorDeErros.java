package br.com.reconecta.tratamentoErro;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarCamposInvalidos(MethodArgumentNotValidException excecao) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Revise os campos enviados.");
        resposta.put("campos", campos);
        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarInformacaoNaoEncontrada(ResponseStatusException excecao) {
        return ResponseEntity.status(excecao.getStatusCode())
                .body(Map.of("mensagem", excecao.getReason()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> tratarCorpoInvalido(HttpMessageNotReadableException excecao) {
        return ResponseEntity.badRequest().body(Map.of("mensagem",
                "JSON inválido. Confira os campos e use uma categoria válida, conforme o Swagger."));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HandlerMethodValidationException.class})
    public ResponseEntity<Map<String, String>> tratarParametroInvalido(Exception excecao) {
        return ResponseEntity.badRequest().body(Map.of("mensagem",
                "Parâmetro inválido. Use um identificador inteiro positivo e uma categoria válida."));
    }
}
