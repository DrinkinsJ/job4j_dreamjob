package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Ivan", "Intern description", LocalDateTime.of(2022, 12, 31, 16, 30)));
        save(new Candidate(0, "Konstantin Pavlovich", "Junior description", LocalDateTime.of(2022, 6, 16, 15, 20)));
        save(new Candidate(0, "Pavel Nickolaevich", "Junior+ description", LocalDateTime.of(2021, 11, 30, 14, 10)));
        save(new Candidate(0, "Nikolay Petrov", "Middle description", LocalDateTime.of(2021, 5, 15, 13, 33)));
        save(new Candidate(0, "Petr Borisov", "Middle+ description", LocalDateTime.of(2020, 10, 14, 12, 21)));
        save(new Candidate(0, "Boris Igorevich", "Senior description", LocalDateTime.of(2020, 4, 29, 11, 49)));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
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
                new Candidate(oldCandidate.getId(), candidate.getName(), candidate.getDescription(), candidate.getCreationDate())) != null;
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
