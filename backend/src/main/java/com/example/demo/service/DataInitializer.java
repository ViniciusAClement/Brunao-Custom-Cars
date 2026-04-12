package com.example.demo.service;

import com.example.demo.models.entities.Gerente;
import com.example.demo.models.entities.Role;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("gerente@empresa.com").isEmpty()) {
            Gerente gerente = new Gerente();
            gerente.setName("Gerente Padrão");
            gerente.setEmail("gerente@empresa.com");
            gerente.setPhone("11999999999");
            gerente.setPassword(passwordEncoder.encode("senha123"));
            gerente.setCpf("12345678901");
            gerente.setRole(Role.GERENTE);
            userRepository.save(gerente);
            System.out.println("Gerente padrão criado: gerente@empresa.com / senha123");
        }
    }
}