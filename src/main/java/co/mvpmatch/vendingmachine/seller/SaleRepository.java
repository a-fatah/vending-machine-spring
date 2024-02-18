package co.mvpmatch.vendingmachine.seller;

import org.springframework.data.jpa.repository.JpaRepository;

interface SaleRepository extends JpaRepository<Sale, Long> {
}
