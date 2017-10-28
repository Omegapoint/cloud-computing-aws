package se.omegapoint.reverse.repositories;



import org.springframework.data.repository.CrudRepository;
import se.omegapoint.reverse.model.ReverseDatum;
import se.omegapoint.reverse.model.ReversedData;


public interface ReverseRepository extends CrudRepository<ReverseDatum, Long> {
    ReverseDatum findByData(String data);
}
