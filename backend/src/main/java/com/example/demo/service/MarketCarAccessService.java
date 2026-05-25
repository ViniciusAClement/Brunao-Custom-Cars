package com.example.demo.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.models.entities.Address;
import com.example.demo.models.entities.MarketCar;
import com.example.demo.models.entities.MarketCarItem;
import com.example.demo.models.entities.Role;
import com.example.demo.models.entities.User;

@Service
public class MarketCarAccessService {

    public User requireAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("Access denied");
        }
        return user;
    }

    public boolean isStaff(User user) {
        return user.getRole() == Role.GERENTE || user.getRole() == Role.FUNCIONARIO;
    }

    public Long resolveClientId(Long requestedClientId, Authentication authentication) {
        User user = requireAuthenticatedUser(authentication);
        if (user.getRole() == Role.CLIENTE) {
            return user.getId();
        }
        if (requestedClientId == null) {
            throw new IllegalArgumentException("ClientId cannot be null");
        }
        return requestedClientId;
    }

    public void ensureCanAccessClientId(Long clientId, Authentication authentication) {
        User user = requireAuthenticatedUser(authentication);
        if (user.getRole() == Role.CLIENTE && !user.getId().equals(clientId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public void ensureCanAccessMarketCar(MarketCar marketCar, Authentication authentication) {
        if (marketCar == null || marketCar.getClient() == null) {
            throw new IllegalArgumentException("Market car has no associated client");
        }
        ensureCanAccessClientId(marketCar.getClient().getId(), authentication);
    }

    public void ensureCanAccessMarketCarItem(MarketCarItem item, Authentication authentication) {
        if (item == null) {
            throw new IllegalArgumentException("MarketCarItem not found");
        }
        MarketCar marketCar = item.getMarketCar();
        if (marketCar == null) {
            throw new IllegalArgumentException("Market car item has no associated cart");
        }
        ensureCanAccessMarketCar(marketCar, authentication);
    }

    public void ensureCanListAllCarts(Authentication authentication) {
        ensureStaffOnly(authentication);
    }

    public void ensureStaffOnly(Authentication authentication) {
        User user = requireAuthenticatedUser(authentication);
        if (!isStaff(user)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public void ensureGerenteOnly(Authentication authentication) {
        User user = requireAuthenticatedUser(authentication);
        if (user.getRole() != Role.GERENTE) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public void ensureCanAccessAddress(Address address, Authentication authentication) {
        if (address == null || address.getClient() == null) {
            throw new IllegalArgumentException("Address has no associated client");
        }
        ensureCanAccessClientId(address.getClient().getId(), authentication);
    }
}
