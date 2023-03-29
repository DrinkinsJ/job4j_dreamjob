package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Ivan", "Intern description", LocalDateTime.of(2022, 12, 31, 16, 30), 1, 0));
        save(new Candidate(0, "Konstantin Pavlovich", "Junior description", LocalDateTime.of(2022, 6, 16, 15, 20), 2, 0));
        save(new Candidate(0, "Pavel Nickolaevich", "Junior+ description", LocalDateTime.of(2021, 11, 30, 14, 10), 3, 0));
        save(new Candidate(0, "Nikolay Petrov", "Middle description", LocalDateTime.of(2021, 5, 15, 13, 33), 1, 0));
        save(new Candidate(0, "Petr Borisov", "Middle+ description", LocalDateTime.of(2020, 10, 14, 12, 21), 2, 0));
        save(new Candidate(0, "Boris Igorevich", "Senior description", LocalDateTime.of(2020, 4, 29, 11, 49), 3, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) ->
                new Candidate(oldCandidate.getId(), candidate.getName(), candidate.getDescription(), candidate.getCreationDate(), candidate.getCityId(), candidate.getFileId())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
