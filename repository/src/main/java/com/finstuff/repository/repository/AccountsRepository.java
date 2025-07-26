package com.finstuff.repository.repository;

import com.finstuff.repository.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Account, String> {

    @Modifying
    @Query("UPDATE Account a SET a.title = :newValue WHERE a.id = :id")
    void updateTitle(@Param("id") String id, @Param("newValue") String newValue);

    Optional<List<Account>> findByOwnedByUserId(String ownedByUserId);
}
