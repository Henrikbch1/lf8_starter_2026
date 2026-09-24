package de.szut.lf8_starter.hello;

import java.util.List;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private final HelloRepository repository;
    private final EmployeeClient employees;

    public HelloService(HelloRepository repository, EmployeeClient employees) {
        this.repository = repository;
        this.employees = employees;
    }

    public HelloEntity create(HelloEntity entity) {
        return repository.save(entity);
    }

    public List<HelloEntity> findAll() {
        return repository.findAll();
    }

    public List<HelloEntity> findByMessage(String message) {
        return repository.findByMessage(message);
    }

    public HelloEntity findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new HelloNotFoundException(id));
    }

    public GreetingDto greet(long employeeId) {
        var employee = employees.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return new GreetingDto("Hallo " + employee.firstName() + " " + employee.lastName());
    }

    public void delete(long id) {
        HelloEntity entity = findById(id);
        repository.delete(entity);
    }
}
