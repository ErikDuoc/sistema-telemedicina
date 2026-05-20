package cl.duoc.fullstack.paymentservice.repository;

import cl.duoc.fullstack.paymentservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
