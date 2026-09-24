package de.szut.lf8_starter.hello;
import org.springframework.stereotype.Component;
@Component
public class HelloMapper {
    public HelloEntity fromCreate(HelloCreateDto dto) { return new HelloEntity(dto.message()); }
    public HelloGetDto toDto(HelloEntity entity) { return new HelloGetDto(entity.getId(), entity.getMessage()); }
}
