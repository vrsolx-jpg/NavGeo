package com.navgeo.security;

import com.navgeo.entity.Usuario;
import com.navgeo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2UsuarioService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsuarioRepository usuarioRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    @Transactional(readOnly = true)
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User googleUser = delegate.loadUser(userRequest);
        String email = googleUser.getAttribute("email");

        if (email == null || email.isBlank()) {
            throw oauthError("Google no retorno un correo valido.");
        }

        Usuario usuario = usuarioRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> oauthError(
                        "El correo de Google no esta registrado como administrador o editor."));

        if (!usuario.isEnabled()) {
            throw oauthError("El usuario esta desactivado.");
        }

        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();
        boolean tieneRolPanel = authorities.stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())
                        || "ROLE_EDITOR".equals(a.getAuthority()));
        if (!tieneRolPanel) {
            throw oauthError("El usuario existe, pero no tiene rol de administrador o editor.");
        }

        return new DefaultOAuth2User(authorities, googleUser.getAttributes(), "email");
    }

    private OAuth2AuthenticationException oauthError(String message) {
        return new OAuth2AuthenticationException(new OAuth2Error("access_denied"), message);
    }
}
