package com.lenda.challenge.repository.postgres;

import com.lenda.challenge.model.postgres.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
}
