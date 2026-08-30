package com.perfectkode.bikri.common.mapper;

public interface Mapper<E, D> {

    /**
     * Converts an Entity object to a DTO Record.
     */
    D toDto(E entity);

    /**
     * Converts a DTO Record back into an Entity object.
     */
    E toEntity(D dto);
}