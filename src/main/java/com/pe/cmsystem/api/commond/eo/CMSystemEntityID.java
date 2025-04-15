package com.pe.cmsystem.api.commond.eo;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@JsonPropertyOrder({"id", "usuins", "datins", "usumod", "datmod", "flgact"})
public abstract class CMSystemEntityID extends CMSystemEntityLOPD implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", precision = 15)
    private Long id;

    @Override
    public boolean equals(Object obj) {
        return Optional.ofNullable(obj)
                .map(o -> {
                    boolean flg = this == o;
                    if (!flg) {
                        flg = Optional.of(o)
                                .filter(CMSystemEntityID.class::isInstance)
                                .map(u -> (CMSystemEntityID) u)
                                .map(u -> (Boolean) (this.id != null && this.id.equals(u.getId())))
                                .orElse(Boolean.FALSE);
                    }
                    return (Boolean) flg;
                }).orElse(Boolean.FALSE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
