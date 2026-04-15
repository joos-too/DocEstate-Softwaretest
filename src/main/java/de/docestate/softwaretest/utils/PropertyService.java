package de.docestate.softwaretest.utils;

import de.docestate.softwaretest.dto.request.PropertyRequest;
import de.docestate.softwaretest.dto.response.PropertyResponse;
import de.docestate.softwaretest.dto.request.PropertyUpdateRequest;
import de.docestate.softwaretest.domain.Address;
import de.docestate.softwaretest.domain.Property;
import de.docestate.softwaretest.repos.PropertyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Transactional
    public PropertyResponse create(PropertyRequest request) {
        Property savedProperty = propertyRepository.save(PropertyMapper.toEntity(request));
        log.info("Property with ID {} was created.", savedProperty.getId());
        return PropertyMapper.toResponse(savedProperty);
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> findAll() {
        return propertyRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(PropertyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PropertyResponse findById(Long id) {
        return PropertyMapper.toResponse(getPropertyById(id));
    }

    @Transactional
    public PropertyResponse update(Long id, PropertyUpdateRequest request) {
        Property property = getPropertyById(id);
        Address mergedAddress = PropertyMapper.mergeAddress(property.getAddress(), request.address());
        String mergedName = request.name() != null ? request.name() : property.getName();
        var mergedObjectType = request.objectType() != null ? request.objectType() : property.getObjectType();
        String mergedConstructionYear = request.constructionYear() != null
                ? request.constructionYear()
                : property.getConstructionYear();
        Float mergedLotSize = request.lotSize() != null ? request.lotSize() : property.getLotSize();
        Float mergedLivingSpace = request.livingSpace() != null ? request.livingSpace() : property.getLivingSpace();

        property.update(
                mergedName,
                mergedObjectType,
                mergedConstructionYear,
                mergedLotSize,
                mergedLivingSpace,
                mergedAddress
        );
        log.info("Property with ID {} was updated.", id);
        return PropertyMapper.toResponse(property);
    }

    @Transactional
    public void delete(Long id) {
        Property property = getPropertyById(id);
        propertyRepository.delete(property);
        log.info("Property with ID {} was deleted.", id);
    }

    private Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException(id));
    }
}
