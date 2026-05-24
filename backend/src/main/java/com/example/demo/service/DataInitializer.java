package com.example.demo.service;

import java.util.List;

import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.CarBrand;
import com.example.demo.models.entities.Category;
import com.example.demo.models.entities.Gerente;
import com.example.demo.models.entities.Product;
import com.example.demo.models.entities.Role;
import com.example.demo.repository.CarBrandRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CarBrandRepository carBrandRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedPartsDatabase();
    }

    private void seedUsers() {
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

        if (userRepository.findByEmail("funcionario@empresa.com").isEmpty()) {
            com.example.demo.models.entities.Funcionario funcionario = new com.example.demo.models.entities.Funcionario();
            funcionario.setName("Funcionario Padrão");
            funcionario.setEmail("funcionario@empresa.com");
            funcionario.setPhone("11988888888");
            funcionario.setPassword(passwordEncoder.encode("senha123"));
            funcionario.setCpf("98765432100");
            funcionario.setRole(Role.FUNCIONARIO);
            userRepository.save(funcionario);
            System.out.println("Funcionario padrão criado: funcionario@empresa.com / senha123");
        }
    }

    private void seedPartsDatabase() {
        if (productRepository.count() > 0 || categoryRepository.count() > 0 || carBrandRepository.count() > 0) {
            return;
        }

        Category freio = new Category();
        freio.setName("Freios");

        Category motor = new Category();
        motor.setName("Motor");

        Category eletrica = new Category();
        eletrica.setName("Elétrica");

        Category suspensao = new Category();
        suspensao.setName("Suspensão");

        categoryRepository.saveAll(List.of(freio, motor, eletrica, suspensao));

        CarBrand fiat = new CarBrand();
        fiat.setName("Fiat");
        CarBrand volkswagen = new CarBrand();
        volkswagen.setName("Volkswagen");
        CarBrand chevrolet = new CarBrand();
        chevrolet.setName("Chevrolet");

        carBrandRepository.saveAll(List.of(fiat, volkswagen, chevrolet));

        Car uno2020 = new Car();
        uno2020.setNome("Uno");
        uno2020.setAno(2020);
        uno2020.setCarBrand(fiat);

        Car palio2018 = new Car();
        palio2018.setNome("Palio");
        palio2018.setAno(2018);
        palio2018.setCarBrand(fiat);

        Car gol2019 = new Car();
        gol2019.setNome("Gol");
        gol2019.setAno(2019);
        gol2019.setCarBrand(volkswagen);

        Car onix2021 = new Car();
        onix2021.setNome("Onix");
        onix2021.setAno(2021);
        onix2021.setCarBrand(chevrolet);

        carRepository.saveAll(List.of(uno2020, palio2018, gol2019, onix2021));

        Product pastilhaFreio = new Product();
        pastilhaFreio.setName("Pastilha de Freio");
        pastilhaFreio.setDescription("Kit de pastilhas de freio para uso em carros populares.");
        pastilhaFreio.setPrice(199.90);
        pastilhaFreio.setStock(50);
        pastilhaFreio.setCategories(List.of(freio));
        pastilhaFreio.setCars(List.of(uno2020, palio2018, gol2019, onix2021));

        Product filtroOleo = new Product();
        filtroOleo.setName("Filtro de Óleo");
        filtroOleo.setDescription("Filtro de óleo de alto desempenho para motores 1.0 e 1.4.");
        filtroOleo.setPrice(79.90);
        filtroOleo.setStock(80);
        filtroOleo.setCategories(List.of(motor));
        filtroOleo.setCars(List.of(uno2020, palio2018, gol2019, onix2021));

        Product bateria = new Product();
        bateria.setName("Bateria Automotiva");
        bateria.setDescription("Bateria 12V com alta durabilidade para uso urbano.");
        bateria.setPrice(399.90);
        bateria.setStock(30);
        bateria.setCategories(List.of(eletrica));
        bateria.setCars(List.of(uno2020, gol2019, onix2021));

        Product amortecedor = new Product();
        amortecedor.setName("Amortecedor");
        amortecedor.setDescription("Amortecedor dianteiro compatível com carros compactos.");
        amortecedor.setPrice(259.90);
        amortecedor.setStock(40);
        amortecedor.setCategories(List.of(suspensao));
        amortecedor.setCars(List.of(uno2020, palio2018, gol2019));

        Product velaIgnição = new Product();
        velaIgnição.setName("Vela de Ignição");
        velaIgnição.setDescription("Vela de ignição de alta qualidade para motores a gasolina.");
        velaIgnição.setPrice(34.90);
        velaIgnição.setStock(120);
        velaIgnição.setCategories(List.of(motor));
        velaIgnição.setCars(List.of(uno2020, palio2018, gol2019, onix2021));

        productRepository.saveAll(List.of(pastilhaFreio, filtroOleo, bateria, amortecedor, velaIgnição));
        System.out.println("Banco populado com peças e categorias iniciais.");
    }
}