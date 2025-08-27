package co.com.pragma.autenticacion.r2dbc;


import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.usecase.auth.AuthUseCase.RolesResolver;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RolesResolverAdapter implements RolesResolver {
    @Override
    public List<String> resolve(User user) {
        if (user.getIdRol() == null) return Collections.emptyList();
        long rid = user.getIdRol().longValue();
        if (rid == 1L) return List.of(AuthConstants.ROLE_ADMIN);
        if (rid == 2L) return List.of(AuthConstants.ROLE_ASESOR);
        if (rid == 3L) return List.of(AuthConstants.ROLE_CLIENTE);
        return Collections.emptyList();
    }
}