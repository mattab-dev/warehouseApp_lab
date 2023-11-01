package org.warehouse.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.warehouse.model.enums.SupportedMetalType;

@NoArgsConstructor
@Setter
@Getter
public class MetalIngot {
    private SupportedMetalType metalType;
    private double mass;
    private double volume;

    public MetalIngot(final SupportedMetalType metalType, final double mass) {
        this.metalType = metalType;
        this.mass = mass;
        this.volume = mass / metalType.getDensity();
    }
}
