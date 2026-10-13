package ru.academits.hibernatemanual;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.academits.hibernatemanual.entity.Category;
import ru.academits.hibernatemanual.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class HibernateManualApplication implements CommandLineRunner {
    private final EntityManagerFactory entityManagerFactory;

    static void main(String[] args) {
        SpringApplication.run(HibernateManualApplication.class, args);
    }

    @Override
    public void run(String @NonNull ... args) {
        createCategoriesAndProducts();
        updateProduct();
        deleteProduct();
        findProductsWithCategories();
    }

    private void createCategoriesAndProducts() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();

            try {
                transaction.begin();

                Category electronics = new Category("Электроника");
                Category appliance = new Category("Бытовая техника");

                entityManager.persist(electronics);
                entityManager.persist(appliance);

                Product samsung = new Product("Телефон Samsung", new BigDecimal("100000"), electronics);
                Product iphone = new Product("Телефон iPhone", new BigDecimal("150000"), electronics);
                Product nokia = new Product("Телефон Nokia", new BigDecimal("50000"), electronics);
                Product lenovo = new Product("Ноутбук Lenovo", new BigDecimal("100000"), electronics);
                Product asus = new Product("Ноутбук Asus", new BigDecimal("120000"), electronics);

                Product kettle = new Product("Чайник Philips", new BigDecimal("2500"), appliance);
                Product vacuum = new Product("Пылесос Bosch", new BigDecimal("10000"), appliance);
                Product fridge = new Product("Холодильник Bosch", new BigDecimal("50000"), appliance);

                entityManager.persist(samsung);
                entityManager.persist(iphone);
                entityManager.persist(nokia);
                entityManager.persist(lenovo);
                entityManager.persist(asus);

                entityManager.persist(kettle);
                entityManager.persist(vacuum);
                entityManager.persist(fridge);

                transaction.commit();

                System.out.println("Категории и товары созданы.");
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw e;
            }
        }
    }

    private void updateProduct() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();

            try {
                transaction.begin();

                Product product = entityManager.find(Product.class, 1L);

                if (product != null) {
                    product.setPrice(new BigDecimal("90000"));
                }

                transaction.commit();

                if (product != null) {
                    System.out.println("Товар изменен.");
                }
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw e;
            }
        }
    }

    private void deleteProduct() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();

            try {
                transaction.begin();

                Product product = entityManager.find(Product.class, 3L);

                if (product != null) {
                    entityManager.remove(product);
                }

                transaction.commit();

                if (product != null) {
                    System.out.println("Товар удалён.");
                }
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw e;
            }
        }
    }

    private void findProductsWithCategories() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);

            Root<Product> product = query.from(Product.class);
            product.fetch("category");
            query.select(product);

            List<Product> products = entityManager.createQuery(query).getResultList();

            System.out.println("Товары с категориями:");
            products.forEach(System.out::println);
        }
    }
}