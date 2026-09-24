package de.szut.lf8_starter.hello;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class HelloService {
    private final HelloRepository repository;
    public HelloService(HelloRepository repository) { this.repository = repository; }
    public HelloEntity create(HelloEntity entity) { return repository.save(entity); }
    public List<HelloEntity> findAll() { return repository.findAll(); }
    public List<HelloEntity> findByMessage(String message) { return repository.findByMessage(message); }
    public void delete(long id) {
        HelloEntity entity = repository.findById(id).orElseThrow(() -> new HelloNotFoundException(id));
        repository.delete(entity);
    }
}
