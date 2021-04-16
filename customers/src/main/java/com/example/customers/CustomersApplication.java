package com.example.customers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }

}

@RestController
@RequiredArgsConstructor
class CustomerRestController {

    private final CustomerRepository customerRepository;

    @GetMapping("/customers")
    Flux<Customer> get() {
        return this.customerRepository.findAll();
    }
}

@Component
@RequiredArgsConstructor
class Initializer implements CommandLineRunner {

    private final CustomerRepository repository;

    private final DatabaseClient dbc;

    @Override
    public void run(String... args) throws Exception {

        var ddl = dbc.sql("create table customer(id serial primary  key, name varchar (255) not null)").fetch().rowsUpdated();

        var names = Flux
                .just("Josh", "Yuxin", "Stéphane", "Oleg",
                        "Violetta", "Madhura", "Dr. Syer")
                .map(name -> new Customer(null, name))
                .flatMap(this.repository::save);

        var all = repository.findAll();

        ddl.thenMany(names).thenMany(all).subscribe(System.out::println);


    }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

    @Id
    private Integer id;

    private String name;
}