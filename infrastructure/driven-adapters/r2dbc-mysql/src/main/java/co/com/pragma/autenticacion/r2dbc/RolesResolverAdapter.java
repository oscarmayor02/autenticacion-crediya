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
        if (user.getIdRole() == null) return Collections.emptyList();
        long rid = user.getIdRole().longValue();
        if (rid == 1L) return List.of(AuthConstants.ADMIN_ROLE);
        if (rid == 2L) return List.of(AuthConstants.ADVISOR_ROLE);
        if (rid == 3L) return List.of(AuthConstants.CLIENT_ROLE);
        return Collections.emptyList();
    }
}