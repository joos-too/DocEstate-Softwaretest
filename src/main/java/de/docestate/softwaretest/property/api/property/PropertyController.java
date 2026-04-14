package de.docestate.softwaretest.property.api.property;

import de.docestate.softwaretest.property.service.PropertyService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/immobilien")
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse create(@Valid @RequestBody PropertyRequest request) {
        return propertyService.create(request);
    }

    @GetMapping
    public List<PropertyResponse> findAll() {
        return propertyService.findAll();
    }

    @GetMapping("/{id}")
    public PropertyResponse findById(@PathVariable Long id) {
        return propertyService.findById(id);
    }

    @PutMapping("/{id}")
    public PropertyResponse update(@PathVariable Long id, @Valid @RequestBody PropertyUpdateRequest request) {
        return propertyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        propertyService.delete(id);
    }
}
