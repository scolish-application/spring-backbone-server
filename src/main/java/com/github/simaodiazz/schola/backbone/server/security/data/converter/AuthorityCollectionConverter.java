package com.github.simaodiazz.schola.backbone.server.security.data.converter;

import com.github.simaodiazz.schola.backbone.server.security.data.converter.util.CollectionConcatenationRender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

@Converter
public final class AuthorityCollectionConverter
        implements AttributeConverter<Collection<? extends GrantedAuthority>, String> {

    @Override
    public String convertToDatabaseColumn(@NotNull Collection<? extends GrantedAuthority> authorities) {
        final Collection<String> names = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        return CollectionConcatenationRender.options().separator(',').collection(names).standard("ROLE_DEFAULT").render();
    }

    @Override
    public Collection<? extends GrantedAuthority> convertToEntityAttribute(@NotNull String s) {
        final String[] parts = s.split(",");
        return Arrays
                .stream(parts)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
