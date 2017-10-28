package se.omegapoint.reverse.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.omegapoint.reverse.model.ReverseDatum;
import se.omegapoint.reverse.model.ReversedData;
import se.omegapoint.reverse.repositories.ReverseRepository;

@Service
public class ReverseService {

    private final ReverseRepository reverseRepository;

    @Autowired
    public ReverseService(ReverseRepository reverseRepository) {
        this.reverseRepository = reverseRepository;
    }

    public ReversedData reverse(final String data) {
        try {
            reverseRepository.save(ReverseDatum.builder()
                    .withData(data)
                    .withReversedData(new StringBuilder(data).reverse().toString())
                    .build());
        } catch(Exception ignored) {}

        final ReverseDatum datum = reverseRepository.findByData(data);

        return new ReversedData(datum.reversedData);
    }
}
