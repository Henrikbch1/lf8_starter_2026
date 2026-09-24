package de.szut.lf8_starter.hello;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController @RequestMapping("/hello")
public class HelloController {
    private final HelloService service;
    private final HelloMapper mapper;
    private final EmployeeClient employees;
    public HelloController(HelloService service, HelloMapper mapper, EmployeeClient employees) {
        this.service = service; this.mapper = mapper; this.employees = employees;
    }

    @Operation(summary="Hello anlegen")
    @ApiResponses({@ApiResponse(responseCode="201"), @ApiResponse(responseCode="400"), @ApiResponse(responseCode="401")})
    @PostMapping public ResponseEntity<HelloGetDto> create(@Valid @RequestBody HelloCreateDto dto) {
        var created = mapper.toDto(service.create(mapper.fromCreate(dto)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary="Alle Hellos abrufen; optional nach Nachricht filtern")
    @ApiResponses({@ApiResponse(responseCode="200"), @ApiResponse(responseCode="401")})
    @GetMapping public List<HelloGetDto> all(@RequestParam(required=false) String message) {
        return (message == null ? service.findAll() : service.findByMessage(message)).stream().map(mapper::toDto).toList();
    }

    @Operation(summary="Hellos nach Nachricht suchen")
    @ApiResponses({@ApiResponse(responseCode="200"), @ApiResponse(responseCode="401")})
    @GetMapping("/findByMessage") public List<HelloGetDto> findByMessage(@RequestParam String message) {
        return service.findByMessage(message).stream().map(mapper::toDto).toList();
    }

    @Operation(summary="Hello löschen")
    @ApiResponses({@ApiResponse(responseCode="204"), @ApiResponse(responseCode="401"), @ApiResponse(responseCode="404")})
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }

    @Operation(summary="Mitarbeiter mit Namen begrüßen")
    @ApiResponses({@ApiResponse(responseCode="200"), @ApiResponse(responseCode="401"), @ApiResponse(responseCode="404"), @ApiResponse(responseCode="503")})
    @GetMapping("/greeting/{employeeId}") public Map<String, String> greeting(@PathVariable long employeeId) {
        var employee = employees.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return Map.of("message", "Hallo " + employee.firstName() + " " + employee.lastName());
    }
}
